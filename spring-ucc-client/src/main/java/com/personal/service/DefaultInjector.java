package com.personal.service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Singleton;
import com.personal.config.*;
import com.personal.exception.ConfigException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class DefaultInjector implements Injector {

    private com.google.inject.Injector injector;

    public DefaultInjector() {
        try {
            injector = Guice.createInjector(new InjectorModule());
        } catch (Throwable ex) {
            log.error("Unable to initialize Guice Injector!", ex);
            throw new ConfigException("Unable to initialize Guice Injector!");
        }
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        try {
            return injector.getInstance(clazz);
        } catch (Throwable ex) {
            log.error("Unable to load instance for {}!", clazz.getName(), ex);
            throw new ConfigException(
                    String.format("Unable to load instance for %s!", clazz.getName()), ex);
        }
    }

    private class InjectorModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(ConfigManager.class).to(DefaultConfigManager.class).in(Singleton.class);
            bind(ConfigFactoryManager.class).to(DefaultConfigFactoryManager.class).in(Singleton.class);
            bind(ConfigRegistry.class).to(DefaultConfigRegistry.class).in(Singleton.class);
        }
    }
}
