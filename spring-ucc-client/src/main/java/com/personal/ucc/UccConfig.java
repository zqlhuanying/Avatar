package com.personal.ucc;

import com.personal.config.ConfigRepository;
import com.personal.config.DefaultConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class UccConfig extends DefaultConfig {

    public UccConfig(String namespace, ConfigRepository configRepository) {
        super(namespace, configRepository);
    }
}
