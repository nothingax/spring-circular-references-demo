package com.demo.circularreferences.object;


import com.demo.circularreferences.annotation.CAutowired;

/**
 * Program Name: spring-circular-references-demo
 * <p>
 * Description:
 * <p>
 *
 * @author zjw
 * @version 1.0
 * @date 2020/1/18 4:51 下午
 */
public class B {
    @CAutowired
    private C c;

    public C getC() {
        return c;
    }

}
