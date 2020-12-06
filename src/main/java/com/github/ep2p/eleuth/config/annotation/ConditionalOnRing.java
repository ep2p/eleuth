package com.github.ep2p.eleuth.config.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(prefix = "config", name = "nodeType", havingValue = "RING")
public @interface ConditionalOnRing {
}
