
## DcTracker

DcTracker是Telephony框架中负责数据业务的核心(类似于GsmCallTracker是通话业务核心，GsmServiceStateTracker是网络CS业务的核心)，所有对数据网络的请求(打开关闭数据开关、切换数据卡、修改APN参数等)都会发送到该处理中心来处理。本节所涉及的大部分代码也都在该对象中。

## APN
APN的英文全称是Access Point Name,中文全称叫接入点,它决定了用户的手机通过哪种接入方式来访问网络,是用户在通过手机上网时必须配置的一个参数。

## DcTracker构建流程

1. 在Phone对象的创建过程中被创建
2. DcTracker初始化
3. 注册监听器
    1. 判断双卡情况
    2. 注册监听器，监听各种数据活动的状态
    3. 对SIM卡状态进行监听
    4. 监听APN状态
4. 初始化TelephonyNetworkFactory对象
    + 初始化网络环境，给网络环境设置一个分值
5. 初始化一些基本的APN参数
    + 载入networkAttributes数组中预置的APN参数
    + 初始化紧急APN参数

## DCTracker是怎么跑起来的

由于手机是Mi5s，基于的是高通，所以从高通的源码开始慢慢看起吧，高通的是QtiDcTracker，他是继承于原生DCTracker的，只是在原生的上面加了一些自己所需要的功能，暂且先不看。
```Java
    public QtiDcTracker(Phone phone) {
        super(phone);
        if (phone.getPhoneType() == PhoneConstants.PHONE_TYPE_GSM) {
            LOG_TAG = "QtiGsmDCT";
        } else if (phone.getPhoneType() == PhoneConstants.PHONE_TYPE_CDMA) {
            LOG_TAG = "QtiCdmaDCT";
        } else {
            LOG_TAG = "DCT";
            loge("unexpected phone type [" + phone.getPhoneType() + "]");
        }
        if (DBG) log(LOG_TAG + ".constructor");

        if (phone.getPhoneType() == PhoneConstants.PHONE_TYPE_CDMA) {
            boolean fetchApnFromOmhCard = getConfigItem(CONFIG_FETCH_APN_FROM_OMH_CARD);
            log(LOG_TAG + " fetchApnFromOmhCard: " + fetchApnFromOmhCard);
            boolean featureOverride = SystemProperties.getBoolean(OMH_FEATURE_ENABLE_OVERRIDE,
                    false);
            if (featureOverride) {
                log(LOG_TAG + "OMH: feature-config override enabled");
                fetchApnFromOmhCard = featureOverride;
            }

            if (fetchApnFromOmhCard) {
                mOmhApt = new QtiCdmaApnProfileTracker(phone);
                mOmhApt.registerForModemProfileReady(this,
                        EVENT_MODEM_DATA_PROFILE_READY, null);
            }
        }
        fillIccIdSet();
    }
```
在构建时先注意一点，构建时传入的参数是一个Phone的实例，那么Phone的实例是在哪里生成的呢，这时候在PhoneGlobals中看如下代码：
```Java
 public void onCreate() {
        if (VDBG) Log.v(LOG_TAG, "onCreate()...");

        ContentResolver resolver = getContentResolver();

        // Cache the "voice capable" flag.
        // This flag currently comes from a resource (which is
        // overrideable on a per-product basis):
        sVoiceCapable =
                getResources().getBoolean(com.android.internal.R.bool.config_voice_capable);
        // ...but this might eventually become a PackageManager "system
        // feature" instead, in which case we'd do something like:
        // sVoiceCapable =
        //   getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY_VOICE_CALLS);

        if (mCM == null) {
            // Initialize the telephony framework
            PhoneFactory.makeDefaultPhones(this);
            //在这里生成默认Phone对象

            .......
            }
```
继续跟踪到PhoneFactory文件中
```Java
public static void makeDefaultPhone(Context context) {
        synchronized (sLockProxyPhones) {
            if (!sMadeDefaults) {
                sContext = context;

                // create the telephony device controller.
                TelephonyDevController.create();

                int retryCount = 0;
                for(;;) {
                    boolean hasException = false;
                    retryCount ++;

                    try {
                        // use UNIX domain socket to
                        // prevent subsequent initialization
                        new LocalServerSocket("com.android.internal.telephony");
                    } catch (java.io.IOException ex) {
                        hasException = true;
                    }

                    if ( !hasException ) {
                        break;
                    } else if (retryCount > SOCKET_OPEN_MAX_RETRY) {
                        throw new RuntimeException("PhoneFactory probably already running");
                    } else {
                        try {
                            Thread.sleep(SOCKET_OPEN_RETRY_MILLIS);
                        } catch (InterruptedException er) {
                        }
                    }
                }
```
这一段代码，先创建一个TelephonyDevController实例，注意这个实例只能创建一次，它在create中会创建一个默认的实例
+ 代码
    ```Java
    private TelephonyDevController() {
            initFromResource();

            //trimToSize()是用来取出ArrayList剩余申请空间
            //ArrayList在创建对象的时候，会申请相对较多的空间
            mModems.trimToSize();
            mSims.trimToSize();
        }
    ```

