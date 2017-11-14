package com.mk.apidoc;

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
import com.mk.apidoc.annotation.*;
import com.mk.apidoc.domain.ApiDoc;
import com.mk.apidoc.domain.ApiExceptionDoc;
import com.mk.apidoc.domain.ApiMethodDoc;
import com.mk.apidoc.domain.ApiParamDoc;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DocUtil {
    private static final int HTTP_TIMEOUT = 60000;
    private static final String DEFAULT_CHART_SET = "UTF-8";
    private static final String JSON_TYPE = "application/json";
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int SUCCESS = 200;
    private static Logger log = LoggerFactory.getLogger("API");

    static {
        // ?????????
        connMgr = new PoolingHttpClientConnectionManager();
        // ????????????
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // ??????????
        configBuilder.setConnectTimeout(HTTP_TIMEOUT);
        // ?????????
        configBuilder.setSocketTimeout(HTTP_TIMEOUT);
        // ???????????????????????
        configBuilder.setConnectionRequestTimeout(HTTP_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    /**
     * @param path
     * @param appname      appname ?? version ????????
     * @param version
     * @param description
     * @param domain
     * @param packageNames
     */
    public static void DocScan(String path, String appname, String version, String description, String domain, String... packageNames) {
        List<Class<?>> classList = new ArrayList<>();
        //region ???class
        Arrays.asList(packageNames).forEach(packageName -> {
            //region ??????
            Enumeration<URL> urls = null;
            try {
                urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion
            //region ??????????????
            while (urls != null && urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    //region class???
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        addClass(classList, packagePath, packageName);
                    }
                    //endregion
                    //region ??
                    else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = null;
                        try {
                            jarURLConnection = (JarURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JarFile jarFile = null;
                        try {
                            jarFile = jarURLConnection.getJarFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        //region ??????
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            //??? class ???
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                try {
                                    classList.add(Class.forName(className));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //endregion
                    }
                    //endregion
                }
            }
            //endregion
        });
        //endregion
        List<ApiDoc> docs = new ArrayList<>();
        classList.forEach(item -> {
            //?????
            if (!item.isAnnotationPresent(Api.class)) return;
            Api api = item.getAnnotation(Api.class);
            ApiDoc apiDoc = new ApiDoc(api, item.getName());

            List<ApiMethodDoc> apiMethodDocs = new ArrayList<>();

            //region ????????
            List<Method> methods = Arrays.asList(item.getMethods());
            methods.forEach(method -> {
                if (!method.isAnnotationPresent(ApiMethod.class)) return;

                ApiMethod apiMethod = method.getDeclaredAnnotation(ApiMethod.class);
                ApiMethodDoc apiMethodDoc = new ApiMethodDoc(apiMethod, method.getName());
                apiMethodDoc.setPath(api.path() + (apiMethod.path().startsWith("/") ? apiMethod.path() : ("/" + apiMethod.path())));

                //region ?????? ???????????????
                List<ApiParam> apiParamList = new ArrayList<>();
                List<ApiException> apiExceptionList = new ArrayList<>();
                ApiReturn apiReturn = method.getDeclaredAnnotation(ApiReturn.class);
                Arrays.asList(method.getDeclaredAnnotations()).forEach(annotation -> {
                    if (annotation.annotationType() == $ApiParams.class) {
                        $ApiParams $apiParams = ($ApiParams) annotation;
                        apiParamList.addAll(Arrays.asList($apiParams.value()));
                    } else if (annotation.annotationType() == ApiParam.class) {
                        apiParamList.add((ApiParam) annotation);
                    }

                    if (annotation.annotationType() == $ApiExceptions.class) {
                        $ApiExceptions $apiExceptions = ($ApiExceptions) annotation;
                        apiExceptionList.addAll(Arrays.asList($apiExceptions.value()));
                    } else if (annotation.annotationType() == ApiException.class) {
                        apiExceptionList.add((ApiException) annotation);
                    }
                });
                //endregion

                //region ?????? ????
                List<ApiParamDoc> apiParamDocs = new ArrayList<>();
                apiParamList.forEach(apiParam -> {
                    String keyword = item.getName() + "." + method.getName() + "?????apiParam??" + apiParam.name();
                    if (apiParam.type() == List.class && apiParam.elementType() == byte.class) {
                        throw new RuntimeException(keyword + "??List??????????????elementType!");
                    }
                    if (apiParam.type() != List.class && apiParam.elementType() != byte.class) {
                        throw new RuntimeException(keyword + "????List?????????????elementType!");
                    }
                    if ((apiParam.type() == Map.class || apiParam.elementType() == Map.class) && apiParam.maps().length == 0) {
                        throw new RuntimeException(keyword + "??Map??????????????maps!");
                    }
                    if (apiParam.type() != Map.class && apiParam.elementType() != Map.class && apiParam.maps().length > 0) {
                        throw new RuntimeException(keyword + "????Map?????????????maps!");
                    }

                    ApiParamDoc apiParamDoc = new ApiParamDoc(apiParam);
                    readApiParam(apiParamDoc, domain, apiParam.maps(), keyword);
                    apiParamDocs.add(apiParamDoc);
                });
                apiMethodDoc.setParams(apiParamDocs);
                //endregion

                //region ?????? return
                if (apiReturn != null) {
                    String keyword = item.getName() + "." + method.getName() + "?????ApiReturn";
                    if (apiReturn.type() == List.class && apiReturn.elementType() == byte.class) {
                        throw new RuntimeException(keyword + "??List??????????????elementType!");
                    }
                    if ((apiReturn.type() == Map.class || apiReturn.elementType() == Map.class) && apiReturn.maps().length == 0) {
                        throw new RuntimeException(keyword + "??Map??????????????maps!");
                    }
                    ApiParamDoc apiParamDoc = new ApiParamDoc(apiReturn);
                    readApiParam(apiParamDoc, domain, apiReturn.maps(), keyword);
                    apiMethodDoc.setReturnValue(apiParamDoc);
                }
                //endregion

                //region ??? ??
                List<ApiExceptionDoc> ex = new ArrayList<>();
                apiExceptionList.forEach(exception -> ex.add(new ApiExceptionDoc(exception)));
                apiMethodDoc.setExceptions(ex);
                //endregion

                apiMethodDocs.add(apiMethodDoc);
            });
            //endregion

            apiDoc.setMethods(apiMethodDocs);
            docs.add(apiDoc);
        });
        Map<String, Object> params = new HashMap<>();
        params.put("clz", JSON.toJSONString(docs));
        params.put("appname", appname);
        params.put("version", version);
        params.put("description", description);

        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", "");

        String result = doPost(path, headers, params);
        log.info("??API???????,???? : {}", result);
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

    private static void readDomain(ApiParamDoc apiParamDoc, String keyword, String domain) {
        List<ApiParamDoc> apiMapDocs = new ArrayList<>();
        Arrays.asList(apiParamDoc.getT().getDeclaredFields()).forEach(field -> {
            ApiParamDoc apiParamDoc1;
            if (field.isAnnotationPresent(ApiField.class)) {
                ApiField apiField = field.getDeclaredAnnotation(ApiField.class);
                if (field.getType().isAssignableFrom(List.class) && apiField.elementType() == byte.class) {
                    throw new RuntimeException(keyword + " ?????" + field.getName() + "??List??????????????elementType!");
                }
                if (!field.getType().isAssignableFrom(List.class) && apiField.elementType() != byte.class) {
                    throw new RuntimeException(keyword + " ?????" + field.getName() + "????List?????????????elementType!");
                }
                if (apiField.deprecated()) {
                    return;
                }
                apiParamDoc1 = new ApiParamDoc(apiField, field);
            } else {
                apiParamDoc1 = new ApiParamDoc(null, field);
            }
            if (apiParamDoc1.getT().getName().contains(domain)) {
                List<ApiParamDoc> apiMapDocs2 = new ArrayList<>();
                Arrays.asList(apiParamDoc1.getT().getDeclaredFields()).forEach(field2 -> {
                    ApiParamDoc apiParamDoc2;
                    if (field2.isAnnotationPresent(ApiField.class)) {
                        ApiField apiField2 = field2.getDeclaredAnnotation(ApiField.class);
                        if (field2.getType().isAssignableFrom(List.class) && apiField2.elementType() == byte.class) {
                            throw new RuntimeException(keyword + " ?????" + field2.getName() + "??List??????????????elementType!");
                        }
                        if (!field2.getType().isAssignableFrom(List.class) && apiField2.elementType() != byte.class) {
                            throw new RuntimeException(keyword + " ?????" + field2.getName() + "????List?????????????elementType!");
                        }
                        if (apiField2.deprecated()) {
                            return;
                        }
                        apiParamDoc2 = new ApiParamDoc(apiField2, field2);
                    } else {
                        apiParamDoc2 = new ApiParamDoc(null, field2);
                    }
                    apiMapDocs2.add(apiParamDoc2);
                });
                apiParamDoc1.setChildren(apiMapDocs2);
            }
            apiMapDocs.add(apiParamDoc1);
        });
        apiParamDoc.setChildren(apiMapDocs);
    }

    private static void readApiParam(ApiParamDoc apiParamDoc, String domain, ApiMap[] apiMaps, String keyword) {
        //region ?????
        if (apiParamDoc.getT().getName().contains(domain)) {
            readDomain(apiParamDoc, keyword, domain);
        }
        //endregion
        else if (apiMaps.length > 0) {
            List<ApiParamDoc> apiParamDocs = new ArrayList<>();
            Arrays.asList(apiMaps).forEach(apiMap -> {
                if (apiMap.type() == List.class && apiMap.elementType() == byte.class) {
                    throw new RuntimeException(keyword + apiMap.name() + "??List??????????????elementType!");
                }
                if (apiMap.type() != List.class && apiMap.elementType() != byte.class) {
                    throw new RuntimeException(keyword + apiMap.name() + "????List?????????????elementType!");
                }
                if ((apiMap.type() == Map.class || apiMap.elementType() == Map.class) && apiMap.maps().length == 0) {
                    throw new RuntimeException(keyword + apiMap.name() + "??Map??????????????maps!");
                }
                if (apiMap.type() != Map.class && apiMap.elementType() != Map.class && apiMap.maps().length > 0) {
                    throw new RuntimeException(keyword + apiMap.name() + "????Map?????????????maps!");
                }
                ApiParamDoc apiParamDoc1 = new ApiParamDoc(apiMap);
                List<ApiParamDoc> apiParamDocs2 = new ArrayList<>();
                Arrays.asList(apiMap.maps()).forEach(apiMap2 -> {
                    if (apiMap2.type() == List.class && apiMap2.elementType() == byte.class) {
                        throw new RuntimeException(keyword + apiMap2.name() + "??List??????????????elementType!");
                    }
                    if (apiMap2.type() != List.class && apiMap2.elementType() != byte.class) {
                        throw new RuntimeException(keyword + apiMap2.name() + "????List?????????????elementType!");
                    }

                    ApiParamDoc apiParamDoc2 = new ApiParamDoc(apiMap2);

                    if (apiParamDoc2.getT().getName().contains(domain)) {
                        readDomain(apiParamDoc2, keyword, domain);
                    } else {
                        if ((apiMap2.type() == Map.class || apiMap2.elementType() == Map.class) && apiMap2.maps().length == 0) {
                            throw new RuntimeException(keyword + apiMap2.name() + "??Map??????????????maps!");
                        }
                        if (apiMap2.type() != Map.class && apiMap2.elementType() != Map.class && apiMap2.maps().length > 0) {
                            throw new RuntimeException(keyword + apiMap2.name() + "????Map?????????????maps!");
                        }
                        List<ApiParamDoc> apiParamDocs3 = new ArrayList<>();
                        Arrays.asList(apiMap2.maps()).forEach(apiMap3 -> {
                            if (apiMap3.type() == List.class && apiMap3.elementType() == byte.class) {
                                throw new RuntimeException(keyword + apiMap3.name() + "??List??????????????elementType!");
                            }
                            if (apiMap3.type() != List.class && apiMap3.elementType() != byte.class) {
                                throw new RuntimeException(keyword + apiMap3.name() + "????List?????????????elementType!");
                            }

                            ApiParamDoc apiParamDoc3 = new ApiParamDoc(apiMap3);
                            if (apiParamDoc3.getT().getName().contains(domain)) {
                                readDomain(apiParamDoc3, keyword, domain);
                            }

                            apiParamDocs3.add(apiParamDoc3);
                        });
                        apiParamDoc2.setChildren(apiParamDocs3);
                    }
                    apiParamDocs2.add(apiParamDoc2);
                });
                apiParamDoc1.setChildren(apiParamDocs2);
                apiParamDocs.add(apiParamDoc1);
            });
            apiParamDoc.setChildren(apiParamDocs);
        }
    }

    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        File[] files = getClassFiles(packagePath);
        if (files != null) {
            Arrays.asList(files).forEach(file -> {
                String fileName = file.getName();
                if (file.isFile()) {
                    String className = getClassName(packageName, fileName);
                    try {
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    String subPackagePath = getSubPackagePath(packagePath, fileName);
                    String subPackageName = getSubPackageName(packageName, fileName);
                    addClass(classList, subPackagePath, subPackageName);
                }
            });
        }
    }

    private static File[] getClassFiles(String packagePath) {
        return new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
    }

    private static String getClassName(String packageName, String fileName) {
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        if (isNotEmpty(packageName)) {
            className = packageName + "." + className;
        }
        return className;
    }

    private static String getSubPackagePath(String packagePath, String filePath) {
        String subPackagePath = filePath;
        if (isNotEmpty(packagePath)) {
            subPackagePath = packagePath + "/" + subPackagePath;
        }
        return subPackagePath;
    }

    private static String getSubPackageName(String packageName, String filePath) {
        String subPackageName = filePath;
        if (isNotEmpty(packageName)) {
            subPackageName = packageName + "." + subPackageName;
        }
        return subPackageName;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    private static boolean isNotEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
