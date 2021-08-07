package org.buenos.common.ha.kubernetes.driver;

import io.fabric8.kubernetes.client.NamespacedKubernetesClient;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.buenos.common.ha.Contender;
import org.buenos.common.ha.HaConstants;
import org.buenos.common.ha.LeaderElectionDriver;
import org.buenos.common.ha.config.KubernetesHaConfig;

@Slf4j
public class KubernetesLeaderElectionDriver implements LeaderElectionDriver {

    private final KubernetesHaConfig haConf;

    private final NamespacedKubernetesClient kubeClient;

    private final Contender leaderContender;

    private volatile boolean isLeading;

    public KubernetesLeaderElectionDriver(KubernetesHaConfig haConf, NamespacedKubernetesClient kubeClient, Contender leaderContender) {
        this.haConf = haConf;
        this.kubeClient = kubeClient;
        this.leaderContender = leaderContender;
    }

    @Override
    public void startLeading() {
        log.info("start leading");
        isLeading = true;
        leaderContender.grantLeadership();
        // put leader information into config map
        kubeClient.configMaps().withName(haConf.getClusterId()).accept(cm -> {
            Map<String, String> data = new HashMap<>();
            data.put(HaConstants.LEADER_NAME_KEY, haConf.getServerName());
            data.put(HaConstants.LEADER_ADDRESS_KEY, haConf.getServerAddress());
            cm.setData(data);
        });
    }

    @Override
    public void stopLeading() {
        log.info("stop leading");
        isLeading = false;
        leaderContender.revokeLeadership();
    }

    @Override
    public boolean isLeading() {
        return isLeading;
    }

}
