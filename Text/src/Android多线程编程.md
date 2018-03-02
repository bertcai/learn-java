## 引子
这一篇是很久以前的坑了，拖了好久没有写，最近刚写完dialer应用，正好有写时间，看了一些关于Android多线程的内容，正好写这一篇分享一下。

## 线程的基本用法

Android多线程并不比Java多线程来的多特殊，基本都是使用相同的语法。比如说：定义一个线程通过继承Thread，然后重写父类的run()方法，再在里面编写耗时的逻辑：
```
class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
    }
}

new MyThread().start;
```

也可以通过实现接口的方式，定义一个线程
```
class MyThread implements Runnable {
    @Override
    public void run() {
        //do something
    }
}

MyThread myThread = new MyThread;
new Thread(MyThread).start();
```

匿名类的实现方法
```
new Thread(new Runnable(){
@Override
public void run(){
        Message message=new Message();
        message.what=UPDATE_TEXT;
        handler.sendMessage(message);
        }
        }).start();
```

## 子线程中更新UI
Android的UI是线程不安全的，也就是说，如果想更新UI元素，就必须在子线程中进行UI更新操作，否则就会报出异常

例子：

布局
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <Button
        android:id="@+id/change_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Change Text"
        android:textAllCaps="false" />

    <TextView
        android:layout_gravity="center"
        android:id="@+id/text_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!" />

</LinearLayout>

```

```
public class MainActivity extends AppCompatActivity {

    public static final int UPDATE_TEXT = 1;
    private TextView textView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    textView.setText("Hello Moecai!");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button change = findViewById(R.id.change_text);
        textView = findViewById(R.id.text_view);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }
}

```
上面这段代码就是通过设置一个整型常量UPDATE_TEXT，作为信号，用于表示更新TextView这个动作，然后新增一个Handler对象，重写父类的handleMessage()方法，然后调用Handler的sendMessage将这条Message发送出去，然后Handler接收到Message，进行判断，决定是否修改UI，因为handleMessage()方法是在主线程中运行的，自然就可以进行UI操作了。

### 异步处理机制解析
上面所说的这种处理机制，被称为异步消息处理机制，Android的异步消息处理主要由4部分构成
+ Message
    + 在线程间传递消息
+ Handler
    + 处理者，主要用于发送和处理消息
+ MessageQueue
    + 消息队列，存放所有通过Handler发送的消息，这些消息一直存储在消息队列中，等待处理
+ Looper
    + MessageQueue的管家，循环消息队列，每当发现消息队列中有信息，就将信息取出并传递给Handler的handleMessage()方法中

主要流程

主线程创建一个Handler对象，重写handleMessage方法，在子线程中创建Message对象，在需要时将Message发送出去，Looper从消息队列中取出信息，取出后将消息发送到Handler的handleMessage中，主线程进行操作更新UI。

## 使用AsyncTask

为了更方便的在子线程中更新UI，Android提供了AsyncTask抽象类：
```
public abstract class AsyncTask<Params, Progress, Result> {
    ...
}
```

+ Params AsyncTask时传入的参数，可以用于后台任务
+ Progress 后台任务执行时，如果要在界面上显示进度，则可再次指定进度的泛型单位
+ Result 指定泛型作为返回值的类型

需要重写的方法：
+ `onPreExecute()`
    + 在后台任务执行前调用，用于界面初始化的一些操作
+ `doInBackground(Params)`
    + 子线程中执行耗时的任务可以在此方法内做,参数会传入
+ `onProgressUpdate(Progress)`
    + 在这个方法可以对UI进行操作，利用参数中的数值就可以对界面元素更新
+ `onPostExecute(Result)`
    + 后台任务执行完毕后并通过return语句进行返回时调用，返回的数据会作为参数传入，可以做一些UI操作表示任务已经执行完毕


启动任务
```
class ExampleTask() extends AsyncTask<...>{
    ...
}

new ExampleTask().execute();
```

## 小结

大致内容就是这样了，还有更多内容后续会补一个小demo做一个实例，到时候会更新0.0。
