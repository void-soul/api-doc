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

import com.github.d925529.apidoc.annotation.*;

import java.lang.reflect.Field;
import java.util.List;

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


    public ApiParamDoc(ApiMap param) {
        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if(param.type().isAssignableFrom(List.class)){
            this.setT(param.elementType());
        }else{
            this.setT(param.type());
        }
    }

    public ApiParamDoc(ApiMap2 param) {
        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if(param.type().isAssignableFrom(List.class)){
            this.setT(param.elementType());
        }else{
            this.setT(param.type());
        }
    }

    public ApiParamDoc(ApiMap3 param) {
        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if(param.type().isAssignableFrom(List.class)){
            this.setT(param.elementType());
        }else{
            this.setT(param.type());
        }
    }


    public ApiParamDoc(ApiParam param) {
        this.setName(param.name());
        this.setTitle(param.title());
        this.setDescription(param.description());
        this.setRequired(param.required());
        this.setDisabled(param.disabled());
        this.setType(param.type().getName());
        this.setElementType(param.elementType().getName());
        if(param.type().isAssignableFrom(List.class)){
            this.setT(param.elementType());
        }else{
            this.setT(param.type());
        }
    }


    public ApiParamDoc(ApiField apiField, Field field) {
        this.setT(field.getType());
        if (apiField != null) {
            this.setTitle(apiField.value());
            this.setDescription(apiField.description());
            this.setRequired(apiField.required());
            this.setDisabled(apiField.disabled());
            this.setElementType(apiField.elementType().getName());
            if(field.getType().isAssignableFrom(List.class)){
                this.setT(apiField.elementType());
            }
        } else {
            this.setTitle(field.getName());
            this.setRequired(true);
            this.setDisabled(false);
        }
        this.setName(field.getName());
        this.setType(field.getType().getName());
    }

    public ApiParamDoc(ApiReturn apiReturn) {
        this.setDescription(apiReturn.description());
        this.setType(apiReturn.type().getName());
        this.setElementType(apiReturn.elementType().getName());
        this.setRequired(true);
        if(apiReturn.type().isAssignableFrom(List.class)){
            this.setT(apiReturn.elementType());
        }else{
            this.setT(apiReturn.type());
        }
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
