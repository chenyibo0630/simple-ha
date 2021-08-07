package org.buenos.common.ha;

public interface Contender {

    void leaderChange(PodInformation newPod);

    void handleError(Exception exception);

    void grantLeadership();

    void revokeLeadership();

}
