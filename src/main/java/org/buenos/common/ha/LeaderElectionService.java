package org.buenos.common.ha;

public interface LeaderElectionService {

    void start(LeaderElectionDriver leaderElectionDriver);

    void stop();

}
