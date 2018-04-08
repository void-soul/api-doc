package com.github.d925529.apidoc;

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

import com.alibaba.fastjson.JSON;
import com.github.d925529.apidoc.domain.ApiDoc;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class DocUtil {
    private static final int HTTP_TIMEOUT = 60000;
    private static final String DEFAULT_CHART_SET = "UTF-8";
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int SUCCESS = 200;
    private static Logger log = LoggerFactory.getLogger("API");

    static {
        connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(HTTP_TIMEOUT);
        configBuilder.setSocketTimeout(HTTP_TIMEOUT);
        configBuilder.setConnectionRequestTimeout(HTTP_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    /**
     * @param path
     * @param appname      appname + version 确定一份API文档
     * @param version
     * @param description
     * @param domain
     * @param packageNames
     */
    @Deprecated
    public static void DocScan(String path, String appname, String version, String description, String domain, String... packageNames) {
        DocScan2(path, appname, version, description, packageNames);
    }

    /**
     *
     * @param path
     * @param appname appname + version 确定一份API文档
     * @param version
     * @param description
     * @param packageNames
     */
    public static void DocScan2(String path, String appname, String version, String description, String... packageNames){
        List<ApiDoc> docs = Utils.readList(packageNames);

        //数据封装
        Map<String, Object> params = new HashMap<>();
        params.put("clz", JSON.toJSONString(docs));
        params.put("appname", appname);
        params.put("version", version);
        params.put("description", description);

        Map<String, String> headers = new HashMap<>();
        String result = doPost(path, headers, params);
        log.info("提交API数据完毕,返回 : {},请浏览 {}?appname={}&version={}", result, path, appname, version);
    }

    private static String doPost(String path, Map<String, String> headers, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        HttpPost httpPost = new HttpPost(path);
        httpPost.setConfig(requestConfig);
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        } else {
            httpPost.reset();
        }
        List<NameValuePair> pairList = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            pairList.add(pair);
        }
        httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(DEFAULT_CHART_SET)));

        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == SUCCESS) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, DEFAULT_CHART_SET);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
