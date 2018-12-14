package com.personal.ucc;

import com.personal.config.AbstractConfigRepository;
import com.personal.config.ConfigRegistry;
import com.personal.config.NamespaceHolder;
import com.personal.service.Injectors;
import com.personal.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class UccSecurityConfigRepository extends AbstractConfigRepository {

    private final String namespace;
    private ConfigRegistry configRegistry;

    public UccSecurityConfigRepository(String namespace) {
        this.namespace = namespace;
        this.configRegistry = Injectors.getInstance(ConfigRegistry.class);
    }

    @Override
    public Properties getConfig() {
        NamespaceHolder holder = this.configRegistry.getNamespaceHolder(this.namespace);
        check(holder);
        return doGetConfig((UccNamespaceHolder) holder);
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
    }

    private Properties doGetConfig(UccNamespaceHolder holder) {
        Properties properties = new Properties();
        try {
            JDSecurityPropertyFactoryBean jdSecurityPropertyFactoryBean = new JDSecurityPropertyFactoryBean();
            jdSecurityPropertyFactoryBean.setIgnoreResourceNotFound(true);
            jdSecurityPropertyFactoryBean.setSecLocation(new ClassPathResource(holder.getPath() + ".properties", ClassLoaderUtil.getLoader()));
            jdSecurityPropertyFactoryBean.loadProperties(properties);
        } catch (Exception e) {
            log.error("Getting config({}) from ucc is failed", holder.getPath(), e);
        }
        return properties;
    }
}
