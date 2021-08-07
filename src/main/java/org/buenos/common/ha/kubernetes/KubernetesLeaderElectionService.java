package org.buenos.common.ha.kubernetes;

import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderCallbacks;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfigBuilder;
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElector;
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.ConfigMapLock;
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.Lock;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

import org.buenos.common.ha.LeaderElectionDriver;
import org.buenos.common.ha.LeaderElectionService;
import org.buenos.common.ha.config.KubernetesHaConfig;

@Slf4j
public class KubernetesLeaderElectionService implements LeaderElectionService {

    private final KubernetesHaConfig haConf;

    private final NamespacedKubernetesClient kubeClient;

    private final String lockIdentity = UUID.randomUUID().toString();

    private final Object electLock = new Object();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public KubernetesLeaderElectionService(KubernetesHaConfig haConf, NamespacedKubernetesClient kubeClient) {
        this.haConf = haConf;
        this.kubeClient = kubeClient;
    }

    @Override
    public void start(LeaderElectionDriver leaderElectionDriver) {
        String configMapName = haConf.getClusterId();
        Lock lock = new ConfigMapLock(haConf.getNamespace(), configMapName, lockIdentity);
        LeaderElectionConfig electionConfig = new LeaderElectionConfigBuilder()
                .withName(configMapName)
                .withLock(lock)
                .withLeaseDuration(Duration.ofSeconds(15))
                .withRenewDeadline(Duration.ofSeconds(15))
                .withRetryPeriod(Duration.ofSeconds(5))
                .withLeaderCallbacks(new LeaderCallbacks(
                        leaderElectionDriver::startLeading,
                        leaderElectionDriver::stopLeading,
                        newLeader -> log.info("New leader elected {} for {}.", newLeader, configMapName)
                ))
                .withReleaseOnCancel(true)
                .build();
        LeaderElector<NamespacedKubernetesClient> leaderElector = new LeaderElector<>(kubeClient, electionConfig);
        synchronized (electLock) {
            if (executorService.isShutdown()) {
                log.debug("Ignoring LeaderElector.run call because the leader elector has already been shut down.");
            } else {
                executorService.execute(leaderElector::run);
            }
        }
    }

    @Override
    public void stop() {
        synchronized (electLock) {
            executorService.shutdownNow();
        }
        kubeClient.close();
    }

}
