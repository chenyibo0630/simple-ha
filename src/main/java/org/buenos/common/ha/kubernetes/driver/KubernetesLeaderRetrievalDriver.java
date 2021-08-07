package org.buenos.common.ha.kubernetes.driver;

import lombok.extern.slf4j.Slf4j;

import org.buenos.common.ha.Contender;
import org.buenos.common.ha.LeaderRetrievalDriver;
import org.buenos.common.ha.PodInformation;

@Slf4j
public class KubernetesLeaderRetrievalDriver implements LeaderRetrievalDriver {

    private final Contender contender;

    private volatile PodInformation leaderInformation;

    public KubernetesLeaderRetrievalDriver(Contender contender) {
        this.contender = contender;
    }

    public void retrieveUpdate(PodInformation newLeader) {
        synchronized (this) {
            if (leaderInformation == null || !leaderInformation.equals(newLeader)) {
                log.info("retrieve new leader, {}", newLeader);
                contender.leaderChange(newLeader);
                this.leaderInformation = newLeader;
            }
        }
    }

    public void retrieveDelete() {
        log.error("retrieve ConfigMap deleted");
    }

    public void retrieveError(Exception exp) {
        log.error("retrieve error", exp);
        contender.handleError(exp);
    }

    @Override
    public PodInformation getLeaderInformation() {
        return leaderInformation;
    }

}
