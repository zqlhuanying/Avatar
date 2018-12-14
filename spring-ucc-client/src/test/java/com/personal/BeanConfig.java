package com.personal;

import com.google.common.collect.Lists;
import com.personal.spring.ConfigPropertySourcesProcessor;
import com.personal.ucc.UccConfigPropertySourcesProcessor;
import com.personal.ucc.UccNamespace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zhuangqianliao
 */
@Configuration
public class BeanConfig {

    @Bean
    public ConfigPropertySourcesProcessor propertySourcesProcessor() {
        UccConfigPropertySourcesProcessor processor = new UccConfigPropertySourcesProcessor();
        registryUccConfig().forEach(holder -> processor.registryNamespace(holder.getNamespace(), holder));
        registryUccSecurityConfig().forEach(holder -> processor.registrySecNamespace(holder.getNamespace(), holder));
        return processor;
    }

    private List<UccNamespace> registryUccConfig() {
        return Lists.newArrayList(
                new UccNamespace("application", "MWZ6tUZe", true),
                new UccNamespace("jmq", "4ZPz3NoW", true),
                new UccNamespace("jss-config", "WldPHiy9", true),
                new UccNamespace("config", "CMrYPPEk", true),
                new UccNamespace("ValidationMessages", "qIKIYOTM", true),
                new UccNamespace("velocity", "ynUmVPlO", false)
        );
    }

    private List<UccNamespace> registryUccSecurityConfig() {
        return Lists.newArrayList(
                new UccNamespace("important", "", false)
        );
    }
}
