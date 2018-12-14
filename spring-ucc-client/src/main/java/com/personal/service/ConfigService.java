package com.personal.service;


import com.personal.config.*;
import com.personal.constants.Constant;

import java.util.function.Supplier;

/**
 * @author zhuangqianliao
 * Imitate the Ctrip's Apollo basiclly
 * So you can just see Ctrip's Apollo instead
 */
public class ConfigService {
    private static final ConfigService CONFIG_SERVICE = new ConfigService();

    private volatile ConfigManager configManager;
    private volatile ConfigRegistry configRegistry;

    public static Config getAppConfig() {
        return getConfig(Constant.APP_NAMESPACE);
    }

    public static Config getConfig(String namespace) {
        return CONFIG_SERVICE.getConfigManager().getConfig(namespace);
    }

    public static void registryNamespace(String namespace, NamespaceHolder holder) {
        CONFIG_SERVICE.getConfigRegistry().registry(namespace, holder);
    }

    public static void registryNamespace(String namespace, NamespaceHolder holder, ConfigFactory factory) {
        CONFIG_SERVICE.getConfigRegistry().registry(namespace, holder, factory);
    }

    private ConfigManager getConfigManager() {
        return instantOnlyOnce(this.configManager, () -> Injectors.getInstance(ConfigManager.class));
    }

    private ConfigRegistry getConfigRegistry() {
        return instantOnlyOnce(this.configRegistry, () -> Injectors.getInstance(ConfigRegistry.class));
    }

    private <T> T instantOnlyOnce(T instance, Supplier<T> supplier) {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = supplier.get();
                }
            }
        }
        return instance;
    }
}
