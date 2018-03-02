## 引子

最近使用在做dialer应用的时候，在启动活动的过程中，不知遇到了多少启动失败，传值错误等等的各种问题，现在专门开一篇，对`Intent`做一个小结。

## Intent
Intent是Android程序中各个组件之间进行交互的一种重要方式，它不仅可以指明当前组件想要执行的动作，还可以在不同组件之间传递数据。Intent一般可被用于启动活动、启动服务以及发送广播等。

### 显式Intent

Intent有多个构造函数的重载，具体可以看Intent源码，常用的一个重载如下：
```Java
public Intent(Context packageContext, Class<?> cls) {
    mComponent = new ComponentName(packageContext, cls);
}
```
第一个参数用于提供一个启动活动的上下文，第二个参数指定这个活动要启动的目标活动，一个简单的启动过程如下：
```Java
Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
startActivity();
```
这就是`FirstActivity`活动的基础上打开`SecondActivity`。

### 隐式Intent
相比与显示Intent，隐式Intent并不明确的指定我们启动那个活动，而是指定一些抽象的action和category信息，然后由系统去分析该启动什么活动，要想使用隐式Intent，我们需要在对应`<activity>`标签中配置相关的<intent-filter>内容，用以指明开启什么类型的活动，例子如下：
```
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="com.example.acitivtytest.ACTION_START" />

                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>
```

然后通过如下方式调用
```Java
Intent intent = new Intent("com.example.acitivtytest.ACTION_START");
startActivity(intent);
```
这样就SecondActivity就能够响应这样的intent的，成功跳转到SecondActivity。

### 其它
上面这两种方式都是intent的最简单的用法，然而在实际使用中，很多时候并不是这样的，假设我现在需要打电话，电话是系统应用，权限问题假设已经申请好了，这时候我们想要启动打电话这个活动，就得使用另外一种构造方式。
```Java
 public Intent(String action, Uri uri) {
        setAction(action);
        mData = uri;
    }
```
第一个参数是一个动作，动作种类很多，具体可以参考源码或者相关文档，打电话的动作是ACTION_CALL，第二个参数是传入的数据，数据格式是Uri格式，表明这个动作操作在什么数据上，打电话的格式为`”tel:xxxxxxxxxx"`,指明了电话号码。

那么，一个简单的打电话Intent操作就可以如下：

```Java
Intent intent = new Intent(Intent.ACTION_CALL, "tel:"+number);
startActivity(intent);
```

这只是一个打电话，有时候我们还要访问各种系统相关的数据或者应用，大部分都是通过这种方式进行调用的。

### Intent传入数据与获取数据

许多时候，我们进行Activity的跳转只是简简单单的一个页面跳转就能解决的，我们可能需要传递某些数据，根据上一个数据来决定新的Activity的具体运行方式和页面效果，使用intent的传递数据方式如下：

```Java
String data = "data";
Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
intent.putExtra("put_data", data);
//public @NonNull Intent putExtra(String name, String[] value);
startActivity();
```
第一个参数是数据的名字，第二个参数是数据的内容，实际中，传递的数据多种多样，还可以通过Serializable的方式传递对象。

获取数据就更方便了
```Java
Intent intent = getIntent();
String data = intent.getStringExtra("put_data");
```
直接使用对应类型的获取数据的方法就能获取到数据了。

## 小结
关于Intent，简单的内容大概就是这点了，实际上intent的使用还会遇到很多坑，个人在使用中，最容易出现问题的就是在访问系统应用相关页面时传入的Data和action，这一点要多多积累，遇到问题即使查询官方文档更方便，

文档地址：[Intent文档](https://developer.android.com/reference/android/content/Intent.html)
