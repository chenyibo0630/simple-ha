package org.buenos.common.ha.config;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(setterPrefix = "with")
public abstract class HaConfig {

    private final String clusterId;

    private final String serverName;

    private final String serverAddress;

}
