package com.demo.circularreferences.annotation;

import java.lang.annotation.*;

/**
 * Program Name: spring-circular-references-demo
 * <p>
 * Description:
 * <p>
 *
 * @author zjw
 * @version 1.0
 * @date 2019/4/24 11:48 AM
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CAutowired {
    String value() default "";
}