看代码可以看到，初始化后先调用`initFromResource();`将一些关于modem和sim的数据添加到`mModems`和`mSims`中,还有一些其他函数先放一下，回到PhoneFactory。

回到PhoneFactory后继续往下
```
sPhoneNotifier = new DefaultPhoneNotifier();
```
创建了一个DefaultPhoneNotifier对象实例，这个对象实例主要工作就是向外发送广播，就是判断一些数据连接的信息，对卡槽的一些状态进行更新，先放一放，继续往后看

```
TelephonyComponentFactory telephonyComponentFactory
                    = TelephonyComponentFactory.getInstance();
```
这里实例化TelephonyComponentFactory，这个类中包装了很多方法，用来设置一些监听器或跟踪器，DcTracker也能在里面设置
+ 例如
    ```
    public DcTracker makeDcTracker(Phone phone) {
        Rlog.d(LOG_TAG, "makeDcTracker");
        return new DcTracker(phone);
    }

    ```

接着往下

```
int cdmaSubscription = CdmaSubscriptionSourceManager.getDefault(context);
```

获取默认的CDMA卡槽，接着往下

```
int numPhones = TelephonyManager.getDefault().getPhoneCount();
int[] networkModes = new int[numPhones];
sPhones = new Phone[numPhones];
sCommandsInterfaces = new RIL[numPhones];
sTelephonyNetworkFactories = new TelephonyNetworkFactory[numPhones];
```

获取Phone实例对象的个数，这里调用了TelephonyManager，它封装了很多有用的方法，用来管理个中Telephony相关的内容。根据获得的Phone实例对象的数量，创建对应的大小的数组。

```
        for (int i = 0; i < numPhones; i++) {
            // reads the system properties and makes commandsinterface
            // Get preferred network type.
            networkModes[i] = RILConstants.PREFERRED_NETWORK_MODE;

            Rlog.i(LOG_TAG, "Network Mode set to " + Integer.toString(networkModes[i]));
            sCommandsInterfaces[i] = new RIL(context, networkModes[i],
                    cdmaSubscription, i);
        }
        Rlog.i(LOG_TAG, "Creating SubscriptionController");
        telephonyComponentFactory.initSubscriptionController(
                context, sCommandsInterfaces);
```
设置网络模式，创建对应的RIL实例进行交互

```
 sUiccController = UiccController.make(context, sCommandsInterfaces);
```
创建UiccController对象，UiccController 是对SIM卡管理的控制器，它通过 UiccCard 来更新SIM卡的信息。UiccController 注册了两个监听器，来监听RIL的消息。分别监听 UNSOL_RESPONSE_RADIO_STATE_CHANGED 和 RIL_UNSOL_RESPONSE_SIM_STATUS_CHANGED，当radio 和 sim卡状态发生变化时，它会第一时间得到消息。

```
 for (int i = 0; i < numPhones; i++) {
                    Phone phone = null;
                    int phoneType = TelephonyManager.getPhoneType(networkModes[i]);
                    if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
                        phone = telephonyComponentFactory.makePhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_GSM,
                                telephonyComponentFactory);
                    } else if (phoneType == PhoneConstants.PHONE_TYPE_CDMA) {
                        phone = telephonyComponentFactory.makePhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_CDMA_LTE,
                                telephonyComponentFactory);
                    }
                    Rlog.i(LOG_TAG, "Creating Phone with type = " + phoneType + " sub = " + i);

                    sPhones[i] = phone;
                }
```

通过TelephonyManager获取phoneType，然后通过TelephonyComponentFactory实例化phone对象，存储到数组中。

