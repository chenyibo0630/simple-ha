package org.buenos.common.ha;

public interface HighAvailabilityServices {

    void start(Contender contender, boolean needsElect);

    void stop();

    boolean isLeading();

    PodInformation getLeaderInformation();

}
