package com.personal.ucc;

import com.personal.config.Config;
import com.personal.config.ConfigManager;
import com.personal.config.ConfigRegistry;
import com.personal.config.NamespaceHolder;
import com.personal.service.Injectors;

import java.util.concurrent.TimeUnit;

/**
 * @author zhuangqianliao
 * Init ucc client with ucc-config.properties when the server startup
 * So the client will be not changed when the service is running
 */
public final class ConfClientUtil {

    private static final String DEFAULT_HOST = "http://api.ucc.jd.local";
    /**
     * TimeUnit: Second
     */
    private static final int DEFAULT_WAIT_TIME_OUT = 5;
    /**
     * TimeUnit: Second
     */
    private static final int DEFAULT_LONG_POLLING_INTERVAL = 1;
    private static final int DEFAULT_THREAD_POOL_SIZE = 15;
    private static final boolean DEFAULT_USE_CACHE = true;

    private static final String NAMESPACE = "ucc-config";
    private static final String HOST = "host";
    private static final String WAIT_TIME_OUT = "waitTimeOut";
    private static final String LONG_POLLING_INTERVAL = "longPollingInterval";
    private static final String THREAD_POOL_SIZE = "threadPoolSize";
    private static final String USE_CACHE = "useCache";

    private static ConfigManager configManager = Injectors.getInstance(ConfigManager.class);
    private static ConfigRegistry configRegistry = Injectors.getInstance(ConfigRegistry.class);
    private static NamespaceHolder holder = () -> NAMESPACE;
    private static ConfClient confClient;


    static {
        Config config = getClientConfig();
        String host = config.getProperty(HOST, DEFAULT_HOST);
        int waitTimeOut = config.getIntProperty(WAIT_TIME_OUT, DEFAULT_WAIT_TIME_OUT);
        int longPollingInterval = config.getIntProperty(LONG_POLLING_INTERVAL, DEFAULT_LONG_POLLING_INTERVAL);
        int threadPoolSize = config.getIntProperty(THREAD_POOL_SIZE, DEFAULT_THREAD_POOL_SIZE);
        boolean useCache = config.getBooleanProperty(USE_CACHE, DEFAULT_USE_CACHE);
        confClient = ConfClientBuilder.newBuilder()
                .host(host)
                .waitTimeout(waitTimeOut)
                .timeUnit(TimeUnit.SECONDS)
                .pollingThreadPoolSize(threadPoolSize)
                .intervalTime(longPollingInterval)
                .useCache(useCache)
                .build();
    }

    public static ConfClient getConfClient() {
        return confClient;
    }

    private static Config getClientConfig() {
        configRegistry.registry(NAMESPACE, holder);
        return configManager.getConfig(NAMESPACE);
    }
}
