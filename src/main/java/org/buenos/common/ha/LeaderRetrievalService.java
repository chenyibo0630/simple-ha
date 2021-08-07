package org.buenos.common.ha;

public interface LeaderRetrievalService {

    void start(LeaderRetrievalDriver retrievalDriver);

    void stop();

}
