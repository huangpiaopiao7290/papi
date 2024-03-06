<a name="vMYmG"></a>

# 项目介绍

> 用户可以登录后，可以开通接口调用权限，每次使用会进行统计，管理员可以进行接口的上线与删除，可视化。

![image.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1708426323139-a8239ee1-3d46-4e16-8140-51e50f9b07e9.png#averageHue=%23f9f9f9&clientId=u84ca714f-f4f7-4&from=paste&height=518&id=UCgz2&originHeight=544&originWidth=735&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=26754&status=done&style=none&taskId=u66646122-69d4-498e-b9e4-f462d742824&title=&width=700.0000317891453)
<a name="oae7i"></a>

## 后端

<a name="fbvyj"></a>

### 项目初始化

1. 拉后端模板
2. 改配置
3. 启动 测试

---

- 改pom.xml
  - 项目名、其他信息、安装依赖
- 修改配置 application.yml等 
  - 数据库连接配置
  - 项目启动端口号
  - ...
- 数据库配置
  - jdbc
  - 创建数据库
  - 执行sql脚本
- 测试
  - 运行项目，访问http://localhost:7291/api/doc.html
  - ok

<a name="XlH0s"></a>

#### 数据库表设计

可按照管理系统设计，`本系统最基本功能是接口管理`

```sql
-- 接口信息
create table if not exists papi.`interface_info`
(
    `id` bigint not null auto_increment comment 'id' primary key,
    `name` varchar(256) not null comment '名称',
    `description` varchar(256) null comment '描述',
    `url` varchar(256) not null comment '接口地址',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态 0-关闭 1-开启',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除 0-未删除 1-已删除'
) comment '接口信息';
```

<a name="eMJOU"></a>

#### 增删改查代码

能少写绝不多写，安装 myatisX，生成对表interface_info的CRUD代码

1. 右键interface_info表，点击myatisX-generator
2. 然后点点点点点
3. 会生成一个generator目录，将球其分别迁移到应该去的位置
   1. service目录下的接口与实现类去springbootinit下的service
   2. domain下的InterfaceInfo去model.entity
   3. mapper下的去springbootinit下的mapper
4. 修改resources下的mapper的InterfaceInfoMapper.xml的namesapce

```sql
<mapper namespace="com.yupi.springbootinit.mapper.InterfaceInfoMapper">
```

5. 后端初始化模板里面有现成的增删改查代码，直接copy一份改一改
   1. 复制controller下的PostController到controller目录下重命名为InterfaceController
   2. 进入InterfaceController.java中，选择访问路径 `post`进行替换（ctrl+R）`interfaceInfo`，将 `Post`替换成  `InterfaceInfo`，将 `InterfaceInfoMapping`替换成 `PostMapping`
   3. 在model.dto下创建package`interfaceInfo`，将同级post下的所有文件到前者，将文件名中的 `Post`换成 `InterfaceInfo`
   4. 在package `entity`下的InterfaceInfo字段 `isDelete`上添加 `@TableLogic`（逻辑删除， 并非真的删除了）

```java
/**
 * 添加接口信息
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态 0-关闭 1-开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

```java
/**
 * 接口信息查询
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    // @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态 0-关闭 1-开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

```java
/**
 * 接口信息修改
 * 
 */
@Data
public class InterfaceInfoEditRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态 0-关闭 1-开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

6. 修改InterfaceController.java爆红的地方

```java
package com.yupi.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.yupi.springbootinit.model.entity.InterfaceInfo;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.InterfaceInfoService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口信息接口
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest 新增接口信息
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        String name = interfaceInfoAddRequest.getName();
        if (name != null) {
            interfaceInfo.setName(JSONUtil.toJsonStr(name));
        }
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 校验参数
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        // 判断待更新的api接口是否存在
        User loginUser = userService.getLoginUser(request);
        Long currentInterfaceIdForUpdating = interfaceInfoUpdateRequest.getId();
        InterfaceInfo oldInterfaceId = interfaceInfoService.getById(currentInterfaceIdForUpdating);
        if (oldInterfaceId == null) {
            // 待更新的api接口已经不存在了
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅限本人或管理员可以修改
        if (!oldInterfaceId.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            // 既不是本人也不是管理员
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 更新API接口信息
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtil.copyProperties(interfaceInfoQueryRequest, interfaceInfo);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfo);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        // 接口请求信息为空时
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // interfaceInfoQueryRequest --> interfaceInfo
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfo);
        long current = interfaceInfoQueryRequest.getCurrent();          // 当前页号
        long size = interfaceInfoQueryRequest.getPageSize();            // 当前页面大小
        String sortField = interfaceInfoQueryRequest.getSortField();    // 排序字段
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();    // 排序顺序
        // description需要支持模糊查询
        String description = interfaceInfo.getDescription();
        interfaceInfo.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfo);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }
    
    // region end增删改查

    /**
     * 发布api接口 仅限管理员
     *
     * @return
     */
//    @PostMapping("/online")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
//                                                     HttpServletRequest request) {
//
//        return null;
//    }


}

```

7. 启动后端，测试接口是否正常

暂时没发现问题（在上面前后端对接回来的，数据库表写的对不上，小问题）
<a name="ShsvF"></a>

#### 小总结

平平无奇

<a name="b2W3q"></a>

### 接口调用

初始化项目中展示的接口数据都是假的，先开发一个真实的接口作为测试，命名为 `papi-client`

> get接口
> post接口（url传参）
> post接口（restful）

```java
package com.yupi.papiclient.controller;

import com.yupi.papiclient.model.User;
import org.springframework.web.bind.annotation.*;

/**
 * 名字接口
 * 测试
 */
@RestController
@RequestMapping("/name")
public class NameController {

    /**
     * 获取用户名字
     * get
     * @param name
     * @return
     */
    @GetMapping("/")
    public String getNameByGet(String name) {
        return "Get your name is " + name;
    }

    /**
     * 获取用户名字
     * post url
     * @param name
     * @return
     */
    @PostMapping("/")
    public String getNameByPost1(@RequestParam String name) {
        return "Post url your name is " + name;
    }

    /**
     * 获取用户名字
     * post restful
     * @param user
     * @return
     */
    @PostMapping("/user")
    public  String getNameByPost2(@RequestBody User user) {
        return "Post restful your name is " + user.getUserName();
    }
}

```

```java
package com.yupi.papiclient.model;

import lombok.Data;

/**
 * 用户
 * 金测试
 */
@Data
public class User {
    private String userName;
}
```

<a name="q9FNJ"></a>
#### 
<a name="aYSxC"></a>

#### 开发者调用接口

作为开发者不应该在地址栏或者测试工具调用接口，应该在前端或者后端调用接口，作为后端开发怎么调用？出于安全性考虑：引入sdk，调用api

```java
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.9</version>
        </dependency>
    </dependencies>
```

在`papi-client`中新建 `client`层：负责与用户交互，处理用户请求，以及调用服务端提供的api接口
<a name="o2eRL"></a>

##### API签名

为开发者提供接口，但是开发者不是谁都可以，所以在开发者调用之前需要对其身份进行验证。

> accessKey 与 secretKey
> **签发签名**
> 加密方式：
> 对称、非对称、md5 .......
> 用户参数  + 密钥 ==签名生成算法(md5、sha256、.......)==》 不可解密的一大串东西
> **校验签名**
> 服务端用一样的参数和算法生成签名，比较是否和用户传来的一致
> **1、是否合法**
> <br />
> **2、防篡改**
> <br />
> **3、防重放（重复请求）**
> 1) 加nonce
> nonce 是由请求方生成的随机数（在规定的时间内保证有充足的随机数产生，即在60s 内产生的随机数重复的概率为0）也作为参数之一加入 sign 签名。
> 服务器接受到请求先判定 nonce 是否被请求过(一般会放到redis中)，如果发现 nonce 参数在规定时间是全新的则正常返回结果，反之，则判定是重放攻击。
> 2) 加时间戳 timestamp
> timestamp由请求方生成，代表请求被发送的时间（需双方共用一套时间计数系统）随请求参数一并发出，并将 timestamp作为一个参数加入 sign 加密计算。
> 平台服务器接到请求后对比当前时间戳，设定不超过60s 即认为该请求正常，否则认为超时并不反馈结果（由于实际传输时间差的存在所以不可能无限缩小超时时间）。 但是这样仍然是仅仅不够的，仿冒者仍然有60秒的时间来模仿请求进行重放攻击。所以更进一步地，可以为sign 加上一个随机码（称之为盐值）这里我们定义为 nonce。


