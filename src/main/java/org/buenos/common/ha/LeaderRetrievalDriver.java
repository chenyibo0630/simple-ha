package org.buenos.common.ha;

public interface LeaderRetrievalDriver {

    void retrieveUpdate(PodInformation newLeader);

    void retrieveDelete();

    void retrieveError(Exception exp);

    PodInformation getLeaderInformation();

}
