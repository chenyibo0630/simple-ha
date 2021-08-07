package org.buenos.common.ha.kubernetes;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

import lombok.extern.slf4j.Slf4j;

import org.buenos.common.ha.HaConstants;
import org.buenos.common.ha.LeaderRetrievalDriver;
import org.buenos.common.ha.LeaderRetrievalService;
import org.buenos.common.ha.PodInformation;
import org.buenos.common.ha.config.KubernetesHaConfig;
import org.buenos.common.ha.exception.HighAvailabilityException;

@Slf4j
public class KubernetesLeaderRetrievalService implements LeaderRetrievalService {

    private final KubernetesHaConfig haConf;

    private final NamespacedKubernetesClient kubeClient;

    private Watch currentWatch;

    public KubernetesLeaderRetrievalService(KubernetesHaConfig haConf, NamespacedKubernetesClient kubeClient) {
        this.haConf = haConf;
        this.kubeClient = kubeClient;
    }

    @Override
    public void start(LeaderRetrievalDriver retrievalDriver) {
        log.info("start leader retrieval service");
        String configMapName = haConf.getClusterId();
        currentWatch = kubeClient.configMaps().withName(configMapName).watch(new Watcher<ConfigMap>() {
            @Override
            public void eventReceived(Action action, ConfigMap configMap) {
                switch (action) {
                    case ADDED:
                    case MODIFIED: {
                        if (configMap == null || configMap.getData() == null) {
                            return;
                        }
                        String name = configMap.getData().get(HaConstants.LEADER_NAME_KEY);
                        String address = configMap.getData().get(HaConstants.LEADER_ADDRESS_KEY);
                        if (name == null || address == null) {
                            return;
                        }
                        PodInformation newLeader = new PodInformation(name, address);
                        retrievalDriver.retrieveUpdate(newLeader);
                        break;
                    }
                    case DELETED:
                        retrievalDriver.retrieveDelete();
                        break;
                    case ERROR:
                        retrievalDriver.retrieveError(new HighAvailabilityException("Error while watching the configMap " + configMapName));
                        break;
                    default:
                        log.debug("Ignore handling {} event for configMap {}", action, configMapName);
                        break;
                }
            }

            @Override
            public void onClose(WatcherException e) {

            }
        });
    }

    @Override
    public void stop() {
        log.info("stop leader retrieval service");
        if (currentWatch != null) {
            currentWatch.close();
            currentWatch = null;
        }
    }

}