<a name="bsfDf"></a>

#### 开发SDK

开发者引入后，可以直接在.yml中写配置

删除pom.xml中部分配置，这是maven构建项目的方式，目前是要构建依赖包，而不是运行jar包

```java
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

`papi-client`删掉主类，创建配置类 `PapiCLientConfig.java`

```java
// 告诉spring这是一个配置类
@Configuration
// 读取application.yml的配置，读取到配置之后，把读到的配置设置到我们这里的属性中
// 给所有的配置加上前缀papi.client
@ConfigurationProperties("papi.client")
@Data
// 自动扫描组件 spring自动注册相应的bean
@Component
public class PapiClientConfig {
    private String accessKey;
    private String secretKey;

    public PClient pClient() {
        return new PClient(accessKey, secretKey);
    }
}
```

> **在 **`**META-INF**`**目录中创建一个文件 **`**spring.factories**`

```java
#starter
org.springframework.boot.autoconfigure.EnableAutoConfiguration=
com.yupi.papiclient.PapiClientConfig
```

springboot将会在应用启动时自动加载和实例化PapiClientConfig<br />然后打包

```java
    <groupId>com.yupi</groupId>
    <artifactId>papi-client</artifactId>
    <version>0.0.1</version>
```

哐叱哐哧测试一下

- starter怎么识别注解的，写配置时提示
  - spring-configuration-metadata.json
    <a name="e0PBp"></a>

### 接口发布/下线

发布接口：这个接口需要执行哪些任务呢?首先需要验证接口是否存在，然后判断接口是否可调用，否则访问接口都是404，影响用户体验。接着，如果接口可以调用，我们需要修改数据库中该接口的状态为1，表示接口已经被发布，状态默认为O(关闭)。<br />下线接口：你可以为其新增一个状态字段。例如，使用1表示开启，使用2表示下线。通过这个新字段，可以清晰地区分接口状态。当状态为0时，表示该接口还没有进行任何处理，看大家自己的考虑。我们这里就直接使用0和1来表示状态，不再添加额外的状态字段，大家可以按照自己的需求进行设计。对于下线接口，校验接口是否存在也是和发布接口类似的，但是下线接口无需判断接口是否可调用。<br />另外，还需注意的一点是仅管理员可操作这两个接口，这点需要特别注意，防止用户越权操作。以上就是这两个接口的基本设计。

> 发布接口
>
> 1. 检验接口是否存在
> 2. 判断接口是否可以正常使用
> 3. 修改接口的状态
>
> 下线接口
>
> 1. 检验接口是否存在
> 2. 修改接口的状态

- 在controller `interfaceInfo` 中新建两个方法：上线/下线（把更新接口哪个复制两遍改一改）
- 身份校验 通过注解 `@AuthCheck`，具体实现过程是通过切面的方式

```java
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}


```

- 在 `pom.xml`中引入api接口依赖，并配置

```java
# api接口配置
papi:
  client:
    access-key: papi
    secret-key: secret
```

- 启动项目，测试目前是否能跑通 
  <a name="vS0gU"></a>

### 在线调用

修改注册功能，为每个用户生成 `accessKey`和 `secretKey`，更新user表、更新跟User相关的所有类: User、UserMapper.xml

```typescript
-- 更新用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    accessKey varchar(512) not null comment 'accessKey',
    secretKey varchar(512) not null comment 'secretKey',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;
```

```typescript
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        // 密码和校验密码不同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }
```

创建测试接口

```typescript
    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        PClient tempClient = new PClient(accessKey, secretKey);
        Gson gson = new Gson();
        String usernameByGet = tempClient.getNameByGet("pp");
        return ResultUtils.success(usernameByGet);
    }
```

> 用户调用测试接口：
>
> 1. 告诉后端当前用户的签名信息来判断其是否有权限


接口表新增 `requestParams`:请求参数<br />add... ==》  model:interfaceInfo ==》model：dto：interfaceInfoAddRequest、interfaceInfoUpdateRequest、interfaceEditRequest  ==》InterfaceInfoMapper.xml<br />前端展示：添加请求参数栏

<a name="Rdizq"></a>

### 接口调用次数

> 需求：
>
> 1. 用户每次调用成功，次数+1
> 2. 给用户分配或用户自主申请接口调用次数

涉及一个新的表用来存储 用户 和 调用接口 之间的关系

```java
-- 用户调用接口关系表
create table if not exists papi.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户 id',
    `interfaceInfoId` bigint not null comment '接口 id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常，1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';
```

根据系统的量级和需求，我们可以选择使用日志存储来记录接口调用信息，而不是直接存储在数据库中。这样可以更好地管理和分析接口调用数据。<br />**总调用次数**:指用户从第一次开通接口开始至今累计的调用次数。<br />**剩余调用次数**:指用户每次吆买接口后利全的可国田出球知识库<br />**状态字段(status)**:决定是否允许其调用特定接口。<br />总调用次数是一直累加的，记录了用户在整个使用期间的累计调用次数。无论用户购买多少次接口，总调用次数都会随着每次调用的增加而增加。<br />剩余调用次数则是在用户购买接口后，根据购买的次数和已使用的次数计算得出的。每次购买接口都会增加一定的调用次数，而每次实际调用接口后，剩余调用次数会相应减少。<br />为了增加安全性，我们可以考虑为每个用户设置一个状态字段(status)，来决定是否允许其调用特定接口。这样，如果用户触发了某些规则或违反了规定，我们可以将其状态设置为不允许调用该接口。通过添加一个状态字段，我们可以灵活地管理用户对接口的访问权限。例如，当用户违反规则时，我们可以限制其对某些接口的调用，而对其他接口仍然保持开放。这个状态字段可以帮助我们实现精确的接口访问控制。

---

通过mybatisX-generate插件根据表生成代码<br />![image.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1708754951196-1c97af04-6acf-45c7-8fe1-f5a37ce1a831.png#averageHue=%233d4246&clientId=u3af0a7e2-8f28-4&from=paste&height=565&id=ub907c53c&originHeight=593&originWidth=1037&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=87691&status=done&style=none&taskId=u7218c6d1-e70c-48c2-a2fa-0f67b349a9e&title=&width=987.6190924698553)<br />将生成的generate目录中的代码迁移到项目中...<br />为其创建一个controller，直接将InterfaceInfoController复制一份，咔咔一顿操作。。。

为了确保用户每次调用接口成功时次数加一，而不是随意增加某个接口的调用次数，我们应该将增加调用次数的逻辑放在接口调用成功的业务逻辑之后，而不是将其对外暴露。<br />通过这样的设计，我们可以控制用户只能通过正确的接口调用来增加次数，而不允许随意调用接口进行次数操作。这样可以保证接口调用的准确性和安全性，防止滥用和不当操作。

- 对于这个添加功能，也可以给它添加管理员权限。我们先完整地开发这个controller，保留添加功能。这样，如果将来需要扩展功能，也会更方便一些。
- 像这种更新接口调用关系方面，可能在将来需要这样的功能。例如，管理员需要为用户增加接口调用次数或者分配额外的次数。
- 等等。先保留InterfaceInfoController的各种接口

创建UserInterfaceInfoController中的各种请求类。。。

```java
/**
 * 用户调用接口关系
 * @TableName user_interface_info
 */
