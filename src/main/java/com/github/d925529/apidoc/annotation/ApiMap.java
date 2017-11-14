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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiMap {
    /**
     * Map 元素的 K
     */
    String name();

    /**
     * Map 元素 K 的含义
     */
    String title();

    /**
     * 详细描述
     */
    String[] description() default "";

    /**
     * 是否必须传递/是否必定返回
     */
    boolean required() default true;

    /**
     * map元素 value 的类型
     */
    Class<?> type();

    /**
     * map元素 value 的类型是List时，必须指定集合元素的类型： elementType
     */
    Class<?> elementType() default byte.class;

    /**
     *
     * map元素 value 或者 集合元素的类型是 Map 时
     * 必须指定Map的K-V
     */
    ApiMap2[] maps() default {};

    /**
     * 是否废弃
     */
    boolean disabled() default false;
}
