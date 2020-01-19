package com.demo.circularreferences;

import com.demo.circularreferences.annotation.CAutowired;
import com.demo.circularreferences.object.X;
import com.demo.circularreferences.object.Y;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * Program Name: spring-circular-references-demo
 * <p>
 * Description:
 * <p>
 *
 * @author zjw
 * @version 1.0
 */
public class Demo {
    /**
     * 单例池，容器最终维护的单例对象，{bean的类名:bean实例}
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 存储【bean获取函数】的map：{bean名称:获取bean实例的函数}
     */
    private Map<String, ObjectFactory> singletonFactories = new HashMap<>();


    @Test
    public void circularReferencesTest() throws Exception {
        // 1、包扫描获取包下的类
        List<String> classNames = this.componentScan("com.demo.circularreferences.object");
        // 2、bean创建
        for (String className : classNames) {
            this.createBean(className);
        }

        X x = (X) singletonObjects.get(X.class.getName());
        Y y = (Y) singletonObjects.get(Y.class.getName());
        assert x != null && y != null && x.getY() != null && y.getX() != null;
    }

    private Object createBean(String className) throws Exception {
        // 从单例池中获取bean对象，如果存在直接返回
        Object singleton = singletonObjects.get(className);
        if (singleton != null) {
            return singleton;
        }

        // 创建原生对象
        Object instance = Class.forName(className).newInstance();

        // 暴露方式为存储一个获取bean的函数到map中，
        // 将【获取bean的函数】放入singletonFactories map中，这是解决循环依赖的关键。bean是提前暴露，此时bean仍没有完成创建，但拿到它的引用就足够了
        // 用函数而不是简单的bean对象，因为函数更灵活，可以插入其他的操作，比如aop
        singletonFactories.put(className, () -> {
            // TODO 对 instance 的其他操作
            return instance;
        });

        // 依赖装配：设置instance的属性
        this.handleFieldInject(instance);
        // bean创建完成，存入单例池中
        singletonObjects.put(className, instance);
        return instance;
    }

    private void handleFieldInject(Object instance) throws Exception {
        Field[] declaredFields = instance.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            // 为标注@CAutowired注解的属性赋值，CAutowired是自定义注解，模拟Spring中@Autowired
            if (field.isAnnotationPresent(CAutowired.class)) {
                // 获取属性类名
                String fieldClassName = field.getType().getName();
                field.setAccessible(true);

                // 从单例池中获取字段的实例
                Object singleton = singletonObjects.get(fieldClassName);
                if (singleton != null) {
                    field.set(instance, singleton);
                } else if (singletonFactories.containsKey(fieldClassName)) {
                    // 单例池中不存在，则从singletonFactories中获取
                    ObjectFactory objectFactory = singletonFactories.get(fieldClassName);
                    Object object = objectFactory.getObject();
                    field.set(instance, object);
                    singletonFactories.remove(fieldClassName);
                } else {
                    // singletonFactories没有，则创建
                    Object injectObj = this.createBean(fieldClassName);
                    field.set(instance, injectObj);
                }
            }
        }
    }

    /**
     * 包扫描获取类名
     */
    private List<String> componentScan(String packageName) {
        // 所扫描包下的所有类名
        List<String> classNameList = new ArrayList<>();
        URL resourceUrl = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(resourceUrl.getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                componentScan(packageName + "." + file.getName());
            } else {
                // 如果是文件
                String className = packageName + "." + file.getName().replace(".class", "");
                classNameList.add(className);
            }
        }
        return classNameList;
    }
}


// zjw




