package com.personal.java8;

import com.google.common.collect.Lists;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huanying
 */
public final class ClassUtil {
    private ClassUtil() {}

    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private static final String RESOURCE_PATTERN = "**/*.class";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';
    private static final PathMatchingResourcePatternResolver RESOURCE_LOADER = new PathMatchingResourcePatternResolver();
    private static final CachingMetadataReaderFactory CLASS_METADATA_FACTORY = new CachingMetadataReaderFactory(RESOURCE_LOADER);

    public static List<String> scanBasePackage(String basePackage) {
        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + RESOURCE_PATTERN;

        try {
            Resource[] resources = RESOURCE_LOADER.getResources(packageSearchPath);
            List<String> classes = Lists.newArrayListWithCapacity(20);
            for (Resource resource : resources) {
                MetadataReader reader = CLASS_METADATA_FACTORY.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                classes.add(className);
            }
            return classes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>> scanClasses(String basePackage) {
        List<String> classes = scanBasePackage(basePackage);
        return classes.stream()
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static String resolveBasePackage(String basePackage) {
        return basePackage.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }
}
