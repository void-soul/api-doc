[MENU]
#edc-java

>该项目已经不再维护，代码并入 mk 项目中了，文档还可以阅读

## 引入

```xml
<dependency>
    <groupId>com.mk</groupId>
    <artifactId>edc</artifactId>
    <version>0.1.8</version>
</dependency><!-- edc -->
```

项目还依赖`socket.io`，`okhttp`[3.5.0]

## 适用的项目

> 无论事件广播者还是事件收听者，**最好**都在service中使用，方便控制事务

## 事件广播

>事件广播只有两步，首先在事件上添加注解，然后调用广播方法即可.


###准备工作

在启动项目中初始化 切面工具类`MainServicePoint`以及事务环境`EventUtil.Ready()`


###注解: `@EventSource`

这个注解的作用是为了控制事务，当`主方法`发生异常需要回滚事务时，注解会通知所有已经处理的事件收听者进行回滚处理.
所以这个注解需要加到支持事务的方法上.

```java
import com.mk.edc.annotation.EventSource;

@Override
@Transactional
@EventSource
public int updateCustom(CpUser record) {

}
```

###广播事件

广播事件参数分别是 `事件名称(String)`,`事件参数(Map)`,`是否异步执行(boolean)`,`是否强制执行(boolean)`,`期望收听者数量(int)`

`事件名称(String)` 需要和收听者约定好,收听者能否收到事件的唯一判断标准就是事件名称。

`事件参数(Map)` value可以是基本类、自定义类。当自定义类添加注解`@EventBean`时，将在事件直接引用传递（也就是参数将被事件收听者更改）,例如：
否则将粗暴传递，即：事件返回值直接覆盖原有值
```java
@Table(name = "cp_user")
@EventBean
public class CpUser extends BasePo {
}
```


`是否异步执行(boolean)` 的含义是：
1. **异步执行**时，广播者不会等到各个收听者执行完毕再继续执行，而是广播请求成功后，就继续了。收听者对事件参数的修改，广播者是不知道的，而且收听者的异常不会影响到广播者,也不会影响到下一个收听者。
2. **同步执行**时，广播者将一直等待直到所有收听者都执行完毕再往下执行。收听者对事件参数的修改将影响到广播者（加了注解`@EventBean`的）,收听者的异常将导致广播者产生异常并中断后续收听者的执行,并根据已经执行的收听者的配置回滚事务。
3. **无论同步还是异步**,收听者都按照顺序执行，同时下一个收听者将得到上一个修改后的事件参数。当广播者产生异常时，所有收听者都将根据配置来回滚事务。

`是否强制执行(boolean)` 的含义是：
是否要求所有收听者都必须执行?如果是，即使收听者下线了，调度中心会缓存本次事件，直到收听者上线并成功调用为止;此时如果`async=true`，广播者也将一直等待收听者上线。
如果否，离线的收听者将收不到事件;

`期望收听者数量(int)`的含义是：期望有多少个收听者?0表示不知道,如果大于0，且收听者与这个数字不符，将抛出异常，导致广播失败


调用`EventBoard`方法时，可能会产生异常，这里的异常是来自收听者的异常；如果不处理这些异常，意味这广播者的事务将回滚。

```java
    Map<String,Object> params = new HashMap<>();
    params.put("user",record);
    params.put("count",1);

    EventUtil.EventBoard("shop_regedit",params,false,true,0);
```

##事件收听

### 准备工作

在启动项目中初始化事务环境`EventUtil.Ready()`,并扫描事件收听者包:`EventUtil.EventScan`(依托于`spring`的对象池)


### 事件收听者

1. 收听者必须在`@EventListenerService`注解的类中,这个类不需要通过`spring`初始化,在`准备工作`中会自动初始化并装配`@Autowired`属性.但是不支持`dubbo`的`@Reference`,所以事件收听者应该在`service`中，而不是`controller`中(或者在不使用dubbo服务的controller中);在这个类上还需要添加事务支持的注解：`@EnableTransactionManagement`,例如

```java
@EnableTransactionManagement //事务支持
@EventListenerService  //收听类标记
public class EventTest {
	@Autowired
    private CpuserMapper cpuserMapper;
}
```

2. `@EventListener`注解的方法表示一个事件收听者.每个方法可以有多个`@EventListener`, 事件收听者方法的参数必须是一个`Map<String,Object>`，不能多、不能少；返回值也必须是`Map<String,Object>`,例如


```java
@Transactional
@EventListener(event = "shop_regedit", rollBackMethod = "unAddUser")
public Map<String,Object> addUser( Map<String,Object> param){
}
```


4. `@EventListener`用来配置收听者如何监听事件，必传参数有: `event`和`rollBackMethod`;注解的参数解释如下：
`event(String)`表示收听的事件名称,需要和广播者约定好,收听者能否收到事件的唯一判断标准就是事件名称。

`rollBackMethod`表示手动回滚事务的方法名,回滚方法要求参数只有一个`Map<String,Object>`,返回值要求是`void`

`order(int)` 表示收听顺序，同一个`event`的所有收听者将按照`order`先后收听，默认-1

`rollBackEventSourceBySelf(boolean)` 表示当自身出现异常时，是否回滚事件源;如果事件源广播事件是异步的，那么此配置无效；默认true

`rollBackSelfByEventSource(boolean)` 表示当事件源以抛出异常时，是否回滚自身？默认true

`rollBackSelfByEvent(String[])`表示哪些事件抛出异常后，回滚自身?（不包括自己的事件名）;如果此参数是空数组，表示**所有的事件**都回滚

`rollBackSelfBySelfEvent(boolean)`表示自己的事件名其他的收听者抛出异常后，是否回滚自身？默认true


一个完整的例子：

```java
@EnableTransactionManagement
@EventListenerService
public class EventTest {
    @Transactional
    @EventListener(event = "shop_regedit", rollBackMethod = "unAddUser")
    @EventListener(event = "shop_regedit2", rollBackMethod = "unAddUser")
    @EventListener(event = "shop_regedit3", rollBackMethod = "unAddUser")
    public Map<String,Object> addUser( Map<String,Object> param){
	    Map<String,Object> result = new HashMap<>();
	    //类型可以直接转换
        CpUser user = (CpUser) param.get("user");
	    //此方法抛出异常，将影响事件传播链

	    //例子在这里收听店铺注册事件，并新增一个钱包记录
	    FlWallet flWallet = new FlWallet();
	    flWallet.setUserid(user.getUserid());
	    flwalletMapper.insertSelective(flWallet);
        //将要回滚事务所需要的参数
	    result.put("id",flWallet.getFlid());

	    //再更新一条记录
	    GlAgent glAgent = glAgentMapper.selectByPrimaryKey("");
	    //缓存修改之前的数据
	    result.put("glAgent",glAgent);
	    glAgent = BeanUtils.copyBean(glAgent,GlAgent.class);
	    glAgentMapper.updateByPrimaryKeySelective(glAgent);
	    return result;
    }

    /**
    事件回滚方法，参数是addUser返回的结果
    **/
    @Transactional
    public void unAddUser(Map<String,Object> param){
        int id = MapUtils.getIntValue(param,"id");
        //类型直接转
        GlAgent glAgent = (GlAgent)param.get("glAgent");
        //更新原先的记录
        glAgentMapper.updateByPrimaryKeySelective(glAgent);
        //删除记录
        flwalletMapper.deleteByPrimaryKey(id);
    }
}
```

## 关于异常

目前异常没有做分级，广播者无法区分监听者抛出的异常是哪种异常。正常情况下，监听者抛出的业务型异常的文字描述信息将抛给广播者