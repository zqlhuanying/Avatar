package com.personal.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class ClassLoaderUtil {

    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static String classPath = "";

    static {
        if (loader == null) {
            log.warn("Using system class loader");
            loader = ClassLoader.getSystemClassLoader();
        }

        try {
            URL url = loader.getResource("");
            // get class path
            if (url != null) {
                classPath = url.getPath();
                classPath = URLDecoder.decode(classPath, "utf-8");
            }

            // 如果是jar包内的，则返回当前路径
            if (Strings.isNullOrEmpty(classPath) || classPath.contains(".jar!")) {
                classPath = System.getProperty("user.dir");
            }
        } catch (Throwable ex) {
            classPath = System.getProperty("user.dir");
            log.warn("Failed to locate class path, fallback to user.dir: {}", classPath, ex);
        }
    }

    public static ClassLoader getLoader() {
        return loader;
    }

    public static String getClassPath() {
        return classPath;
    }

    public static Properties loadLocalProperties(String namespace) {
        String name = String.format("%s.properties", namespace);
        Properties properties = null;
        try (
                InputStream in = ClassLoaderUtil.getLoader().getResourceAsStream(name)
        ) {
            if (in != null) {
                properties = new Properties();
                properties.load(in);
            }
        } catch (IOException e) {
            log.error("Load local config for namespace {} failed", namespace, e);
        }
        return properties;
    }
}
