package com.personal.ucc;

import com.personal.config.ConfigFactory;
import com.personal.spring.ConfigPropertySourcesProcessor;
import com.personal.spring.PropertySourcesProcessor;

/**
 * @author zhuangqianliao
 */
public class UccConfigPropertySourcesProcessor extends ConfigPropertySourcesProcessor {

    private ConfigFactory configFactory;
    private ConfigFactory securityConfigFactory;

    public UccConfigPropertySourcesProcessor() {
        this.configFactory = new UccConfigFactory();
        this.securityConfigFactory = new UccSecurityConfigFactory();
    }

    public void registryNamespace(String namespace, UccNamespaceHolder holder) {
        PropertySourcesProcessor.registryNamespace(namespace, holder, this.configFactory);
    }

    public void registrySecNamespace(String namespace, UccNamespaceHolder holder) {
        PropertySourcesProcessor.registryNamespace(namespace, holder, this.securityConfigFactory);
    }
}
