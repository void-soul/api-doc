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

import com.github.d925529.apidoc.annotation.ApiMethod;

import java.util.List;

public class ApiMethodDoc {
    private String title;
    private String name;
    private String[] description;
    private String path;
    private boolean disabled;
    private String method;
    private ApiParamDoc returnValue;
    private boolean common;
    private List<ApiParamDoc> params;
    private List<ApiExceptionDoc> exceptions;
    private String version;


    public ApiMethodDoc(ApiMethod method, String name){
        this.setTitle(method.title());
        this.setName(name);
        this.setDescription(method.description());
        this.setPath(method.path());
        this.setDisabled(method.disabled());
        this.setMethod(method.method().name());
        this.setCommon(method.common());
        this.setVersion(method.version());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isCommon() {
        return common;
    }

    public void setCommon(boolean common) {
        this.common = common;
    }

    public ApiParamDoc getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ApiParamDoc returnValue) {
        this.returnValue = returnValue;
    }

    public List<ApiParamDoc> getParams() {
        return params;
    }

    public void setParams(List<ApiParamDoc> params) {
        this.params = params;
    }

    public List<ApiExceptionDoc> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<ApiExceptionDoc> exceptions) {
        this.exceptions = exceptions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
