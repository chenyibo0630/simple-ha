package org.buenos.common.ha.config;

import java.time.Duration;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(setterPrefix = "with")
public class KubernetesHaConfig extends HaConfig {

    @Builder.Default
    private final String namespace = "default";

    @Builder.Default
    private final Duration leaseDuration = Duration.ofSeconds(15);

    @Builder.Default
    private final Duration renewDeadline = Duration.ofSeconds(15);

    @Builder.Default
    private final Duration retryPeriod = Duration.ofSeconds(5);

    private final String masterUrl;

}
