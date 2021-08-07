package org.buenos.common.ha.impl;

import org.buenos.common.ha.Contender;
import org.buenos.common.ha.PodInformation;

public class DefaultContender implements Contender {

    @Override
    public void leaderChange(PodInformation newPod) {
    }

    @Override
    public void handleError(Exception exception) {
    }

    @Override
    public void grantLeadership() {
    }

    @Override
    public void revokeLeadership() {
    }
}