@TableName(value ="user_interface_info")
@Data
public class UserInterfaceInfo implements Serializable {

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

```java
/**
 * 接口信息查询
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
//    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

```java
/**
 * 接口信息更新
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {
    /**
     * 主键
     */
//    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
```

```java
package com.yupi.springbootinit.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.annotation.AuthCheck;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.DeleteRequest;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.constant.UserConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import com.yupi.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.yupi.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserInterfaceInfo;
import com.yupi.springbootinit.service.UserInterfaceInfoService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * api接口管理
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 新增用户接口关系信息
     *
     * @param userInterfaceInfoAddRequest 用户接口关系新增对象
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> adduserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        // 将userInterfaceInfoAddRequest copy到 userInterfaceInfo
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        // 校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);

        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        Long newUserInterfaceInfoId = userInterfaceInfo.getUserId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteuserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 校验参数
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);

        // 判断待更新的api接口是否存在
        User loginUser = userService.getLoginUser(request);
        Long currentInterfaceIdForUpdating = userInterfaceInfoUpdateRequest.getId();
        UserInterfaceInfo oldInterfaceId = userInterfaceInfoService.getById(currentInterfaceIdForUpdating);
        if (oldInterfaceId == null) {
            // 待更新的api接口已经不存在了
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅限本人或管理员可以修改
        if (!oldInterfaceId.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            // 既不是本人也不是管理员
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 更新API接口信息
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userInterfaceInfo);
    }

    /**
     * 获取列表（仅管理员）
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserInterfaceInfo>> listuserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtil.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfo);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfo);
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(userInterfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfo>> listuserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
            HttpServletRequest request) {
        // 接口请求信息为空时
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        // userInterfaceInfoQueryRequest --> userInterfaceInfo
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfo);
        long current = userInterfaceInfoQueryRequest.getCurrent();          // 当前页号
        long size = userInterfaceInfoQueryRequest.getPageSize();            // 当前页面大小
        String sortField = userInterfaceInfoQueryRequest.getSortField();    // 排序字段
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();    // 排序顺序
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfo);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInterfaceInfoPage);
    }

    // region end增删改查
}

```

<a name="YStIL"></a>

#### 实现用户调用成功次数加一

找到用户调用接口的位置，在模拟接口中 `papi-interface`

```java
updateWrapper.eq("interfaceInfoId", interfaceInfoId);
updateWrapper.eq("userId", userId);
updateWrapper.setSql("leftNum = leftN   um - 1, totalNum = totalNum + 1");
```

在这里需要注意的是，由于用户可能会瞬间调用大量接口次数，为了避免统计出错，需要涉及到事务和锁的知识。在这种情况下，如果我们是在分布式环境中运行的，那么可能需要使用分布式锁来保证数据的一致性。<br />事务是一组操作的集合，要么全部成功，要么全部失败回滚。在这个场景中，我们希望在更新用户接口信息的时候，保证原子性，即要么用户接口信息全部更新成功，要么全部不更新。<br />锁的作用是为了防止多个线程或进程同时修改同一个数据，造成数据不一致的情况。在分布式环境中，我们需要使用分布式锁来确保在多个节点上对数据的访问是互斥的。<br /> 然而，在这里的代码中，并没有实现事务和锁的逻辑。这里只是演示了整体的流程，并没有具体实现细节。所以,如果要在实际项目中应用这个功能，还需要进一步考虑并实现事务和锁的机制，以确保数据的一致性和安全性。

问题：每次调用金额口成功后，都需要执行 `invokeCount`方法
<a name="vus9V"></a>

#### aop切面

> 问题: 如果每个接口的方法都写调用次数+1，是不是比较麻烦?
> 致命问题: 接口开发者需要自己去添加统计代码。

**AOP切面的优点**: 独立于接口，在每个接口调用后统计次数＋1<br />**AOP切面的缺点**: 只存在于单个项目中，如果每个团队都要开发自己的模拟接口，那么都要写一个切面。

图片说明<br /> 

> **aop切面与filter的区别**
> 过滤器，拦截器拦截的是URL。<br />AOP拦截的是类的元数据(包、类、方法名、参数等)。
>
> - 过滤器并没有定义业务用于执行逻辑前、后等，仅仅是请求到达就执行。
> - 拦截器有三个方法，相对于过滤器更加细致，有被拦截逻辑执行前、后等。
> - AOP针对具体的代码，能够实现更加复杂的业务逻辑。
>
> 三者功能类似，但各有优势，从过滤器–》拦截器–》切面（AOP），拦截规则越来越细致。 执行顺序依次是过滤器、拦截器、切面（AOP）。
> 过滤器使用场景
>
> - 统⼀设置编码
> - 过滤敏感字符
> - 登录校验
> - URL级别的访问权限控制
> - 数据压缩
>
> 拦截器使用场景
>
> - ⽇志记录
> - 权限校验
> - 登录校验
> - 性能检测[检测⽅法的执⾏时间]
> - 其实拦截器和过滤器很像，有些使⽤场景。⽆论选⽤谁都能实现。需要注意的使他们彼此的使⽤范围，触发机制。
>
> 切片（AOP）
>
> - 更加精确细致的业务场景


在我们当前的项目中，我们需要实现的统计功能涉及多个项目之间的调用，而不仅仅是单个项目内的统计。虽然AOP切面是一个不错的解决方案，但它有一个**缺点:它是独立于单个项目的，每个项目都需要自己实现统计逻辑，并引入相应的AOP切面包**。<br />考虑到我们的项目架构，我们希望实现一种通用的统计方案，可以统一处理所有项目的接口调用情况。因此，我们决定采用网关来实现这个功能。修改刚刚的架构图:
<a name="FRXcs"></a>

#### 网关

考虑到我们的项目架构，我们希望实现一种通用的统计方案，可以统一处理所有项目的接口调用情况。因此，我们决定采用网关来实现这个功能。<br />为了避免这种情况，将统计次数的功能再抽出来一层。我们可以将统计次数的逻辑放在一个公共的位置，就像进入火车站一样，无论你乘坐哪趟列车，都需要经过这个统一的检票口。同样地，无论哪个模拟接口被调用，都会经过这个统—的统计次数逻辑。

看spring官网去：[https://docs.spring.io/spring-cloud-gateway/reference/index.html](https://docs.spring.io/spring-cloud-gateway/reference/index.html)

**网关的应用场景:**<br />**路由**:路由实际上就像一个中转站，类似于我们的路由器。

- 现在我们再看一下上面的图示。假设用户要访问某个接口A，但现在用户不需要直接调用接口A，而是通过我们的网关统一接收用户的请求。网关记录了用户调用的接口，并将其转发到对应的项目和接口进行处理，有点类似于前台接待。
- 路由在这里起到了转发的作用。举个例子，假设我们有接口A和接口B，网关会记录这些信息，并根据用户访问的地址和参数，将请求转发到对应的接口(服务器/集群)。为了更好地理解，我们可以设置以下示例路由:如果用户访问接口A，网关将转发请求到接口A;如果用户访问接口B，网关将转发请求到接口B。这种转发过程就叫做路由。此外，还有一种情况是我们后面可能对接到一个集群。比如，当用户访问接口C时，网关可以将请求转发给服务A或者集群中的某个机器。在集群中，请求可能会随机转发到其中的某个机器上。

**统一鉴权**:判断用户是否有权限进行操作，无论访问什么接口，我都统—去判断权限，不用重复写 。

- 之前的鉴权逻辑写在yuapi-interface这个项目中的方法里，用于判断用户是否有权限进行操作。但是如果每个方法都要单独写鉴权逻辑，显然是不可行的。所以我们决定将鉴权逻辑和统计次数一样，抽取出来放到网关里面。
- 在网关中，鉴权的重点是实现统一鉴权。无论用户要访问哪个接口，网关都会统—判断权限，不需要重复编写鉴权逻辑，这是网关的强调点之一。网关的作用在很多方面都是强调统一性，将重复的逻辑进行抽象和集中。

**统—处理跨域**:网关统一处理跨域，不用在每个项目里单独处理。

- 在开发单个Spring Boot项目或Web项目时，跨域问题是一个常见的挑战。特别是在接口项目中，可能存在多个项目如项目A、项目B等，每个项目都可能面临跨域问题。如果每个项目都要单独处理跨域，就会出现重复劳动的情况。
- 为了避免重复的跨域处理，我们可以将跨域处理逻辑统一放到网关中，让网关来帮助我们处理跨域问题。这样，项目A和项目B就不再需要单独处理跨域，而是统一由网关处理。这是一种统一处理跨域的方法。

**访问控制**:黑白名单，比如限制DDOS IP。

- 访问控制，又称为黑白名单，实际上也是一种权限控制机制。它与鉴权有一些区别。鉴权通常指授权，即判断用户是否有访问某种资源的权限。而黑白名单则主要用于判断每个用户是否可以访问特定资源，它是一种与业务逻辑独立的控制方式。
- 举个例子，如果有人恶意刷我们的流星，进行DDOS攻击，我们可以将这些恶意IP加入黑名单，限制它们的访问。这样，这些IP就无法访问我们的服务，从而保护了我们的接口和服务不受恶意攻击。

**发布控制:**灰度发布，比如上线新接口，先给新接口分配20%的流量，老接口80%，再慢慢调整比重。

- 举个例子，假设我们的团队开发了一个名为项目A的接口A，现在我们要对接口A进行升级，推出一个新版本的接口A-V2。但我们并不确定新版本是否稳定可靠，所以我们想先让一部分用户试用这个新接口。我们可以将流量按照比例划分，比如80%的流量继续访问旧版本的接口A，而20%的流量则引导到新版本的接口A-V2。这样就实现了灰度测试的效果。然后我们会观察V2的表现，如果测试没有问题，我们可以逐步增加流星比例，比如50%、70%、80%，直到100%。最后，当我们确认新版本的接口稳定可靠时，就可以完全替换掉旧版本，下线接口A。
- 这个流量分配的过程就是发布控制，而它通常是在网关层进行。因为网关是整个流量的入口，所以它可以担当请求流星分配的角色。通过在网关层进行发布控制，我们能够更加灵活地控制用户访问不同版本接口的比例,而无需在每个服务中进行单独处理。这种方式让我们能够更加安全和可靠地进行接口的升级和发布。

**流量染色**:给请求（流量）添加一些标识，一般是设置请求头中，添加新的请求头。

- 流量染色是什么意思呢?我们举个例子来解释。假设现在有一个用户要访问我的接口。但是有一个问题，我希望用户不能绕过网关直接调用我的接口，我想要防止这种情况发生。那么我应该如何防止绕过网关呢?一个方法是要确定请求的来源。我们可以为用户通过网关来的请求打上一个标识，比如添加一个请求头source=gateway。只要经过网关的请求，网关就会给它打上source=gateway的标识。接口A就可以根据这个请求头来判断，如果请求没有source=gateway这个标识，就直接拒绝掉它。这样，如果用户尝试绕过网关，没有这个请求头的话，我们的项目就不会认可它。这就是流星染色的一种应用。流量染色还有其他应用，比如区分用户的来源，这和鉴权是不同的概念，属于不同的应用场景。
- 另外一个常见的应用是用于排查用户调用接口时出现的问题。我们为每个用户的每次调用都打上一个唯一的traceid，这是分布式链路追踪的概念。通过这个traceid，当出现问题时，下游服务可以根据traceid追踪到具体的请求，从而逐层排查问题。这也是流量染色的作用之一。

> 提问： 用户怎么绕过网关


全局网关 与 业务网关：[https://blog.csdn.net/qq_21040559/article/details/122961395](https://blog.csdn.net/qq_21040559/article/details/122961395)<br />网关选型：[https://zhuanlan.zhihu.com/p/500587132](https://zhuanlan.zhihu.com/p/500587132)<br />还是建议先看下官网文档（使用说明书）再看看别人总结的文章

小结：<br />核心：路由、断言、过滤

---

**实现网关功能**<br />新建网关接口 `papi-gateway`
:::success
该项目可能用到的网关的特性

1. **路由:**肯定会用到路由功能。为什么呢?因为现在用户原本是直接请求模拟接口，然后再进行鉴权。但**现在我们要让用户请求我们的网关，然后由网关将请求重定向到模拟接口项目**。在这个过程中，鉴权是在网关中完成的，所以路由转发请求肯定是要用到的。
2. **负载均衡**:(暂时不会演示)负载均衡需要一个注册中心的支持。使用Nachos 或者Eureka等不同的注册中心可能会稍微麻烦一些。这个可以在学习完Spring Cloud微服务之后再来实践，就修改一下服务地址而已。
3. **统一鉴权**:还记得我们API开放平台项目是如何进行鉴权的吗?我们使用了accessKey、secretKey 来进行鉴权，并非通过session 获取用户信息。因为我们的鉴权是请求级别的，而不是基于会话级别的。
4. **跨域**:如果需要用到跨域，将在适当的时候进行处理。跨域实现其实相对简单，可以通过配置方式或编程式配置实现，集中在网关层面解决。
5. **统—业务处理**:例如给用户的所有查询添加一层缓存。对于我们的项目，我们主要用到两个统一的业务处理，用户鉴权和接口调用次数统计。我们需要记录每次调用接口成功后，调用次数加一，从而统计总共调用次数(在API网关中实现)。
6. **访问控制**:这里更多指黑白名单的使用。举个例子，如果有用户发现系统漏洞并不断刷接口，一秒钟调用数十万次或数百万次，这显然不行的。这种情况下，我们可以将该用户的IP地址或访问密钥(AKSK)加入黑名单，进行访问控制，类似于防火墙的功能。访问控制也可以涉及不同权限的许可，与鉴权略有相似但也有区别，需要细细品味。
7. **发布控制**:(暂时不会演示，因为我们已经在之前的直播中讲过并实践过了，像灰度发布这类的访问控制)免费版和企业版之间也可看作一种访问控制，鉴权一般是判断是否有权限执行某个操作，这些大家可以在上次的直播回放中学习了。
8. **流量染色**:就是判断一个请求是否经过我们的网关，若用户绕过网关直接请求模拟接口，相当于绕过防火墙直接进入服务器，因此我们可以记录请求是否来自网关，并在请求中添加相应标识。但是要实现这个功能，还是需要在最终被调用的接口层面进行判断。是否实现流星染色要看实际情况，有利有弊。一般情况下，我们进行流量染色是为了链路追踪。举个例子，你的一个接口可能A调用B，B调用C，C再调用D，形成了一条很长的调用链。为了更好地追踪一个请求的完整过程，我们需要知道每次调用中的请求是从哪里发过来的。因此，我们通常会给请求打上一个唯一的标识，而流量染色的作用就是实现这种功能。尽管这个概念可能有点抽象，但只要在遇到对应的场景时，能想起这个东西就可以了。
9. **接口保护**:(不需要过于关注，因为会涉及一些微服务的知识，大家学完微服务之后再去深入了解会更好理解)不过我们可以简单地理解它为一种兜底的策略，或者说是一种防止接口出现问题后的补救方法。举个例子，如果我的接口受到攻击，那么我们可以强制进行降级处理。原本接口是实际调用模拟接口的，但现在我们可以将其降级为直接返回一个失败结果或者给用户一个提示，告知他稍后再试。这样可以提供用户一个稍微友好的提示，也能在一定程度上增强用户体验。总比抛出一串500错误代码或者乱码要好得多。
10. **统一日志**:记录每次请求和响应的日志。
11. **统—文档**:(暂时不会演示，因为这个相对来说并不复杂)以前可能会使用Swagger等整合网关的方式来实现统一集合文档。但现在有更简单的方式，不需要使用微服务网关进行整合。我们可以直接引入Knife4j文档库，并按照它的方式进行配置即可。所以这部分没什么特别需要讲解的，可以参考官方文档。
    :::

- 业务流程
  - 用户发送请求到api网关
  - 请求日志
  - 白（黑）名单
  - 用户鉴权（判断accessKey、secretKey是否合法）
  - 判断请求的模拟接口是否存在
    - 存在就调用模拟接口
    - 调用成功，调用次数+1
    - 调用失败，返回错误码
  - 响应日志

改造模拟接口 `papi-interface`

```java
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
        // todo 实际情况应该是去数据库中查是否已分配给用户
//        if (!accessKey.equals("papi")) {
//            throw new RuntimeException("无权限");
//        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
        // todo 时间和当前时间不能超过 5 分钟
        // todo 实际情况中是从数据库中查出 secretKey  
        // todo 调用次数 + 1 invokeCount
        return "POST 用户名字是" + user.getUserName();
    }
}

```

访问：http://localhost:8123/api/name/get?name=pp 可以跑通  

---

- 创建 `papi-gateway`
  - 注意依赖版本

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.13</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <!-- Generated by https://start.springboot.io -->
    <!-- 优质的 spring/boot/data/security/cloud 框架中文文档尽在 => https://springdoc.cn -->
    <groupId>com.yupi</groupId>
    <artifactId>papi-gateway</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>papi-gateway</name>
    <description>papi-gateway</description>
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2021.0.5</spring-cloud.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>


```

- 添加全局过滤器
- 在 `papi-gateway`中调用 `papi-backed`中的获取数据库数据接口
  - HTTP请求（RestTemplate、Feign等）
  - RPC（dubbo等）

1. **提供者(Producer/Provider)**:首先，我们需要一个项目来提供方法，这个项目被称为提供者。它的主要任务是为其他人提供已经写好的代码，让其他人可以使用。举例来说，我们可以提供一个名为invokeCount的方法。
2. **调用方(Invoker/Consumer)**:一旦服务提供者提供了服务，调用方需要能够找到这个服务的位置。这就需要一个存储，用于存储已提供的方法，调用方需要知道提供者的地址和invokeCount方法，这里需要一个公共的存储。
3. **存储**:这是一个公共存储，用于存储提供者提供的方法信息。调用方可以从这个存储中获取提供者的地址和方法信息，例如，提供者的地址可能是123.123.123.1，而方法是invokeCount，这些信息会存储在这个存储器中。调用方可以从存储中获取信息后，就知道调用invokeCount方法时需要访问123.123.123.1，这就是RPC的基本流程，这三个角色构成了整个RPC模型。

存储有时也会被称为**注册中心**，它管理着服务信息，包括提供者的IP地址等等。调用方从这里获取信息，以便进行调用。

---

- 下载nacos、解压缩
- 启动

```java
startup.cmd -m standalone
```

- 在papi-gateway、papi-backed引入依赖

```java
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>3.0.9</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.alibaba.nacos/nacos-client -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>2.3.0</version>
        </dependency>
```

- papi-backed 作为提供者
- papi-gateway 作为调用者

---

:::success
报错啦<br />服务已经注册到nacos，但调用者老是说 no provider
:::

```java
Nacos Config Metadata : dataId='test', groupId='DEFAULT_GROUP', beanName='papiGatewayApplication', bean='null', beanType='class com.yupi.papigateway.PapiGatewayApplication', annotatedElement='null', xmlResource='null', nacosProperties='{encode=UTF-8, serverAddr=127.0.0.1:8848, enableRemoteSyncConfig=false}'
result：piaopiao
```

按照nacos官网配置，仅从nacos中获取注册数据是可行的，但和dubbo一起使用的时候，报错中显示 no providr .... <br />暂停（去拿个驾照先，回来再搞，屮）<br />回来了，19:33，开整<br />![屏幕截图 2024-03-01 210038.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1709298074253-76468342-d678-48f7-97e0-ea8761787ed9.png#averageHue=%23fbfbfa&clientId=uf1b54ca6-6cb6-4&from=paste&height=674&id=uf7e29494&originHeight=708&originWidth=1920&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=94054&status=done&style=none&taskId=uf8c37434-482b-4d4c-9c14-8912ce0c54f&title=&width=1828.5715116124613)

```java
Failed to check the status of the service com.yupi.provider.DemoService. 
No provider available for the service com.yupi.provider.DemoService from the url 
consumer://192.168.0.104/com.yupi.provider.DemoService
?application=dubbo-springboot-consumer&background=false&dubbo=2.0.2
&interface=com.yupi.provider.DemoService&methods=sayHello&pid=13568
&register.ip=192.168.0.104&release=3.0.9&side=consumer&sticky=false
&timestamp=1709225205798 to the consumer 192.168.0.104 use dubbo version 3.0.9
```

> 排查方式： 
>
> 1. 先查 provider ：情况比较少：一般未注册
> 2. consumer端：
>    1. 检查配置
>    2. 查consumer订阅的信息
>    3. 看报的啥：the service com.yupi.provider.DemoService

看nacos图片注册的服务是 com.yupi.springbootinit.provider.DemoService 当然找不到服务咯<br />所以官网的demo是将公共接口单独出来的

```java
org.apache.dubbo.common.cache.FileCacheStoreFactory$PathNotExclusiveException

result: Hello world
```

**总结：**

1. 调用者和提供者中的共同使用接口必须在同一个目录下（与注册中心中路径包名路径一致）
2. 配置
3. 依赖冲突
   <a name="C6GJN"></a>

### 抽象公共服务

上面使用dubbo真的是难受，明明那么简单的一个问题搞半天，屮。所以把需要公用的接口单独出来

> papi-common
> 作用：让方法、实体类在多个项目中服用，减少重复编写，方便调用

- 抽取公共部分

1. 查询用户密钥
2. 查询接口是否存在
3. 接口调用次数统计

---

新建maven项目 `papi-common`

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.13</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.yupi</groupId>
    <artifactId>papi-common</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.2</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.2</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.9.0</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.30</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- https://mvnrepository.com/artifact/junit/junit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>

    </dependencies>

</project>
```

1. 将 `papi-backed`中的可以复用的部分单独出来
2. 打包（记得打包的时候排除common中使用的依赖）
3. 引入到要使用的项目中
4. 启动 `papi-backed` ~ ok
5. 在`papi-gateway`如法炮制

---

- 进入 `papi-backed`实现抽象出来的公共部分的接口

```java
/**
 * 内部接口信息实现
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    /**
     * 获取接口信息
     * @param path url
     * @param method 方法名
     * @return
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        // 参数校验
        if (StringUtils.isAnyBlank(path, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 创建查询条件包装器
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>();
        interfaceInfoQueryWrapper.eq("url", path);
        interfaceInfoQueryWrapper.eq("method", method);
        // 查询selectOne()
        return interfaceInfoMapper.selectOne(interfaceInfoQueryWrapper);
    }
}

====================

/**
 * 内部用户接口关系信息实现类
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 接口调用次数加1
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}

==================

/**
 * 内部用户接口实现
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    private UserMapper userMapper;

    /**
     * 查询是否给用户分配密钥
     * @param accessKey 公钥
     * @return
     */
    @Override
    public User getInvokerUser(String accessKey) {
        // 参数校验
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 创建查询条件包装器
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("accessKey", accessKey);
        // 查询selectOne()
        return userMapper.selectOne(userQueryWrapper);
    }
}

