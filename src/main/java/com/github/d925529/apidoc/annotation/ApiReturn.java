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
public @interface ApiReturn {
    /**
     * 详细描述
     */
    String[] description() default "";

    /**
     * 返回值类型
     *
     * 当返回值类型是List.class时，必须指定 elementType
     * 当返回值类型是Map.class时，必须指定maps来说明map的K-V
     *
     */
    Class<?> type();

    /**
     * 集合元素类型
     * 当返回值是集合类型才有用
     * 当集合元素类型是Map.class时，必须指定maps来说明map的K-V
     */
    Class<?> elementType() default byte.class;

    /**
     * 只有type或者elementType是Map.class时才有用
     * 用来指明map的K-V
     */
    ApiMap[] maps() default {};

    String version() default "";
}
