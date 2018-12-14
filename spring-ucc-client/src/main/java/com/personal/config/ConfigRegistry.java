package com.personal.config;

/**
 * @author zhuangqianliao
 */
public interface ConfigRegistry {

    void registry(String namespace, NamespaceHolder holder);

    void registry(String namespace, NamespaceHolder holder, ConfigFactory configFactory);

    ConfigFactory getConfigFactory(String namespace);

    NamespaceHolder getNamespaceHolder(String namespace);
}
