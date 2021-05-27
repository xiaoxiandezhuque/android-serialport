# android-serialport

Android串口，基于官方的串口通信，在此基础上做了一定的封装，支持实体类的发送和接收，自动切换线程到子线程去处理

## 使用步骤

### 第一步：`app/build.gradle`进行依赖

```groovy
implementation 暂时没有
```

### 第二步：定义实体类

```java
// 接收数据
public class DeviceResBean {
    @ByteOccupy(order = 1)//order接收数据的解析顺序；count指占用的字节数量，默认1
    public String address;

    @TypeCompare(hexString = "04")//根据不同类型，来确定实体类的解析方式
    @ByteOccupy(order = 2)
    public int type;
    
    @ByteOccupy(order = 3)
    public int length;

    @ByteOccupy(order = 4, variableLenName = "length")//支持变长
    public List<ContentBean> content;

    @ByteOccupy(count = 2, order = 5)
    public String crc;
}
//写入数据，不支持变长，是固定长度
public class WriteBean {
    @ByteOccupy(order = 1)
    public String address;

    @ByteOccupy(order = 2)
    public int function;

    @ByteOccupy(order = 3, count = 2)
    public int registerStartBit;

    @ByteOccupy(order = 4, count = 2)
    public ContentBean numberOfRegisters;

    @ByteOccupy(order = 5, count = 2)
    public String crc;
}
```

### 第三步：配置返回类型和回调接口

```kotlin
val xhSerialPort = XhSerialPort()
xhSerialPort.addResultType(
    DeviceResBean1::class.java,
    DeviceResBean::class.java,
    DeviceResBean2::class.java//可以配置多个
)
xhSerialPort.addCallBack(object : Callback {
    override fun suc(any: Any) {
        //接收到的数据，自行判断类型处理
    }
})
```

### 第四步：连接串口

```kotlin
xhSerialPort.connect(ConfigBean("/dev/ttyS4", 9600))
```

### 第五步：使用完毕之后，关闭串口

```kotlin
xhSerialPort.close()
```

### 补充说明

目前支持的类型：

1. byte、byteArray
2. short
3. int
4. string——只支持16进制的字符串形式
5. arrayList
6. 对象类型——必须使用ByteOccupy确定解析的顺序和占用

具体使用可以参考dome里面代码和源码

## 版权声明

* 所有原创文章(未进行特殊标识的均属于原创) 的著作权属于 **xiaoxiandezhuque**。
* 所有转载文章(标题注明`[转]`的所有文章) 的著作权属于原作者。
* 所有译文文章(标题注明`[译]`的所有文章) 的原文著作权属于原作者，译文著作权属于 **xiaoxiandezhuque**。

#### 转载注意事项

除注明外，所有文章均采用 [Creative Commons BY-NC-ND 4.0（自由转载-保持署名-非商用-禁止演绎）](http://creativecommons.org/licenses/by-nc-nd/4.0/deed.zh)协议发布。

你可以在非商业的前提下免费转载，但同时你必须：

* 保持文章原文，不作修改。
* 明确署名，即至少注明 `作者：xiaoxiandezhuque` 字样以及文章的原始链接，且不得使用 `rel="nofollow"` 标记。
* 微信公众号转载一律不授权 `原创` 标志。