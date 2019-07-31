package com.personal;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.beans.Introspector;
import java.util.Collections;
import java.util.List;

/**
 * @author huanying
 */
@Service
public class ServiceLocator implements BeanFactoryAware {

    private static final Splitter SPLITTER = Splitter.on(".").omitEmptyStrings();
    private static ListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        init(beanFactory);
    }

    /**
     * 从Spring容器中获取对象
     * @param name the class name
     * @return the object
     */
    public static Object getBean(String name) {
        return beanFactory.getBean(resolve(name));
    }

    public static <T> List<T> getBeansOfType(Class<T> type) {
        return Collections.unmodifiableList(
                Lists.newArrayList(beanFactory.getBeansOfType(type).values())
        );
    }

    private static String resolve(String name) {
        List<String> res = SPLITTER.splitToList(name);
        String beanName = res.get(res.size() - 1);
        return Introspector.decapitalize(beanName);
    }

    private static void init(BeanFactory beanFactory) {
        ServiceLocator.beanFactory = (ListableBeanFactory) beanFactory;
    }
}
