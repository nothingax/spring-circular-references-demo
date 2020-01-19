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
 * @date 2020/1/16 8:22 下午
 */
public class X {
    @CAutowired
    private Y y;

    public Y getY() {
        return y;
    }
}