```

- 注意之前写的更新接口调用次数的功能，设置组合索引，提高速度

```java
-- 将用户调用接口关系表中的 userid 和 interfaceId 设置成组合索引
alter table `user_interface_info` add index `idx_user_and_interface_id`(`userId`, `interfaceInfoId`);
```

- 进入 `papi-gateway`完善全局过滤

```java
package com.yupi.springbootinit;

import com.yupi.papiclient.utils.SignUtil;
import com.yupi.papicommon.model.entity.InterfaceInfo;
import com.yupi.papicommon.model.entity.User;
import com.yupi.papicommon.service.InnerInterfaceInfoService;
import com.yupi.papicommon.service.InnerUserInterfaceInfoService;
import com.yupi.papicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, GatewayFilterChain, Ordered {

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    private static final String INTERFACE_HOST = "http://localhost:8123";
    // 白名单
    private static final List<String> IP_WHITE_LIST = List.of("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // todo 用户发送请求到api网关
        // 1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();

        // 2.访问控制---黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            // 禁止访问
            response.setStatusCode(HttpStatus.FORBIDDEN);
            // 标记响应已结束后序过滤器无需再处理
            return response.setComplete();
        }
        // 用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // todo 实际情况是去数据库中查是否已经分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokerUser(accessKey);
        } catch (Exception e) {
            log.error("获取授权用户异常", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        // 实际情况中是从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.genSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
        // 4. 请求的接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // todo 是否还有调用次数
        // 5. 请求转发，调用模拟接口 + 响应日志
        //        Mono<Void> filter = chain.filter(exchange);
        //        return filter;
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());

    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        //释放掉内存
                                        DataBufferUtils.release(dataBuffer);
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + sb2);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 用户无权限
     * @param response 响应
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

