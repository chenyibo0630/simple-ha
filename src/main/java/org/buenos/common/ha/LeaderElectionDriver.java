package org.buenos.common.ha;

public interface LeaderElectionDriver {

    void startLeading();

    void stopLeading();

    boolean isLeading();

}
