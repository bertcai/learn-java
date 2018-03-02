## 引子

依旧是补的内容，上一篇是Android多线程，多线程走完了，正好就可以开始Service的相关内容，服务是Android中实现程序后台运行的解决方案，非常适合去执行那些不需要和用户交互而且还要求长期运行的任务，服务不依赖任何用户界面，但是它依赖用户进程，服务并不是运行在一个独立的进程中的，而是依赖于创建服务时所在的应用程序进程，所以服务必然与多线程联系起来。

## 服务

### 定义服务

自定义的服务是继承于一个父类，例如：

```
public class MyService extends Service {
    public MyService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
```

自定义服务处理逻辑的也是通过重写父类的三个方法

```
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
```

第一个是创建时调用,第二个是启动是调用,第三个是销毁时调用。

### 启动服务

启动服务通过Intent

```
Intent intent = new Intent(this, MyService.class);
startService(intent);
```

### 活动与服务通信

服务启动后必然会有与活动通信，与活动通信就需要用到onBind方法了,使用onBind必然就要有Binder，所以可以继承Binder父类，为不同的service构建不同的Binder

```
class MyBinder extends Binder {
    function(){
        ...
    }
}
```

在Binder写一些功能方法，然后在活动中调用服务里的方法。

### Activity中绑定服务

```
public boolean bindService(Intent service, ServiceConnection conn, int flags)
```
第一个参数是目标服务，第二个是ServiceConnection， 第三个是flag。

## 小结

未完待续