```

- 打开 `papi-client`引入 `papi-common-sdk`
- 启动前端，完整测试能否跑通





> - **question：**用户自己上传接口？
> - **answer-ref：** 
>   - 提供注册机制，用户上传自己编写的接口信息、服务器地址、请求路径等等
>   - 对上传的接口进行审核
>   - 用户决定是否用我们的网关，需要按照我们提供的sdk和文档来开发
>   - 用户上传的api接口的存储，需要在接口信息表中新增 `host`字段 ，用于区分不同服务器地址
>   - ......


:::tips

- **回顾用户点击**`**调用**`**按钮前后发生了什么？**

1. 用户注册，会为其创建 aksk
2. 从当前登录用户获取aksk（所以application.yml中配不配置aksk都无所谓）。然后，用户点击调用按钮 (访问`/invoke`)
3. 进入 `papi-client`的 `PClient.getUsernameByPost(username)`方法
4. 在`getUsernameByPost(xxx)`方法中向 `papi-interface`发起请求（访问 http://localhost:8123/xxx/xxx）在请求头中添加用于身份鉴权的参数，最主要参数的是`accessKey`与 `sign`
5. 步骤4会被网关`papi-gateway`的全局拦截器拦截，从步骤3中封装的请求头中取出用 `secretKey`生成的签名`sign`。然后根据用户的`accessKey`查找到其对应的`secretKey`。然后用`body`与刚取出来的`secretKey`生成签名/摘要`sign_new`，然后比较请求头中的`sign`与`sign_new`进行比较是否一致。
   1. 不一致说明网络传输中，信息被修改过，有内鬼终止交易！
   2. 一致说明这个请求正常，`body`中的信息在网络传输中未被修改过。网关进行其他校验操作。
6. 网关放行后，进入`papi-interface`调用对应的方法。
7. 得到返回结果，over。
   :::

```java
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        // 密码和校验密码不同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

