package com.td.tdpicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//  自定义注解  但凡加了这个注解都要满足以下条件
public @interface AuthCheck {

    /**
     * 必须具有某个角色
     * @return
     */
    String mustRole() default "";
}
