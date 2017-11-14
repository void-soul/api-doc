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

import com.github.d925529.apidoc.annotation.ApiException;

public class ApiExceptionDoc {
    private String code;
    private String[] description;
    private boolean disabled;

    public ApiExceptionDoc(ApiException exception){
        this.setDisabled(exception.disabled());
        this.setCode(exception.code());
        this.setDescription(exception.description());
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