```

```java
    /**
     * 测试调用
     * papi-client中的getUsernameByPost()方法
     *
     * @param interfaceInfoInvokeRequest 测试接口id 请求参数userRequestParams
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        // 获取用户输入的请求参数
        // {"username":"pp", "type":string}
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断对应id的接口是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口状态
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 获取用户签名信息
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        // 创建临时的papiClient对象, 并传入accessKey\secretKey
        PClient tempClient = new PClient(accessKey, secretKey);
        Gson gson = new Gson();
        //
        com.yupi.papiclient.model.User userName = gson.fromJson(userRequestParams, com.yupi.papiclient.model.User.class);
        String usernameByPost = tempClient.getUsernameByPost(userName);
        return ResultUtils.success(usernameByPost);
    }
```

```java
    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        return httpResponse.body();
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 生成签名/摘要 用于与accessKey生成的比对
        hashMap.put("sign", SignUtil.genSign(body, secretKey));
        return hashMap;
    }
```

```java
        // 从数据库中查出当前用户的secretKey
        String secretKey = invokeUser.getSecretKey();
        // 用secretKey对内容进行签名生成摘要
        String serverSign = SignUtil.genSign(body, secretKey);
        // 比对用accessKey生成的摘要
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
```

```java
    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        return "POST 用户名字是" + user.getUserName();
    }
```

---

<a name="mGaO9"></a>

### 数据统计与分析

- 创建接口使用情况表
- 在`papi-backed`创建数据分析的`controller`
- <br />

<a name="t4g6l"></a>

## 前端

<a name="sy03Y"></a>

### 项目初始化

1. 按照ant design pro官方文档初始化前端项目
2. 查看本地环境版本，安装依赖
3. 直接启动项目 yarn run start / dev

---

<a name="IZS2N"></a>

#### 删除模板不必要的文件

- 先提交代码，防止删除后出现问题

```
git init
git add .
// add时显示当前文件夹的所有者不一致 
// 1.将目录的所有者改掉
// 2.按git推荐 将改目录加入safe.directory
git commit -m '说明信息'

```

- 移除国际化

```
直接执行 yarn run i18n-remove 报错

正确步骤：
yarn add eslint-config-prettier --dev yarn add eslint-plugin-unicorn --dev 
然后修改node_modules/@umijs/lint/dist/config/eslint/index.js文件注释
// es2022: true
yarn run i18n-remove
```

- 移除测试工具

直接删除tests目录

<a name="gpJrh"></a>

#### 前端代码自动生成调用后端接口

通过Ant Design Pro支持的openapi插件，为openapi插件提供其规范的接口文档，实现接口的自动生成。

- 查看后端swagger文档的主页，分组url：[http://localhost:7291/api/v2/api-docs](http://localhost:7291/api/v2/api-docs)
- 打开前端项目，在config目录下的config.ts中找到openapi

```typescript
/**
   * @name openAPI 插件的配置
   * @description 基于 openapi 的规范生成serve 和mock，能减少很多样板代码
   * @doc https://pro.ant.design/zh-cn/docs/openapi/
   */
openAPI: [
  {
    requestLibPath: "import { request } from '@umijs/max'",
    // 或者使用在线的版本
    // schemaPath: "https://gw.alipayobjects.com/os/antfincdn/M%24jrzTTYJN/oneapi.json"
    schemaPath: join(__dirname, 'oneapi.json'),
    mock: false,
  },
  {
    requestLibPath: "import { request } from '@umijs/max'",
    schemaPath: 'https://gw.alipayobjects.com/os/antfincdn/CA1dOm%2631B/openapi.json',
    projectName: 'swagger',
  },
],
```

```typescript
  /**
   * @name openAPI 插件的配置
   * @description 基于 openapi 的规范生成serve 和mock，能减少很多样板代码
   * @doc https://pro.ant.design/zh-cn/docs/openapi/
   */
  openAPI: [
    {
      requestLibPath: "import { request } from '@umijs/max'",
      // 在线json文档地址
      schemaPath: 'http://localhost:7291/api/v2/api-docs',
      // 项目名称
      projectName: 'papi-backed',
    },
  ],
