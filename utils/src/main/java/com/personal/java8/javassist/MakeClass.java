package com.personal.java8.javassist;

import com.personal.java8.ClassUtil;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author huanying
 */
@Component
public class MakeClass implements ApplicationListener<ApplicationPreparedEvent> {

    private static ClassPool classPool = ClassPool.getDefault();

    static {
        ClassClassPath classPath = new ClassClassPath(RuleMetric.class);
        classPool.insertClassPath(classPath);
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        makeClass();
    }

    public void makeClass() {
        List<String> classes = ClassUtil.scanBasePackage("com.pdd.service.megatron.strategy.handler.rule");
        String methodName = "evaluate";

        try {
            for (String clazzName : classes) {
                CtClass cc = classPool.get(clazzName);
                CtMethod cm = cc.getDeclaredMethod(methodName);
                cm.insertBefore("System.out.println(\"javassist\");");
                cc.toClass();
                classPool.getClassLoader().loadClass(clazzName);
            }
        } catch (NotFoundException | CannotCompileException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
