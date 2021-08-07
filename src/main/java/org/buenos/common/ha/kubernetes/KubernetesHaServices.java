package org.buenos.common.ha.kubernetes;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;

import org.buenos.common.ha.Contender;
import org.buenos.common.ha.HighAvailabilityServices;
import org.buenos.common.ha.LeaderElectionDriver;
import org.buenos.common.ha.LeaderElectionService;
import org.buenos.common.ha.LeaderRetrievalDriver;
import org.buenos.common.ha.LeaderRetrievalService;
import org.buenos.common.ha.PodInformation;
import org.buenos.common.ha.config.KubernetesHaConfig;
import org.buenos.common.ha.kubernetes.driver.KubernetesLeaderElectionDriver;
import org.buenos.common.ha.kubernetes.driver.KubernetesLeaderRetrievalDriver;

public class KubernetesHaServices implements HighAvailabilityServices {

    private final KubernetesHaConfig haConf;

    private final NamespacedKubernetesClient kubeClient;

    private LeaderRetrievalService leaderRetrievalService;

    private LeaderElectionService leaderElectionService;

    private LeaderRetrievalDriver retrievalDriver;

    private LeaderElectionDriver electionDriver;

    public KubernetesHaServices(KubernetesHaConfig haConf) {
        this.haConf = haConf;
        this.kubeClient = buildKubeClient();
    }

    @Override
    public void start(Contender contender, boolean needsElect) {
        // init retrieve driver
        retrievalDriver = new KubernetesLeaderRetrievalDriver(contender);
        // start retrieve leader
        leaderRetrievalService = createLeaderRetrievalService();
        leaderRetrievalService.start(retrievalDriver);
        if (needsElect) {
            electionDriver = new KubernetesLeaderElectionDriver(haConf, kubeClient, contender);
            // start elect leader
            leaderElectionService = createLeaderElectionService();
            leaderElectionService.start(electionDriver);
        }
    }

    @Override
    public void stop() {
        if (leaderRetrievalService != null) {
            leaderRetrievalService.stop();
            leaderRetrievalService = null;
        }
        if (leaderElectionService != null) {
            leaderElectionService.stop();
            leaderElectionService = null;
        }
        kubeClient.close();
    }

    @Override
    public boolean isLeading() {
        if (electionDriver != null) {
            return electionDriver.isLeading();
        }
        return false;
    }

    @Override
    public PodInformation getLeaderInformation() {
        if (retrievalDriver != null) {
            return retrievalDriver.getLeaderInformation();
        }
        return null;
    }

    private LeaderRetrievalService createLeaderRetrievalService() {
        return new KubernetesLeaderRetrievalService(haConf, kubeClient);
    }

    private LeaderElectionService createLeaderElectionService() {
        return new KubernetesLeaderElectionService(haConf, kubeClient);
    }

    private NamespacedKubernetesClient buildKubeClient() {
        Config config = new ConfigBuilder().build();
        return new DefaultKubernetesClient(config).inNamespace(haConf.getNamespace());
    }

}