```

- 在package.json中找到openapi

```typescript
yarn run openapi
```

- 在src目录下面生成的service目录

<a name="tNGIW"></a>

#### 修改请求配置

src目录下的requestErrorConfig.ts

```typescript
将请求配置文件名requestErrorConfig改成requestConfig

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const requestConfig: RequestConfig = {
  // 后端请求地址
  baseURL:'http://localhost:7291',

在同级目录下的app.ts中引用
```

前端启动项目用 `dev`模式： `yarn run dev`<br />![image.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1707978920834-12391486-56ef-498d-a563-875c0c40f846.png#averageHue=%23e2e1d5&clientId=u6a8db566-a727-4&from=paste&height=252&id=u2ee2aa39&originHeight=265&originWidth=1149&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=49209&status=done&style=none&taskId=u97687773-2d95-4d4e-b4ae-0a5bba26419&title=&width=1094.2857639805823)<br />请求的接口地址与后端定义的不一致，需要修改脚手架生成的接口地址

- 找到src/pages/User/Login/index.tsx的提交函数，将登录接口改成service里的。然后哪里报错改哪里。然后把输入的用户名密码改成自己后端定义的名称
  <a name="Zjc1P"></a>

#### 尝试前后端对接

淦！看图请求发送成功为啥不跳转页面，再看控制台打印的信息{code:5000,...}，赶紧看看后台<br />![屏幕截图 2024-02-15 215500.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1708015115929-d8176688-0bd3-4f22-b9d5-d5fb72726054.png#averageHue=%23e4cd98&clientId=u27dac18c-ae55-4&from=paste&height=375&id=ua8ae9cf1&originHeight=394&originWidth=1009&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=61920&status=done&style=none&taskId=ud0b281e5-ced2-427d-887a-2194e252858&title=&width=960.9524245921737)

```java
### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column 'unionId' in 'field list'
### The error may exist in com/yupi/springbootinit/mapper/UserMapper.java (best guess)
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: SELECT  id,userAccount,userPassword,unionId,mpOpenId,userName,userAvatar,userProfile,userRole,createTime,updateTime,isDelete  FROM user   WHERE  isDelete=0  AND (userAccount = ? AND userPassword = ?)
                                                                                                                                                                           ### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'unionId' in 'field list'
```

定义的User实体类里面的属性值与数据库中user表的不一致，直接重新创建user表。<br />哐叱哐哧，后台显示登录成功，页面也提示登录成功，但还是页面不跳转--------视频说的是没有记录用户的登录态（2:18:52）

- 用户在app.tsx中getInitialState()，去typings.d.ts中定义getInitialState返回的对象类型

```java
/**
 * 全局状态
 */

interface InitialState {
  loginUser?: API.UserVO;
}
```

```typescript
// 首次访问页面的时候会调用这个getInitialState()方法获取
// 修改返回的结果类型
export async function getInitialState(): Promise<InitialState> {
  const userState: InitialState = {
    loginUser: undefined,
  };
  try {
    // 获取登录用户
    const res = await getLoginUserUsingGet();
    if (res.data) {
      userState.loginUser = res.data;
    }
  } catch (error) {
    history.push(loginPath);
  }
  return userState;
}

把下面currentUser改成loginUser，等等

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  // 后端请求地址
  baseURL: 'http://localhost:7291/',
  // 忽略cookie时默认为false
  withCredentials: true,
  ...requestConfig,
};
```

```typescript
try {
  // 用户登录
  const msg = await userLoginUsingPost({
    ...values,
  });
  // 判断返回msg对象中是否有data属性
  if (msg.data) {
    const urlParams = new URL(window.location.href).searchParams;
    // 将用户数据设置到全局变量
    setInitialState({
      loginUser: msg.data,
    });        
    // 将用户重定向到'redirect' redirect不存在就重定向到'/'
    history.push(urlParams.get('redirect') || '/');
    return;
  }
} catch (error) {
  const defaultLoginFailureMessage = '登录失败，请重试！';
  console.log(error);
  message.error(defaultLoginFailureMessage);
}
```

> 瞎改瞎改???又tm莫名其妙好了--------------动了一个地方，将设置全局变量放到重定向前面

后面又回来了<br />需要 `登录两次`才可以登录上去，问题应该还是是设置登录状态 getInitialState()  那。在app.tsx的layout中切换页面的逻辑

```typescript
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login    登录态为空会重新跳转到登录页
      if (!initialState?.loginUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
```

> 原因：
> ant design pro官方文档中 setInitialState() 方法是异步更新的 
> 解决方式：
>
> - 方式一：设置登录态后不立即跳转，设置一个延迟时间
>   - 延迟时间应该设置多久合适？
> - 方式二：将恩如态信息用同步更新的方式
>   - flsuhSync() : [https://zh-hans.react.dev/reference/react-dom/flushSync](https://zh-hans.react.dev/reference/react-dom/flushSync)

```typescript
在index.tsx中的handleSubmit方法中将重定向

        // 将用户数据设置到全局变量
        setInitialState({
          loginUser: msg.data,
        });  
        setTimeout(()=>{
          const urlParams = new URL(window.location.href).searchParams;
          // 将用户重定向到'redirect' redirect不存在就重定向到'/'
          history.push(urlParams.get('redirect') || '/');
        }, 100);
```

```typescript
        flushSync(()=>{
          setInitialState({
            loginUser: msg.data,
          });
        });
```

<a name="NtLHb"></a>

#### 头像转圈圈

在components里的AvatarDropdown.tsx中把所有currentUser换成loginUser，name换成userName<br />在图床上上传图像图片，保存其链接：[https://i0.imgs.ovh/2024/02/16/o0Xks.jpeg](https://i0.imgs.ovh/2024/02/16/o0Xks.jpeg)
<a name="K0y0L"></a>

#### 用户注销（退出登录）

模仿用户登录，将框架的接口换成后端定义的退出登录接口<br />还是在AvatarDropdown.tsx中

```typescript
  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        flushSync(() => {
          setInitialState((s) => ({ ...s, loginUser: undefined }));
        });
        // 改成后端的注销方法
        // loginOut();
        userLogoutUsingPost();
        // 退出后跳转到登录页面
        const {search, pathname} = window.location;
        const redirect = pathname + search;
        history.replace('/user/login', {redirect});
        return;
      }
      history.push(`/account/${key}`);
    },
    [setInitialState],
  );
```

<a name="MpNQu"></a>

#### 小总结

写前端代码是变少了，但tm的杀鸡用牛刀，什么功能都塞里面，写的少了，但tm出现报错改都不知道咋改---压根不会react，看文档咯，多看多写多想

<a name="xRTPr"></a>

### api接口页面

<a name="YF8Vj"></a>

#### 接口管理

前端怎么做权限区分的，ant design pro内置了一套权限管理机制，找到access.ts<br />后端给用户设置不同角色

```typescript
/**
 * @see https://umijs.org/docs/max/access#access
 * */
export default function access(initialState: { loginUser?: API.LoginUserVO } | undefined) {
  const { loginUser } = initialState ?? {};
  return {
    canUser: true,
    canAdmin: loginUser && loginUser.userRole === 'admin',
  };
}
```

将TableList页面改成接口管理页面（仅管理员可见）

```typescript
const columns: ProColumns<API.InterfaceInfo>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      tip: 'index',
      // render: (dom, entity) => {
      //   return (
      //     <a
      //       onClick={() => {
      //         setCurrentRow(entity);
      //         setShowDetail(true);
      //       }}
      //     >
      //       {dom}
      //     </a>
      //   );
      // },
    },
    {
      title: '创建者ID',
      dataIndex: 'userId',
      valueType: 'textarea',
    },
    {
      title: '接口名称',
      dataIndex: 'name',
    },
    {
      title: '描述',
      dataIndex: 'description',
      valueType: 'textarea',
    },
    {
      title: 'url',
      dataIndex: 'url',
      valueType: 'text',
    },
    {
      title: '请求头',
      dataIndex: 'requestHeader',
      valueType: 'textarea',
    },
    {
      title: '响应头',
      dataIndex: 'responseHeader',
      valueType: 'textarea',
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      valueEnum: {
        0: {
          text: '关闭',
          status: 'Default',
        },
        1: {
          text: '开启',
          status: 'Processing',
        },
      },
    },
    {
      title: '请求方法',
      dataIndex: 'method',
      valueType: 'textarea',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          key="config"
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          配置
        </a>,
        <a key="subscribeAlert" href="https://procomponents.ant.design/">
          订阅警报
        </a>,
      ],
    },
  ];
```

```typescript
// 向后端请求数据
request={listInterfaceInfoByPageUsingPost}
```

request在什么时候会被触发？【由组件控制】

1. 刚打开页面或者加载表格时
2. 手动点击刷新
3. 点击查询按钮

```typescript
    request?: (params: U & {
        pageSize?: number;
        current?: number;
        keyword?: string;
    }, sort: Record<string, SortOrder>, filter: Record<string, (string | number)[] | null>) => Promise<Partial<RequestData<DataSource>>>;
```

```typescript
        // 向后端请求数据
        request={async (params, sort: Record<string, SortOrder>, filter: Record<string, React.ReactText[] | null>) => {
          const res = await listInterfaceInfoByPageUsingPost({
            ...params,
          })
          if (res?.data) {
            return {
              data: res?.data.records || [],
              success: true,
              total: res.total,
            }
          }
        }}
