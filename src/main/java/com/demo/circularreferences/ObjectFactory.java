package com.demo.circularreferences;

/**
 * Program Name: spring-circular-references-demo
 * <p>
 * Description:
 * <p>
 *
 * @author zjw
 * @version 1.0
 * @date 2020/1/16 8:55 下午
 */
@FunctionalInterface
public interface ObjectFactory {
    Object getObject();
}
