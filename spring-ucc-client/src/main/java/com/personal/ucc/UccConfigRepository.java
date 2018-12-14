package com.personal.ucc;

import com.google.common.io.Files;
import com.personal.config.AbstractConfigRepository;
import com.personal.config.ConfigRegistry;
import com.personal.config.NamespaceHolder;
import com.personal.constants.Constant;
import com.personal.exception.ConfigException;
import com.personal.service.Injectors;
import com.personal.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class UccConfigRepository extends AbstractConfigRepository implements PathListener {

    private final String namespace;
    private ConfigRegistry configRegistry;
    private ConfClient client;

    public UccConfigRepository(String namespace) {
        this.namespace = namespace;
        this.configRegistry = Injectors.getInstance(ConfigRegistry.class);
        this.client = ConfClientUtil.getConfClient();
    }

    @Override
    public Properties getConfig() {
        NamespaceHolder holder = this.configRegistry.getNamespaceHolder(this.namespace);
        check(holder);
        return doGetConfig((UccNamespaceHolder) holder);
    }

    @Override
    public void handleConfig(Map<String, String> map) {
        Properties newProperties = new Properties();
        newProperties.putAll(map);
        this.onConfigChange(this.namespace, newProperties);
    }

    @Override
    public void exceptionCaught(Throwable throwable) {
        log.error("Path({}) is not be subscribed, please subscribe it firstly!", this.namespace);
        log.error("--------------->" + throwable);
    }

    private void check(NamespaceHolder holder) {
        if (!(holder instanceof UccNamespaceHolder)) {
            throw new IllegalArgumentException(
                    String.format("NamespaceHolder(%s) is not UccNamespaceHolder instance", this.namespace)
            );
        }

        UccNamespaceHolder _holder = (UccNamespaceHolder) holder;
        if (StringUtils.isBlank(_holder.getPath())) {
            throw new IllegalArgumentException(
                    String.format("Path is empty for namespace: %s", namespace)
            );
        }
        if (StringUtils.isBlank(_holder.getReadToken())) {
            throw new IllegalArgumentException(
                    String.format("Read Token is empty for namespace: %s", namespace)
            );
        }
    }

    private Properties doGetConfig(UccNamespaceHolder holder) {
        Properties properties = new Properties();
        try {
            Map<String, String> values = this.client.getPathValues(
                    populate(holder.getPath()), holder.getReadToken()
            );
            properties.putAll(values);
            subscribe(holder);
        } catch (Exception e) {
            log.error("Getting config({}) from ucc is failed", holder.getPath(), e);
        }
        return properties;
    }

    private void subscribe(UccNamespaceHolder holder) throws Exception {
        if (holder.isSubscribe()) {
            this.client.subscribePath(
                    populate(holder.getPath()), holder.getReadToken(), this
            );
        }
    }

    private String populate(String namespace) {
        String appId = ClassLoaderUtil.loadLocalProperties(Constant.APP_NAMESPACE)
                .getProperty(Constant.APP_ID, null);
        if (StringUtils.isBlank(appId)) {
            throw new ConfigException("app id must be setted");
        }
        String populateNamespace = new StringBuilder("/")
                .append(appId)
                .append("/")
                .append(namespace.replace(appId, ""))
                .toString();
        return Files.simplifyPath(populateNamespace);
    }
}