```
                sPhone = sPhones[0];
                sCommandsInterface = sCommandsInterfaces[0];

                // Ensure that we have a default SMS app. Requesting the app with
                // updateIfNeeded set to true is enough to configure a default SMS app.
                ComponentName componentName =
                        SmsApplication.getDefaultSmsApplication(context, true /* updateIfNeeded */);
                String packageName = "NONE";
                if (componentName != null) {
                    packageName = componentName.getPackageName();
                }
                Rlog.i(LOG_TAG, "defaultSmsApplication: " + packageName);

                // Set up monitor to watch for changes to SMS packages
                SmsApplication.initSmsPackageMonitor(context);
```
让第0个Phone实例作为默认Phone实例，设置默认短信应用，设置监视器监听短信服务的变化

```
             Rlog.i(LOG_TAG, "Creating SubInfoRecordUpdater ");
             sSubInfoRecordUpdater = telephonyComponentFactory.makeSubscriptionInfoUpdater(
                    context, sPhones, sCommandsInterfaces);
             SubscriptionController.getInstance().updatePhonesAvailability(sPhones);
```
创建SubInfoRecordUpdater，监听卡的广播状态

```
for (int i = 0; i < numPhones; i++) {
                    sPhones[i].startMonitoringImsService();
                }
```
启动对应Phone实例的Ims服务监听器

```
ITelephonyRegistry tr = ITelephonyRegistry.Stub.asInterface(
                        ServiceManager.getService("telephony.registry"));
                SubscriptionController sc = SubscriptionController.getInstance();
```
注册广播 卡槽控制器

```
                sSubscriptionMonitor = new SubscriptionMonitor(tr, sContext, sc, numPhones);

                sPhoneSwitcher = telephonyComponentFactory.
                        makePhoneSwitcher (MAX_ACTIVE_PHONES, numPhones,
                        sContext, sc, Looper.myLooper(), tr, sCommandsInterfaces,
                        sPhones);

                sProxyController = ProxyController.getInstance(context, sPhones,
                        sUiccController, sCommandsInterfaces, sPhoneSwitcher);
```

创建卡槽监视器， 创建一个PhoneSwitcher，用来监听活动的sim卡，根据卡槽和网络状态进行Phone实例的切换，创建一个ProxyController实例控制Phone代理

```
                sTelephonyNetworkFactories = new TelephonyNetworkFactory[numPhones];
                for (int i = 0; i < numPhones; i++) {
                    sTelephonyNetworkFactories[i] = new TelephonyNetworkFactory(
                            sPhoneSwitcher, sc, sSubscriptionMonitor, Looper.myLooper(),
                            sContext, i, sPhones[i].mDcTracker);
                }

                telephonyComponentFactory.makeExtTelephonyClasses(
                        context, sPhones, sCommandsInterfaces);
```

创建对应数量的TelephonyNetworkFactory实例，用以控制网络状态

## 小结
暂时先到这，后续会接着更0.0

## 接上

上一次吧PhoneFactory中的`makeDefaultPhone`函数过了一遍，现在接着往下走

```
 PhoneFactory.makeDefaultPhones(this);
 // Start TelephonyDebugService After the default phone is created.
 Intent intent = new Intent(this, TelephonyDebugService.class);
 startService(intent);
```
看注释也能明白，PhoneGlobals咋onCreate过程中，当defaultPhone创建完成后，启动了
TelephonyDebugService服务。

```
        mCM = CallManager.getInstance();
        for (Phone phone : PhoneFactory.getPhones()) {
            mCM.registerPhone(phone);
        }
```

实例化CallManager，赋值给mCM，然后遍历所有的phone对象，添加并注册RingingCall、BackgroundCall、ForegroundCall方法

```
        // Create the NotificationMgr singleton, which is used to display
        // status bar icons and control other status bar behavior.
        notificationMgr = NotificationMgr.init(this);

```

注释写的很清楚了，创建NotificationMgr实例，注意这个也是单例的，如果已经存在是不会再创建新的的，这个实例主要是做状态栏通知相关的工作,NotificationMgr以静态成员函数的方式为PhoneApp用于Phone进程在状态栏中通知用户消息的功能，诸如：有未接电话、正在通话、是否静音等信息。它使用系统提供的API类NotificationManager和StatusBarManager完成通知功能。每项通知对应着通知、更新通知和取消通知的函数。当收到Message时，PhoneApp的Handler的handleMessage会使用NotificationMgr更新状态栏信息

```
        // Create an instance of CdmaPhoneCallState and initialize it to IDLE
        cdmaPhoneCallState = new CdmaPhoneCallState();
        cdmaPhoneCallState.CdmaPhoneCallStateInit();
```

依然可以看注释，创建了一个CdmaPhoneCallState实例并初始化为IDLE(闲置)状态

```
        // before registering for phone state changes
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, LOG_TAG);
        // lock used to keep the processor awake, when we don't care for the display.
        mPartialWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, LOG_TAG);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
```

