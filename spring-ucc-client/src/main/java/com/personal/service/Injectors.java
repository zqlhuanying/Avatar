package com.personal.service;

/**
 * @author zhuangqianliao
 */
public final class Injectors {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector().getInstance(clazz);
    }

    private static Injector getInjector() {
        return Singleton.INJECTOR.getInstance();
    }

    private enum Singleton {
        /**
         * Guice 注入器
         */
        INJECTOR;

        private Injector injector;

        Singleton() {
            injector = new DefaultInjector();
        }

        public Injector getInstance() {
            return injector;
        }
    }
}
