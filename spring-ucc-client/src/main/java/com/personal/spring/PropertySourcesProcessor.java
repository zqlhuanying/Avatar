package com.personal.spring;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.personal.config.Config;
import com.personal.config.ConfigFactory;
import com.personal.config.NamespaceHolder;
import com.personal.service.ConfigService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author zhuangqianliao
 * Imitate the Ctrip's Apollo basiclly
 * So you can just see Ctrip's Apollo instead
 */
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    private static final String REMOTE_PROPERTY_SOURCE_NAME = "remotePropertySources";
    private static final List<String> NAMESPACES = Lists.newCopyOnWriteArrayList();

    private ConfigurableEnvironment environment;

    public static void registryNamespace(String namespace, NamespaceHolder holder) {
        ConfigService.registryNamespace(namespace, holder);
        NAMESPACES.add(namespace);
    }

    public static void registryNamespace(String namespace, NamespaceHolder holder, ConfigFactory factory) {
        ConfigService.registryNamespace(namespace, holder, factory);
        NAMESPACES.add(namespace);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (environment.getPropertySources().contains(REMOTE_PROPERTY_SOURCE_NAME)) {
            //already initialized
            return;
        }
        CompositePropertySource remotePropertySource = new CompositePropertySource(REMOTE_PROPERTY_SOURCE_NAME);
        for (String namespace : ImmutableSet.copyOf(NAMESPACES)) {
            Config config = ConfigService.getConfig(namespace);
            remotePropertySource.addPropertySource(new ConfigPropertySource(namespace, config));
        }
        environment.getPropertySources().addFirst(remotePropertySource);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