mPowerManager控制电源相关操作，在这里主要是为了控制屏幕的点亮熄灭，明暗等，具体内容看电源架构相关的文章。

KeyguardManager主要是锁屏相关的管理，这里获取了这个实例用来获取锁屏状态

```
        // Get UpdateLock to suppress system-update related events (e.g. dialog show-up)
        // during phone calls.
        mUpdateLock = new UpdateLock("phone");
```

看注释，获取UpdateLock主要是为了阻止在通话过程中会出现的系统更新等相关的事件，保证通话的正常。

```
    // Create the CallController singleton, which is the interface
    // to the telephony layer for user-initiated telephony functionality
    // (like making outgoing calls.)
    callController = CallController.init(this, callLogger, callGatewayManager);
```

创建CallController实例，注意这个也是单例的，它作为一个接口，初始化用户拨号相关的操作

```
    // Create the CallerInfoCache singleton, which remembers custom ring tone and
    // send-to-voicemail settings.
    //
    // The asynchronous caching will start just after this call.
    callerInfoCache =CallerInfoCache.init(this);
```

创建一个CallerInfoCache单例，存储用户按下号码键的信息并发送到send-to-voicemail设置中

```
    // Create the CallNotifer singleton, which handles
    // asynchronous events from the telephony layer (like
    // launching the incoming-call UI when an incoming call comes
    // in.)
    notifier = CallNotifier.init(this);
```

创建一个CallNotifer单例，CallNotifier是一个Handler，它为PhoneApp处理各个主动上报来的一些消息。它监听来自Telephony层phone状态变化和其它各种事件，从而作出反应 如各种UI行为：启动铃音播放和来电显示UI、播放正在通话时的来电提示、更新状态栏提示（通过NotificationMgr）、通话记录添加等。

```
        PhoneUtils.registerIccStatus(mHandler, EVENT_SIM_NETWORK_LOCKED);

        // register for MMI/USSD
        mCM.registerForMmiComplete(mHandler, MMI_COMPLETE, null);

        // register connection tracking to PhoneUtils
        PhoneUtils.initializeConnectionHandler(mCM)
```

注册相关信息

```
        // Register for misc other intent broadcasts.
        IntentFilter intentFilter =
                new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_RADIO_TECHNOLOGY_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_SERVICE_STATE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED);
        intentFilter.addAction(TelephonyIntents.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED);
        registerReceiver(mReceiver, intentFilter);
```

注册各种广播

```
            //set the default values for the preferences in the phone.
            PreferenceManager.setDefaultValues(this, R.xml.network_setting, false);

            PreferenceManager.setDefaultValues(this, R.xml.call_feature_setting, false);

            // Make sure the audio mode (along with some
            // audio-mode-related state of our own) is initialized
            // correctly, given the current state of the phone.
            PhoneUtils.setAudioMode(mCM);
```

设置各种参数为默认值，将当前音频模式设置给Phone实例

```
        //OTASP相关
        cdmaOtaProvisionData = new OtaUtils.CdmaOtaProvisionData();
        cdmaOtaConfigData = new OtaUtils.CdmaOtaConfigData();
        cdmaOtaScreenState = new OtaUtils.CdmaOtaScreenState();
        cdmaOtaInCallScreenUiState = new OtaUtils.CdmaOtaInCallScreenUiState();

        simActivationManager = new SimActivationManager();

        // XXX pre-load the SimProvider so that it's ready
        //尝试访问SimProvider来初始化它
        resolver.getType(Uri.parse("content://icc/adn"));

        // TODO: Register for Cdma Information Records
        // phone.registerCdmaInformationRecord(mHandler, EVENT_UNSOL_CDMA_INFO_RECORD, null);

        // Read HAC settings and configure audio hardware
        // 获取HAC(无线助听)设置并配置音频硬件
        if (getResources().getBoolean(R.bool.hac_enabled)) {
            int hac = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.HEARING_AID,
                    0);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setParameter(SettingsConstants.HAC_KEY,
                    hac == SettingsConstants.HAC_ENABLED
                            ? SettingsConstants.HAC_VAL_ON : SettingsConstants.HAC_VAL_OFF);
        }
```

创建一些实例，simActivationManager用于SIM卡的激活请求并执行激活动作

## 小结

好了PhoneGlobals大概就是这样，后面接着看DCTracker。


## 参考文章

[深入理解Android Telephony之PhoneApp的初始化](http://blog.csdn.net/mathcompfrac/article/details/53876668)