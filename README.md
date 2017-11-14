[MENU]

# install



## java

```xml
<dependency>
    <groupId>com.github.d925529</groupId>
    <artifactId>apidoc</artifactId>
    <version>0.0.1</version>
</dependency>
```

## node

```bash
cd node-server
npm install
# edit your mongodb server、port、username/password
vi api-server.js
# please install pm2 first,or you can start by node
pm2 start api-server.js
```




# useage

## java

```java
//only run under dev mode
if(isDev){
    DocUtil.DocScan("node-server-path","appname", "version", "app desc","domain package", "api packages");
}
//for example
if(isDev){
     DocUtil.DocScan("http://127.0.0.1:848","mk-shop-app", "1.0.1", "店铺接口","com.mk.domain", "com.mk.controller.shop");
}
```

## node

1. open `http://127.0.0.1:848?appname=appname&version=version`(node server path) on chrome
2. input your username/password,then you can see:

![Alt text](https://github.com/d925529/apidoc/blob/dev/doc/screencapture-doc-emeker-1510654151737.png)



