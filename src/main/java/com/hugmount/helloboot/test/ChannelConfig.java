package com.hugmount.helloboot.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author: lhm
 * @date: 2023/3/22
 */

@Data
@Component
@ConfigurationProperties("pay")
public class ChannelConfig {
    private Set<String> channel;
}
