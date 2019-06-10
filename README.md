# data-clean
数据清洗-计算引擎
springBoot2+springBatch+springJPA+thymeleaf
####介绍

日常工作中,经常会有针对某个用户或者某个订单做变量清洗的场景,而变量对应的清洗逻辑需要经常修改.
本项目就是针对这种应用场景设计的数据的批处理程序.项目主要使用的框架为 springboot+springbatch+springjpa,
对外提供数据批处理的RESTful接口.数据清洗脚本采用groovy语法脚本配置.

例如:已有用户数据 {"userInfo":{"name":""zhangsan","age":"28","sex":"male"},
"orders":[{"time":"2019-05-05 12:11:11","amount":110.32,"products":[{"prodName":"fruit","number":3,"prices":3.2}...]}...]}
现在需要计算用户在最近30天内的购买商品的金额,数量,种类等等.
本工程可以动态配置好每个变量的清洗脚本,通过接口访问后,实时放回计算好的变量.

#### 适用场景
- 风控场景:需要根据用户的风控数据清洗出用户的特征变量,后续根据用户变量做决策
- 报表场景:计算报表中独立指标,指标的生成关系可以是相互依赖多层次的
- 用户参数配置: 根据用户数据和计算公式,动态调整用户的一些参数

#### 使用帮助
- git clone本工程.
- 先 mvn clean install ,先生成JPA需要的Q... 等辅助类.
- 配置好mysql 数据源 (application.properties 文件只需要配置数据源,jpa会自动建表). 
- 启动本工程,启动类:com.my.zhj.cloud.Application.
- 指标管理界面入口: http://127.0.0.1:8222/user/login  登录账号 admin/admin


#### 指标配置说明

执行 src/main/resources 下面的 demon.sql 后,程序会生成如下的groovy脚本并加载
```groovy
// import 部分
import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.lang3.time.DateUtils ;

// functions 部分
def add(def x,def y){
        x+y
};

// indicator 部分
public Object TEST_my_add(Map<String, Object> data, Map<String, Object> res){ 
 add(data.aa,data.bb) 
};
```
指标配置有三块组成
import,function,indicator 分别对应 dpm_imports,dpm_funcitons,dpm_indicators 的数据

配置这些数据后,服务对外提供计算 TEST 类型的数据, 传入 {"aa":1.1,"bb":2.1} ,计算结果 {"my_add":3.2}

- swagger地址:
http://localhost:8222/swagger-ui.html

- 请求参数:
curl -X POST "http://localhost:8222/data-clean/api/v1/task/execution/TEST?transId=1234" -H "accept: */*" -H "Content-Type: application/json" -d "{\"aa\":1.1,\"bb\":2.1}"
- 返回参数:
{"my_add":3.2}

#### 指标配置界面说明

![登录](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/login.png)

![指标类型管理](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/dpmType.png)

![指标导入类管理](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/import.png)

![指标函数管理](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/function.png)

![指标管理](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/indicator.png)
指标计算可以使用两个参数 : data:传入的原始数据 ,res:前面层级计算的指标结果 

![用户管理](https://github.com/zj41545423/data-clean/blob/master/src/main/resources/static/img/manageImage/user.png)



#### Groovy 语法简要说明
http://www.groovy-lang.org/documentation.html#gettingstarted <BR>
https://www.w3cschool.cn/groovy/ <BR>
http://wiki.jikexueyuan.com/project/groovy-introduction/



#### 项目亮点
- 最新的springboot 和springbatch 版本,全部基于注解配置,去掉繁琐的xml
- springbatch 原来的应用场景是数据库批处理任务,修改 数据源为内部默认hsqldb,同时针对接口可能传入的较大数据做了适配
(修改batch默认表结构,src/main/resource/batch-sqldb.sql restFul接口支持 gzip压缩)
- 原生微服务架构,方便扩展嵌入 springcloud , consul,zukua 等服务注册组件,组成微服务调用.
- web接口使用FastJson替换原来的HttpmessageConvert,FastJson会默认把请求参数中的浮点数据转换成 BigDecimal,Groovy脚本中默认会把数字类型用BigDecimal做运算,保证浮点数据运算的精度.(java 中 1d-0.99d = 0.010000000000000009)
- 效率 500个指标,5M左右的请求数据,响应时间在1s左右.
- 实时返回,其他业务方便扩展功能