```

---

创建模态框，直接将UpdateForm.tsx复制一份,再改吧改吧

```typescript
import {ProColumns, ProTable} from '@ant-design/pro-components';
import '@umijs/max';
import {Modal} from 'antd';
import React from 'react';
export type FormValueType = {
  target?: string;
  template?: string;
  type?: string;
  time?: string;
  frequency?: string;
} & Partial<API.RuleListItem>;
// 定义组件接收的参数
export type Props = {
  // 表格列
  columns: ProColumns<API.InterfaceInfo>[];
  // 点击取消按钮时触发
  onCancel: ()=>void;
  // 用户提交表单时，将用户输入的数据作为参数传给后台
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  // updateModalOpen: boolean;
  // 模态框是否可见
  visible: boolean
  // values不用传递
  // values: Partial<API.RuleListItem>;
};
const CreateModal: React.FC<Props> = (props) => {
  // 使用解构赋值获取props中的属性
  const {visible, columns, onCancel, onSubmit} = props;
  return (
    // 创建一个Modal组件，通过visible属性控制其显示或隐藏
    <Modal visible={visible} footer={null} onCancel={()=>onCancel?.()}>
      <ProTable
        type="form"
        columns={columns}
        onSubmit={async (value) => {
          onSubmit?.(value);
        }}
      />
    </Modal>
  );
};
export default CreateModal;

```

用户在修改表格信息时，打开修改表单时，直接将表格信息显示在框内，用户只用修改想要修改的地方。<br />把CreateModal引入到接口管理页面<br />全局响应拦截器

```typescript
  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response as unknown as ResponseStructure;
      console.log('data', data);
      if (data.code !== 0) {
        throw new Error(data.message);
      }
      return response;
    },
  ],
```

<a name="gSCHL"></a>

#### 新建

1. 新建模态框中 `onsubmit`将表单信息提交到外层

```java
         onSubmit={async (value) => {
          onSubmit?.(value);
        }}
```

2. 在外层 `index.tsx`将提交接口换成service中定义好的 

```java
const handleAdd = async (fields: API.RuleListItem) => {
  const hide = message.loading('正在添加');
  try {
    // 向后端请求新建
    await addInterfaceInfoUsingPost({
      ...fields,
    });
    hide();
    message.success('Added successfully');
    return true;
  } catch (error) {
    hide();
    message.error('Adding failed, please try again!');
    return false;
  }
};
```

![image.png](https://cdn.nlark.com/yuque/0/2024/png/27431211/1708328074937-aa67916b-423b-401e-a49b-07772384b6f6.png#averageHue=%23f8f8f8&clientId=u8bcdf81a-3c5d-4&from=paste&height=981&id=u3d198318&originHeight=1030&originWidth=1920&originalType=binary&ratio=1.0499999523162842&rotation=0&showTitle=false&size=258796&status=done&style=none&taskId=u9098dada-f532-4d2b-a0ac-962ee49b778&title=&width=1828.5715116124613)
<a name="Kqj0Q"></a>

#### 发布/上线

新建 `发布``下线` 按钮

```typescript
 record.status == 0 ?
<a
  key="config"
  onClick={() => {
    handleOnline(record);
  }}
>
  发布
</a> : null,
record.status == 1 ?
<a
  key="config"
  onClick={() => {
    handleOffline(record);
  }}
>
  下线
</a> : null,
```

<a name="YrguW"></a>

#### 浏览页

- 路由

> Umi 应用都是单页面应用，页面地址的跳转都是在浏览器端完成的，不会发起网络请求从服务器端获取 HTML。HTML 只在应用初始化时加载一次，后续路由的变化都是利用了浏览器的 API 实现。

将原来的欢迎页放到 `page/Index`目录下重命名为 `index.tsx`

```typescript
export default [
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' }],
  },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', name: '二级管理页', component: './Admin' },
    ],
  },
  { name: '查询表格', icon: 'table', path: '/list', component: './TableList' },
  { path: 'welcome', name: '欢迎', icon: 'smile', component: './Index'},
  { path: '*', layout: false, component: './404' },
];

```

```typescript
import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import { Card, List, message } from 'antd';
import {listInterfaceInfoByPageUsingGet} from '@/services/papi-backed/interfaceController'
import { useMatch } from 'react-router';
import { Descriptions } from 'antd/lib';

/** 主页
 * @constructor
 */
const Index: React.FC = () => {
  // 加载状态
  const [loading, setLoading] = useState(false);
  // 列表数据
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [data, setData] = useState<API.InterfaceInfo>();
  // 总数
  const [total, setTotal] = useState<number>(0);
  // 列表单页大小
  const singlePageSize = 10;

  // 使用useMatch钩子将当前的url与指定路径模式进行匹配 /interface_info:id
  const match = useMatch('/interface_info/:id');

  const loadData = async (current = 1, pageSize = singlePageSize) => {
    setLoading(true);
    try {
      const res = await listInterfaceInfoByPageUsingGet({
        current,
        pageSize,
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageContainer title="在线接口开放平台">
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          return (
            <List.Item actions={[<a key={item.id} href={apiLink}>查看</a>]}>
              <List.Item.Meta
                title={<a href={apiLink}>{item.name}</a>}
                description={item.description}
              />
            </List.Item>
          );
        }}
        pagination={{
          // eslint-disable-next-line @typescript-eslint/no-shadow
          showTotal(total: number) {
            return '总数：' + total;
          },
          pageSize: singlePageSize,
          total,
          onChange(page, pageSize) {
            loadData(page, pageSize);
          },
        }}
      />
    </PageContainer>
  );
};

export default Index;

```

```
{/* 接口信息不存在时展示 */}
      <Card>
        {data ? (
          <Descriptions title="接口详细信息测试">
            <Descriptions.Item label="UserName">Zhou Maomao</Descriptions.Item>
            <Descriptions.Item label="Telephone">1810000000</Descriptions.Item>
            <Descriptions.Item label="Live">Hangzhou, Zhejiang</Descriptions.Item>
            <Descriptions.Item label="Remark">empty</Descriptions.Item>
            <Descriptions.Item label="Address">
              No. 18, Wantang Road, Xihu District, Hangzhou, Zhejiang, China
            </Descriptions.Item>
          </Descriptions>
        ):(
          <>接口不存在</>
        )}
      </Card>
```

在 `page`目录下新建 `InterfaceInfo`目录用于展示欢迎页的中接口详细信息，并修改路由配置  

```typescript
export default [
  { name: '接口页', icon: 'smile', path: '/', component: './Index'},
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' }],
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', name: '二级管理页', component: './Admin' },
    ],
  },
  { name: '接口管理', icon: 'table', path: '/list', component: './TableList' },
  { 
    name : '接口信息', 
    icon: 'smile', 
    path: '/interface_info/:id', 
    component: './InterfaceInfo',
    hideInMenu : true
  },
  { path: '*', layout: false, component: './404' },
];

```

<a name="mMXsX"></a>

### 在线调用

接口详情页新增 `调用`按钮。


<a name="B5YOm"></a>

### 接口使用情况展示

<a name="RamXL"></a>

#### 图表

<a name="Zklo8"></a>

#### 数据分析页

可以把智能BI那个放进来


<a name="hMrCL"></a>

# 项目上线

> 准备工作

1. 准备一台云服务器（之前买的腾讯云的，但阿里云掀桌子了，大降价）
2. 注册个域名，还要工信部备案（不想这么备案那上一步就别买国内的服务器了）
3. 配置好云服务器

---



<a name="qJHx6"></a>

# review

<a name="pZocd"></a>

### 改进方向

作为个人项目，打算长期做下去，以作为技术深入的引子。<br />后期会开发些方便实用的api上线到这里，比如最近碰到的帮初中老师弄pdf（扫描件提取文字）当时急着要，真的是纯手动的，用ocr工具一张一张识图复制粘贴，累死了。可以整个`papi-pdfWithoutCopy-ocr-suoha`【转图片-提取文本-输出为可编辑文本文件】一气呵成，打包这些繁琐且重复的操作，解放初中老师的双手，让课件资料不再头疼......
