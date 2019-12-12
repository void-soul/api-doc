package com.github.d925529.apidoc;

import com.github.d925529.apidoc.annotation.*;
import com.github.d925529.apidoc.domain.ApiDoc;
import com.github.d925529.apidoc.domain.ApiExceptionDoc;
import com.github.d925529.apidoc.domain.ApiMethodDoc;
import com.github.d925529.apidoc.domain.ApiParamDoc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
@SuppressWarnings("unused")
public class Utils {
    static List<ApiDoc> readList(String... packageNames) {
        List<Class<?>> classList = new ArrayList<>();
        //region 读取class
        Arrays.asList(packageNames).forEach(packageName -> {
            //region 读取文件
            Enumeration<URL> urls = null;
            try {
                urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //endregion
            //region 递归本文件以及子目录
            while (urls != null && urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    //region class文件
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        Utils.addClass(classList, packagePath, packageName);
                    }
                    //endregion
                    //region 包
                    else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = null;
                        try {
                            jarURLConnection = (JarURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JarFile jarFile = null;
                        try {
                            assert jarURLConnection != null;
                            jarFile = jarURLConnection.getJarFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        assert jarFile != null;
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        //region 循环子包
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            //读取 class 文件
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
        //region 读取文档
        List<ApiDoc> docs = new ArrayList<>();
        classList.forEach(item -> {
            //检测类
            if (!item.isAnnotationPresent(Api.class)) return;
            Api api = item.getAnnotation(Api.class);
            ApiDoc apiDoc = new ApiDoc(api, item.getName());
            List<ApiMethodDoc> apiMethodDocs = new ArrayList<>();
            //region 解析方法
            List<Method> methods = Arrays.asList(item.getMethods());
            methods.forEach(method -> {
                if (!method.isAnnotationPresent(ApiMethod.class)) return;

                ApiMethod apiMethod = method.getDeclaredAnnotation(ApiMethod.class);
                ApiMethodDoc apiMethodDoc = new ApiMethodDoc(apiMethod, method.getName());
                apiMethodDoc.setPath(api.path() + (apiMethod.path().startsWith("/") ? apiMethod.path() : ("/" + apiMethod.path())));


                //region 获取注解 参数、异常、返回值
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

                String keyword = item.getName() + "." + method.getName() + "->";

                //region 检测并赋值 参数
                List<ApiParamDoc> apiParamDocs = new ArrayList<>();
                apiParamList.forEach(apiParam -> {
                    //本次方法防止递归
                    Map<Class,Boolean> domainReadCache = new HashMap<>();
                    ApiParamDoc apiParamDoc = new ApiParamDoc(apiParam, keyword,domainReadCache);
                    apiParamDocs.add(apiParamDoc);
                });
                apiMethodDoc.setParams(apiParamDocs);
                //endregion

                //region 检测并赋值 return
                if (apiReturn != null) {
                    Map<Class,Boolean> domainReadCache = new HashMap<>();
                    ApiParamDoc apiParamDoc = new ApiParamDoc(apiReturn, keyword,domainReadCache);
                    apiMethodDoc.setReturnValue(apiParamDoc);
                }
                //endregion

                //region 赋值 异常
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
        //endregion
        return docs;
    }

    public static boolean isBaseType(Class clz) {
        return
            clz != Short.class && clz != short.class &&
                clz != Integer.class && clz != int.class &&
                clz != Long.class && clz != long.class &&
                clz != Float.class && clz != float.class &&
                clz != Double.class && clz != double.class &&
                clz != BigDecimal.class &&
                clz != Date.class &&
                clz != String.class && clz != char.class &&
                clz != Number.class && clz != Boolean.class &&
                clz != boolean.class && clz != Byte.class && clz != Map.class && clz != HashMap.class && clz != TreeMap.class &&
                clz != StringBuilder.class && clz != StringBuffer.class;
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
        return !isEmpty(str);
    }
}
