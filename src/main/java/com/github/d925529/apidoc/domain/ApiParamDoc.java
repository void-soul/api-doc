package com.github.d925529.apidoc.domain;

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

import com.github.d925529.apidoc.Utils;
import com.github.d925529.apidoc.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ApiParamDoc {
    private String name;
    private String title;
    private String[] description;
    private boolean required;
    private boolean disabled;
    private String type;
    private String elementType;
    private List<ApiParamDoc> children;
    private transient Class<?> t;
    private String version;
    private boolean onlyIn;
    private boolean onlyOut;

    public ApiParamDoc(ApiParam param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    public ApiParamDoc(ApiReturn param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + "返回值是List类型，但没有定义elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + "返回值是Map类型，但没有定义maps!");
        }

        this.setDescription(param.description());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        this.setRequired(true);
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());
        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }


    private ApiParamDoc(ApiMap param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap2 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap3 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap4 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap5 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap6 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap7 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap8 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap9 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }
        if ((param.type() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
            throw new RuntimeException(keyword + param.name() + "是Map类型，但没有定义maps!");
        }
        if (param.type() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
            throw new RuntimeException(keyword + param.name() + "不是Map类型，但定义了maps!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
        //List、map
        if (param.maps().length > 0) {
            Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
        }
    }

    private ApiParamDoc(ApiMap10 param, String keyword, Map<Class, Boolean> domainReadCache) {
        if (param.type() == List.class && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + param.name() + "是List类型，但没有定义elementType!");
        }
        if (param.type() != List.class && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + param.name() + "不是List类型，但定义了elementType!");
        }

        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if (param.type().isAssignableFrom(List.class)) {
            this.setT(param.elementType());
        } else {
            this.setT(param.type());
        }
        this.setVersion(param.version());
        this.setChildren(new ArrayList<>());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field -> {
                    ApiParamDoc apiParamDoc1;
                    if (field.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }
    }

    private ApiParamDoc(ApiField param, Field field, String keyword, Map<Class, Boolean> domainReadCache) {
        if (field.getType().isAssignableFrom(List.class) && param.elementType() == byte.class) {
            throw new RuntimeException(keyword + " 字段：" + field.getName() + "是List类型，但没有定义elementType!");
        }
        if (!field.getType().isAssignableFrom(List.class) && param.elementType() != byte.class) {
            throw new RuntimeException(keyword + " 字段：" + field.getName() + "不是List类型，但定义了elementType!");
        }

        this.setChildren(new ArrayList<>());
        this.setT(field.getType());
        if (param != null) {
            if ((field.getType() == Map.class || param.elementType() == Map.class) && param.maps().length == 0) {
                throw new RuntimeException(keyword + field.getName() + "是Map类型，但没有定义maps!");
            }
            if (field.getType() != Map.class && param.elementType() != Map.class && param.maps().length > 0) {
                throw new RuntimeException(keyword + field.getName() + "不是Map类型，但定义了maps!");
            }
            this.setTitle(param.value());
            this.setOnlyIn(param.onlyIn());
            this.setOnlyOut(param.onlyOut());
            this.setDescription(param.description());
            this.setRequired(param.required());
            this.setDisabled(param.disabled());
            this.setElementType(param.elementType().getName());
            if (field.getType().isAssignableFrom(List.class)) {
                this.setT(param.elementType());
            }
            this.setVersion(param.version());

            //List、map
            if (param.maps().length > 0) {
                Arrays.asList(param.maps()).forEach(apiMap -> this.getChildren().add(new ApiParamDoc(apiMap, keyword, domainReadCache)));
            }

        } else {
            this.setTitle(field.getName());
            this.setRequired(true);
            this.setDisabled(false);
        }
        this.setName(field.getName());
        this.setType(field.getType().getName());

        //实体类
        if (Utils.isBaseType(this.getT())) {
            if (!domainReadCache.containsKey(this.getT())) {
                domainReadCache.put(this.getT(), true);
                Arrays.asList(this.getT().getDeclaredFields()).forEach(field2 -> {
                    ApiParamDoc apiParamDoc1;
                    if (field2.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField = field2.getDeclaredAnnotation(ApiField.class);
                        if (apiField.deprecated()) {
                            return;
                        }
                        apiParamDoc1 = new ApiParamDoc(apiField, field2, keyword, domainReadCache);
                    } else {
                        apiParamDoc1 = new ApiParamDoc(null, field2, keyword, domainReadCache);
                    }
                    this.getChildren().add(apiParamDoc1);
                });
            }
        }

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class<?> getT() {
        return t;
    }

    public void setT(Class<?> t) {
        this.t = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOnlyIn() {
        return onlyIn;
    }

    public void setOnlyIn(boolean onlyIn) {
        this.onlyIn = onlyIn;
    }

    public boolean isOnlyOut() {
        return onlyOut;
    }

    public void setOnlyOut(boolean onlyOut) {
        this.onlyOut = onlyOut;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public List<ApiParamDoc> getChildren() {
        return children;
    }

    public void setChildren(List<ApiParamDoc> children) {
        this.children = children;
    }
}
