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

this is a verv complex demo

```java
   @ApiMethod(path = "/skus.json", title = "获取商品sku", method = ApiHttpMethod.POST,description = {
        "获取商品的SKU数据",
        "同时返回商品详情页面需要展示的手机号码"
    })
    @ApiParam(name = "pdid", title = "商品id", type = int.class)
    @ApiReturn(description = "商品SKU", type = Map.class, maps = {
        @ApiMap(name = "pdid", title = "商品ID", type = Integer.class),
        @ApiMap(name = "pdname", title = "商品名称", type = String.class),
        @ApiMap(name = "unit", title = "单位", type = String.class),
        @ApiMap(name = "img", title = "商品主图", description = {
            "可以联合/core/upload/{img}.html进行下载",
            "也可以直接访问文件服务器，目前token是固定不变的"
        }, type = String.class),
        @ApiMap(name = "agmobile", title = "服务商电话", type = String.class),
        @ApiMap(name = "bdtelphone", title = "品牌商客服电话", type = String.class),

        @ApiMap(name = "price_type", title = "返回价格数据类型", description = {
            "price_type == 1 价格只有一个，取price",
            "price_type == 2 最小价格 ~ 最大价格，取price_min和price_max"
        }, type = Integer.class),
        @ApiMap(name = "price", title = "单一价格", type = Double.class),
        @ApiMap(name = "price_max", title = "最大价格", type = Double.class),
        @ApiMap(name = "price_min", title = "最小价格", type = Double.class),

        @ApiMap(name = "sku_type", title = "返回规格类型", description = {
            "sky_type == 1 只有一个规格，取skuOne，此时商品的skus是空的,skuOne的标准属性除了skus是空的以外，其他都有值",
            "sku_type == 2 一维规格数组，取skus，此时商品的skuOne是空的，skus中的每个元素除了skus是空的以外，其他都有值",
            "sky_type == 3 二维规格数组，取skus，此时skuOne是空的，skus中的每个元素仅skuname和skus有值，每个元素的skus又是一个数组，每个元素除了skus以外都是有值"
        }, type = Integer.class),
        @ApiMap(name = "skuOne", title = "单个规格SKU", type = Map.class, maps = {
            @ApiMap2(name = "skuid", title = "SKU的id", type = Integer.class),
            @ApiMap2(name = "skuname", title = "SKU名称", type = Integer.class),
            @ApiMap2(name = "skus", title = "子SKU集合", type = List.class, elementType = Map.class, maps = {
                @ApiMap3(name = "skuid", title = "SKU的id", type = Integer.class),
                @ApiMap3(name = "skuname", title = "SKU名称", type = Integer.class),
                @ApiMap3(name = "price", title = "价格", type = Double.class),
                @ApiMap3(name = "stock", title = "库存", type = Integer.class),
                @ApiMap3(name = "unit", title = "单位", type = String.class),
                @ApiMap3(name = "moq", title = "起订量", type = Integer.class),
                @ApiMap3(name = "farpostage", title = "偏远地区运费", type = Integer.class, disabled = true),
                @ApiMap3(name = "postage", title = "运费", type = Integer.class, disabled = true)
            }),
            @ApiMap2(name = "price", title = "价格", type = Double.class),
            @ApiMap2(name = "stock", title = "库存", type = Integer.class),
            @ApiMap2(name = "unit", title = "单位", type = String.class),
            @ApiMap2(name = "moq", title = "起订量", type = Integer.class),
            @ApiMap2(name = "farpostage", title = "偏远地区运费", type = Integer.class, disabled = true),
            @ApiMap2(name = "postage", title = "运费", type = Integer.class, disabled = true)
        }),
        @ApiMap(name = "skus", title = "多个规格SKU", type = List.class, elementType = Map.class, maps = {
            @ApiMap2(name = "skuid", title = "SKU的id", type = Integer.class),
            @ApiMap2(name = "skuname", title = "SKU名称", type = Integer.class),
            @ApiMap2(name = "skus", title = "子SKU集合", type = List.class, elementType = Map.class, maps = {
                @ApiMap3(name = "skuid", title = "SKU的id", type = Integer.class),
                @ApiMap3(name = "skuname", title = "SKU名称", type = Integer.class),
                @ApiMap3(name = "price", title = "价格", type = Double.class),
                @ApiMap3(name = "stock", title = "库存", type = Integer.class),
                @ApiMap3(name = "unit", title = "单位", type = String.class),
                @ApiMap3(name = "moq", title = "起订量", type = Integer.class),
                @ApiMap3(name = "farpostage", title = "偏远地区运费", type = Integer.class, disabled = true),
                @ApiMap3(name = "postage", title = "运费", type = Integer.class, disabled = true)
            }),
            @ApiMap2(name = "price", title = "价格", type = Double.class),
            @ApiMap2(name = "stock", title = "库存", type = Integer.class),
            @ApiMap2(name = "unit", title = "单位", type = String.class),
            @ApiMap2(name = "moq", title = "起订量", type = Integer.class),
            @ApiMap2(name = "farpostage", title = "偏远地区运费", type = Integer.class, disabled = true),
            @ApiMap2(name = "postage", title = "运费", type = Integer.class, disabled = true)
        })
    })
    @ApiException(ErrorCode.E207003)
    @ApiException(code = "207003", description = "商品ID对应商品不是在售状态")
```


## node

1. open `http://127.0.0.1:848?appname=appname&version=version`(node server path) on chrome
2. input your username/password,then you can see:

![Alt text](https://github.com/d925529/apidoc/blob/master/doc/screencapture-doc-emeker-1510654151737.png?raw=true)



