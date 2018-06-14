package com.github.d925529.apidoc.annotation;

/*-
 * #%L
 * ul-tron-api
 * %%
 * Copyright (C) 2017 mk
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */

import java.lang.annotation.*;

/**
 * 字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiField {
    /**
     * 描述
     */
    String value() default "";

    /**
     * 详细描述
     */
    String[] description() default "";

    /**
     * 禁用
     */
    boolean disabled() default false;

    /**
     * 必传、必定返回
     */
    boolean required() default true;

    /**
     * 略过
     */
    boolean deprecated() default false;

    /**
     * map元素 value 的类型是List时，必须指定集合元素的类型： elementType
     */
    Class<?> elementType() default byte.class;

    String version() default "";

    /**
     * 只有type或者elementType是Map.class时才有用
     * 用来指明map的K-V
     */
    ApiMap[] maps() default {};

    /**
     * 这个字段只用来参数传入值,方法返回时不会有此字段
     * @return
     */
    boolean onlyIn() default false;
    /**
     * 这个字段只用来返回值,调用方法时不需要传此字段
     * @return
     */
    boolean onlyOut() default false;
}
