/*package com.personal.web.ucc;

import com.google.common.collect.Lists;
import com.personal.spring.ConfigPropertySourcesProcessor;
import com.personal.ucc.UccConfigPropertySourcesProcessor;
import com.personal.ucc.UccNamespace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

*//**
 * @author zhuangqianliao
 *//*
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
                new UccNamespace("jss-config", "WldPHiy9", true)
        );
    }

    private List<UccNamespace> registryUccSecurityConfig() {
        return Lists.newArrayList(
                new UccNamespace("important", "", false)
        );
    }
}*/
