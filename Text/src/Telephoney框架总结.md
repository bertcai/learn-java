## 引子
无论手机如何改变，功能如何丰富，它最核心最关键的功能依旧是通讯，通讯按现在的情况来说主要就是打电话发短信和上网，Android的通讯框架从上往下可以分为4个部分：

+ Modem 这是整个通讯的硬件基础，需要Modem芯片，不同制式需要采用不同的Modem；
+ RIL 为了适配不同的Modem芯片而抽象出来的中间层，用于将Modem指令转换为Java可用的数据流；
+ Telephony 这是在Framework层搭建的通讯框架，面向开发者提供操作通讯事务的能力；
+ Application 这是最上层的应用，直接面向用户，提供拨号、上网、发短信的界面；

这篇文章主要对Telephony框架做一个小小的概述与总结

## Telephony进程与实体

### 进程

整个Framework层的Telephony框架运行在一个叫做Phone（com.android.phone）的进程中。而这个进程是在packages\services\Telephony模块中被创建的(Android 8.0.0平台)。并且该模块在AndroidManifest.xml中有如下关键的声明：

``` xml
    <application android:name="PhoneApp"
            android:persistent="true"
            android:label="@string/phoneAppLabel"
            android:icon="@mipmap/ic_launcher_phone"
            android:allowBackup="false"
            android:supportsRtl="true"
            android:usesCleartextTraffic="true"
            android:defaultToDeviceProtectedStorage="true"
            android:directBootAware="true"
            //从 Android N 开始，在首次开机时，在用户尚未来得及解锁设备之前，设备可直接启动到一种名为 Direct Boot（直接启动）的新模式中。在此模式下，操作系统可以全功能运行，但不允许访问私有应用数据，只能运行经过更新、可支持直接启动功能的应用。
            //该属性使得用户在加密状态（未解锁）下能够正常使用一些手机功能，如闹钟，接电话等
            >
```

这个声明创建了一个名叫“PhoneApp”的application，并且确定了他的name、label、icon等信息，而且将该application的persistent属性置为true。那么这个persistent属性的作用是什么呢？这里的persistent属性具备两个关键作用：
+ 该模块会在开机时被系统自动初始化；
+ 该模块所在的进程（com.android.phone）由于任何原因被kill掉之后，都会自动重启（这种情况只针对系统内置app，第三方安装的app不会被重启）；

以上两点是十分必要的，他保证/导致了两个效果：

+ 所有Application层和Framework层中与Telephony相关的操作，包括各种Service的创建、与RIL层交互的RILJ的初始化等，都是通过Phone进程创建的；
+ Phone进程由于任何原因被kill掉后，都会发生重新搜网的动作；

### 实体对象

前面介绍了Telephony的框架和进程，那么当发生具体的某个通讯请求，比如打电话、发短信时，该如何操作呢？

这里要引入一个非常重要的对象：Phone对象。该对象可以看做是Telephony框架的实体，可以向该对象发起各种通讯相关的请求，可以说，Phone对象是Telephony整个框架的核心，他负责与RIL层的交互。

而这个Phone对象就是在PhoneApp这个application初始化过程中被创建的。我们就从入口开始，来查看Phone进程的创建过程：

在PhoneApp的onCreate()方法中，会new出PhoneGlobals的对象，并接着调用该对象的onCreate方法：

```Java
    public void onCreate() {
        if (UserHandle.myUserId() == 0) {
            // We are running as the primary user, so should bring up the
            // global phone state.
            mPhoneGlobals = new PhoneGlobals(this);
            mPhoneGlobals.onCreate();

            mTelephonyGlobals = new TelephonyGlobals(this);
            mTelephonyGlobals.onCreate();
        }
    }
```

先看PhoneGlobals的onCreate过程：

``` Java
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
            // 创建Phone对象
            PhoneFactory.makeDefaultPhones(this);

            // Start TelephonyDebugService After the default phone is created.
            Intent intent = new Intent(this, TelephonyDebugService.class);
            startService(intent);

            // 初始化CallManager
            mCM = CallManager.getInstance();
            for (Phone phone : PhoneFactory.getPhones()) {
                mCM.registerPhone(phone);
            }

            // Create the NotificationMgr singleton, which is used to display
            // status bar icons and control other status bar behavior.
            // 初始化NotificationMgr，用于状态栏通知
            notificationMgr = NotificationMgr.init(this);

            // If PhoneGlobals has crashed and is being restarted, then restart.
            mHandler.sendEmptyMessage(EVENT_RESTART_SIP);

            // Create an instance of CdmaPhoneCallState and initialize it to IDLE
            cdmaPhoneCallState = new CdmaPhoneCallState();
            cdmaPhoneCallState.CdmaPhoneCallStateInit();

            // before registering for phone state changes
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, LOG_TAG);
            // lock used to keep the processor awake, when we don't care for the display.
            mPartialWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, LOG_TAG);

            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            // Get UpdateLock to suppress system-update related events (e.g. dialog show-up)
            // during phone calls.
            mUpdateLock = new UpdateLock("phone");

            if (DBG) Log.d(LOG_TAG, "onCreate: mUpdateLock: " + mUpdateLock);

            CallLogger callLogger = new CallLogger(this, new CallLogAsync());

            callGatewayManager = CallGatewayManager.getInstance();

            // Create the CallController singleton, which is the interface
            // to the telephony layer for user-initiated telephony functionality
            // (like making outgoing calls.)
            // 初始化CallController
            callController = CallController.init(this, callLogger, callGatewayManager);

            // Create the CallerInfoCache singleton, which remembers custom ring tone and
            // send-to-voicemail settings.
            //
            // The asynchronous caching will start just after this call.
            callerInfoCache = CallerInfoCache.init(this);

            // 初始化PhoneInterfaceManager
            phoneMgr = PhoneInterfaceManager.init(this, PhoneFactory.getDefaultPhone());

            configLoader = CarrierConfigLoader.init(this);

            // Create the CallNotifer singleton, which handles
            // asynchronous events from the telephony layer (like
            // launching the incoming-call UI when an incoming call comes
            // in.)
            // 初始化CallNotifer， 响铃等动作在这里面完成
            notifier = CallNotifier.init(this);

            PhoneUtils.registerIccStatus(mHandler, EVENT_SIM_NETWORK_LOCKED);

            // register for MMI/USSD
            mCM.registerForMmiComplete(mHandler, MMI_COMPLETE, null);

            // register connection tracking to PhoneUtils
            PhoneUtils.initializeConnectionHandler(mCM);

            // Register for misc other intent broadcasts.
            IntentFilter intentFilter =
                    new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intentFilter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(TelephonyIntents.ACTION_SIM_STATE_CHANGED);
            intentFilter.addAction(TelephonyIntents.ACTION_RADIO_TECHNOLOGY_CHANGED);
            intentFilter.addAction(TelephonyIntents.ACTION_SERVICE_STATE_CHANGED);
            intentFilter.addAction(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED);
            registerReceiver(mReceiver, intentFilter);

            mCarrierVvmPackageInstalledReceiver.register(this);

            //set the default values for the preferences in the phone.
            PreferenceManager.setDefaultValues(this, R.xml.network_setting_fragment, false);

            PreferenceManager.setDefaultValues(this, R.xml.call_feature_setting, false);

            // Make sure the audio mode (along with some
            // audio-mode-related state of our own) is initialized
            // correctly, given the current state of the phone.
            PhoneUtils.setAudioMode(mCM);
        }

        // XXX pre-load the SimProvider so that it's ready
        resolver.getType(Uri.parse("content://icc/adn"));

        // TODO: Register for Cdma Information Records
        // phone.registerCdmaInformationRecord(mHandler, EVENT_UNSOL_CDMA_INFO_RECORD, null);

        // Read HAC settings and configure audio hardware
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
    }
```

从以上代码可以看出，PhoneGlobals的初始化过程中要先通过PhoneFactory的makeDefaultPhones()方法创建Phone对象，接着完成了一系列与Telephony相关的重要服务的初始化，比如CallManager、NotificationMgr、CallCommandService、PhoneInterfaceManager、CallNotifier等。

我们现在只关心Phone对象的创建过程，也就是PhoneFactory的makeDefaultPhones过程：

```Java
    public static void makeDefaultPhones(Context context) {
        makeDefaultPhone(context);
    }

    /**
     * FIXME replace this with some other way of making these
     * instances
     */
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

                // 创建DefaultPhoneNotifier，负责通知Phone的状态
                sPhoneNotifier = new DefaultPhoneNotifier();

                int cdmaSubscription = CdmaSubscriptionSourceManager.getDefault(context);
                Rlog.i(LOG_TAG, "Cdma Subscription set to " + cdmaSubscription);

                /* In case of multi SIM mode two instances of Phone, RIL are created,
                   where as in single SIM mode only instance. isMultiSimEnabled() function checks
                   whether it is single SIM or multi SIM mode */
                int numPhones = TelephonyManager.getDefault().getPhoneCount();
                // Start ImsResolver and bind to ImsServices.
                String defaultImsPackage = sContext.getResources().getString(
                        com.android.internal.R.string.config_ims_package);
                Rlog.i(LOG_TAG, "ImsResolver: defaultImsPackage: " + defaultImsPackage);
                sImsResolver = new ImsResolver(sContext, defaultImsPackage, numPhones);
                sImsResolver.populateCacheAndStartBind();

                // 获取当前网络类型
                int[] networkModes = new int[numPhones];
                sPhones = new Phone[numPhones];

                // 根据网络类型创建RILJ，负责Framework与RIL层的交互
                sCommandsInterfaces = new RIL[numPhones];
                sTelephonyNetworkFactories = new TelephonyNetworkFactory[numPhones];

                for (int i = 0; i < numPhones; i++) {
                    // reads the system properties and makes commandsinterface
                    // Get preferred network type.
                    networkModes[i] = RILConstants.PREFERRED_NETWORK_MODE;

                    Rlog.i(LOG_TAG, "Network Mode set to " + Integer.toString(networkModes[i]));
                    sCommandsInterfaces[i] = new RIL(context, networkModes[i],
                            cdmaSubscription, i);
                }
                Rlog.i(LOG_TAG, "Creating SubscriptionController");
                SubscriptionController.init(context, sCommandsInterfaces);

                // Instantiate UiccController so that all other classes can just
                // call getInstance()
                // 创建UiccController，间接创建UiccCard、UiccCardApplication、IccFileHandler、IccRecords、CatService服务
                // （手机中使用的卡SIM,USIM,UIM等统称为:UICC）
                sUiccController = UiccController.make(context, sCommandsInterfaces);

                // 根据当前Phone类型创建不同的PhoneProxy
                for (int i = 0; i < numPhones; i++) {
                    Phone phone = null;
                    int phoneType = TelephonyManager.getPhoneType(networkModes[i]);
                    if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
                        phone = new GsmCdmaPhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_GSM,
                                TelephonyComponentFactory.getInstance());
                    } else if (phoneType == PhoneConstants.PHONE_TYPE_CDMA) {
                        phone = new GsmCdmaPhone(context,
                                sCommandsInterfaces[i], sPhoneNotifier, i,
                                PhoneConstants.PHONE_TYPE_CDMA_LTE,
                                TelephonyComponentFactory.getInstance());
                    }
                    Rlog.i(LOG_TAG, "Creating Phone with type = " + phoneType + " sub = " + i);

                    sPhones[i] = phone;
                }

                // Set the default phone in base class.
                // FIXME: This is a first best guess at what the defaults will be. It
                // FIXME: needs to be done in a more controlled manner in the future.
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

                sMadeDefaults = true;

                Rlog.i(LOG_TAG, "Creating SubInfoRecordUpdater ");
                sSubInfoRecordUpdater = new SubscriptionInfoUpdater(context,
                        sPhones, sCommandsInterfaces);
                SubscriptionController.getInstance().updatePhonesAvailability(sPhones);

                // Start monitoring after defaults have been made.
                // Default phone must be ready before ImsPhone is created because ImsService might
                // need it when it is being opened. This should initialize multiple ImsPhones for
                // ImsResolver implementations of ImsService.
                for (int i = 0; i < numPhones; i++) {
                    sPhones[i].startMonitoringImsService();
                }

                ITelephonyRegistry tr = ITelephonyRegistry.Stub.asInterface(
                        ServiceManager.getService("telephony.registry"));
                SubscriptionController sc = SubscriptionController.getInstance();

                sSubscriptionMonitor = new SubscriptionMonitor(tr, sContext, sc, numPhones);

                sPhoneSwitcher = new PhoneSwitcher(MAX_ACTIVE_PHONES, numPhones,
                        sContext, sc, Looper.myLooper(), tr, sCommandsInterfaces,
                        sPhones);

                sProxyController = ProxyController.getInstance(context, sPhones,
                        sUiccController, sCommandsInterfaces, sPhoneSwitcher);

                sNotificationChannelController = new NotificationChannelController(context);

                sTelephonyNetworkFactories = new TelephonyNetworkFactory[numPhones];
                for (int i = 0; i < numPhones; i++) {
                    sTelephonyNetworkFactories[i] = new TelephonyNetworkFactory(
                            sPhoneSwitcher, sc, sSubscriptionMonitor, Looper.myLooper(),
                            sContext, i, sPhones[i].mDcTracker);
                }
            }
        }
    }
```

 经过上面两段代码，我们看到了Phone对象的创建过程：
 + 先创建DefaultPhoneNotifier对象，该对象的作用是监听RIL层发过来的Phone状态变化；
 + 针对当前的网络类型创建不同的Java层RIL，即RILJ；
 + 拿到RILJ之后，就要利用DefaultPhoneNotifier和RILJ并根据当前的Phone类型（GSM/CDMA）来创建不同GsmCdmaPhone对象；



## Telephony之GsmCdmaCallTracker

Application如果要发起通话相关的动作，可以通过Telephony的实体对象，也就是Phone对象来发起请求，而Phone对象就会通话相关的请求通过GsmCallTracker转发给RILJ，然后传递给Modem。所以，GsmCallTracker是Phone对象和RILJ之间通话相关事务的接力者。

### GsmCdmaCallTracker的作用及创建过程


首先看GsmCdmaCallTracker提供的功能

```Java
synchronized Connection dial (){}
void acceptCall () throws CallStateException {}
void rejectCall () throws CallStateException {}
void switchWaitingOrHoldingAndActive() throws CallStateException {}
void clearDisconnected() {}
boolean canDial() {}
private void updatePhoneState() {}
void hangup (GsmCdmaConnection conn) throws CallStateException {}
void hangup (GsmCdmaCall call) throws CallStateException {}
```

上述方法表明GsmCdmaCallTracker的作用包括两方面：
+ 对通话线路进行操作，包括接听、挂断、切换、设置静音等；
+ 对当前的通话状态进行通知（IDEL、RINGING、OFFHOOK）；

下面看初始化过程：
创建过程在GsmCdmaPhone中完成

```Java
    public GsmCdmaPhone(Context context, CommandsInterface ci, PhoneNotifier notifier, int phoneId,
                        int precisePhoneType, TelephonyComponentFactory telephonyComponentFactory) {
        this(context, ci, notifier, false, phoneId, precisePhoneType, telephonyComponentFactory);
    }

    public GsmCdmaPhone(Context context, CommandsInterface ci, PhoneNotifier notifier,
                        boolean unitTestMode, int phoneId, int precisePhoneType,
                        TelephonyComponentFactory telephonyComponentFactory) {
        super(precisePhoneType == PhoneConstants.PHONE_TYPE_GSM ? "GSM" : "CDMA",
                notifier, context, ci, unitTestMode, phoneId, telephonyComponentFactory);

        // phone type needs to be set before other initialization as other objects rely on it
        mPrecisePhoneType = precisePhoneType;
        initOnce(ci);
        initRatSpecific(precisePhoneType);
        mSST = mTelephonyComponentFactory.makeServiceStateTracker(this, this.mCi);
        // DcTracker uses SST so needs to be created after it is instantiated
        mDcTracker = mTelephonyComponentFactory.makeDcTracker(this);
        mSST.registerForNetworkAttached(this, EVENT_REGISTERED_TO_NETWORK, null);
        mDeviceStateMonitor = mTelephonyComponentFactory.makeDeviceStateMonitor(this);
        logd("GsmCdmaPhone: constructor: sub = " + mPhoneId);
    }
```

GsmCdmaCallTracker构造方法

```Java
    public GsmCdmaCallTracker (GsmCdmaPhone phone) {
        this.mPhone = phone;
        // 拿到RILJ
        mCi = phone.mCi;
        // 监听通话、Radio状态
        mCi.registerForCallStateChanged(this, EVENT_CALL_STATE_CHANGE, null);
        mCi.registerForOn(this, EVENT_RADIO_AVAILABLE, null);
        mCi.registerForNotAvailable(this, EVENT_RADIO_NOT_AVAILABLE, null);

        // Register receiver for ECM exit
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyIntents.ACTION_EMERGENCY_CALLBACK_MODE_CHANGED);
        mPhone.getContext().registerReceiver(mEcmExitReceiver, filter);

        updatePhoneType(true);
    }
```

在构造函数中GsmCdmaCallTracker拿到RILJ对象，当需要对当前通话连接操作时，就会直接调用RILJ去实现，同时在构造方法中又注册了通话状态和Radio的状态监听器，用于向其他对象通知当前Radio状态的改变。

### GsmCdmaCallTracker对通话动作的处理

通话动作包含接听、挂断、切换、静音等，这些事件在APP层被请求后，最终都会发送给当前的Phone对象，也就是PhoneProxy，然后再转交给当前的mActivePhone，也就是某个GSMCdmaPhone，此时GSMCdmaPhone对象就会把请求转交给GsmCdmaCallTracker来处理

拨号：

```Java
    //GSM
    /**
     * clirMode is one of the CLIR_ constants
     */
    public synchronized Connection dial(String dialString, int clirMode, UUSInfo uusInfo,
                                        Bundle intentExtras)
            throws CallStateException {
        // note that this triggers call state changed notif
        // 清楚链接
        clearDisconnected();

        // 条件判断
        if (!canDial()) {
            throw new CallStateException("cannot dial in current state");
        }

        String origNumber = dialString;
        dialString = convertNumberIfNecessary(mPhone, dialString);

        // The new call must be assigned to the foreground call.
        // That call must be idle, so place anything that's
        // there on hold
        // 是否需要切换通话
        if (mForegroundCall.getState() == GsmCdmaCall.State.ACTIVE) {
            // this will probably be done by the radio anyway
            // but the dial might fail before this happens
            // and we need to make sure the foreground call is clear
            // for the newly dialed connection
            switchWaitingOrHoldingAndActive();
            // This is a hack to delay DIAL so that it is sent out to RIL only after
            // EVENT_SWITCH_RESULT is received. We've seen failures when adding a new call to
            // multi-way conference calls due to DIAL being sent out before SWITCH is processed
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // do nothing
            }

            // Fake local state so that
            // a) foregroundCall is empty for the newly dialed connection
            // b) hasNonHangupStateChanged remains false in the
            // next poll, so that we don't clear a failed dialing call
            fakeHoldForegroundBeforeDial();
        }

        if (mForegroundCall.getState() != GsmCdmaCall.State.IDLE) {
            //we should have failed in !canDial() above before we get here
            throw new CallStateException("cannot dial in current state");
        }
        boolean isEmergencyCall = PhoneNumberUtils.isLocalEmergencyNumber(mPhone.getContext(),
                dialString);

        // 准备新的通话连接
        mPendingMO = new GsmCdmaConnection(mPhone, checkForTestEmergencyNumber(dialString),
                this, mForegroundCall, isEmergencyCall);
        mHangupPendingMO = false;
        mMetrics.writeRilDial(mPhone.getPhoneId(), mPendingMO, clirMode, uusInfo);


        if ( mPendingMO.getAddress() == null || mPendingMO.getAddress().length() == 0
                || mPendingMO.getAddress().indexOf(PhoneNumberUtils.WILD) >= 0) {
            // Phone number is invalid
            mPendingMO.mCause = DisconnectCause.INVALID_NUMBER;

            // handlePollCalls() will notice this call not present
            // and will mark it as dropped.
            pollCallsWhenSafe();
        } else {
            // Always unmute when initiating a new call
            // 设置非静音模式
            setMute(false);

            // 向RIL层发送拨号请求
            mCi.dial(mPendingMO.getAddress(), clirMode, uusInfo, obtainCompleteMessage());
        }

        if (mNumberConverted) {
            mPendingMO.setConverted(origNumber);
            mNumberConverted = false;
        }

        // 更新通话状态
        updatePhoneState();
        mPhone.notifyPreciseCallStateChanged();

        return mPendingMO;
    }
```

接听动作：

```Java
    public void acceptCall() throws CallStateException {
        // FIXME if SWITCH fails, should retry with ANSWER
        // in case the active/holding call disappeared and this
        // is no longer call waiting

        if (mRingingCall.getState() == GsmCdmaCall.State.INCOMING) {
            Rlog.i("phone", "acceptCall: incoming...");
            // Always unmute when answering a new call
            setMute(false);
            // 向RIL层发送接听的请求
            mCi.acceptCall(obtainCompleteMessage());
        } else if (mRingingCall.getState() == GsmCdmaCall.State.WAITING) {
            if (isPhoneTypeGsm()) {
                setMute(false);
            } else {
                GsmCdmaConnection cwConn = (GsmCdmaConnection)(mRingingCall.getLatestConnection());

                // Since there is no network response for supplimentary
                // service for CDMA, we assume call waiting is answered.
                // ringing Call state change to idle is in GsmCdmaCall.detach
                // triggered by updateParent.
                cwConn.updateParent(mRingingCall, mForegroundCall);
                cwConn.onConnectedInOrOut();
                updatePhoneState();
            }
            // 切换通话
            switchWaitingOrHoldingAndActive();
        } else {
            throw new CallStateException("phone not ringing");
        }
    }
```

拒接动作：

```Java
    public void rejectCall() throws CallStateException {
        // AT+CHLD=0 means "release held or UDUB"
        // so if the phone isn't ringing, this could hang up held
        if (mRingingCall.getState().isRinging()) {
            // 拒接
            mCi.rejectCall(obtainCompleteMessage());
        } else {
            throw new CallStateException("phone not ringing");
        }
    }
```

以上三个动作最后都要调用mCi对象来处理，这个对象就是RILJ，他会把请求发送到RIL层来处理

### GsmCdmaCallTracker对通话状态的处理

在GsmCdmaCallTracker中完成了通话相关动作之后，就立刻更新当前的状态并发送给Radio状态监听者。

例如，接听电话时，当发送了mCi.dial()的请求之后，就立刻调用updatePhoneState()进行状态更新：

```Java
    private void updatePhoneState() {
        PhoneConstants.State oldState = mState;
        // 获取当前状态
        if (mRingingCall.isRinging()) {
            mState = PhoneConstants.State.RINGING;
        } else if (mPendingMO != null ||
                !(mForegroundCall.isIdle() && mBackgroundCall.isIdle())) {
            mState = PhoneConstants.State.OFFHOOK;
        } else {
            Phone imsPhone = mPhone.getImsPhone();
            if ( mState == PhoneConstants.State.OFFHOOK && (imsPhone != null)){
                imsPhone.callEndCleanupHandOverCallIfAny();
            }
            mState = PhoneConstants.State.IDLE;
        }

        if (mState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallEndedRegistrants.notifyRegistrants(
                new AsyncResult(null, null, null));
        } else if (oldState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallStartedRegistrants.notifyRegistrants (
                    new AsyncResult(null, null, null));
        }
        if (Phone.DEBUG_PHONE) {
            log("update phone state, old=" + oldState + " new="+ mState);
        }
        if (mState != oldState) {
            // 通知GsmCdmaPhone进行状态广播
            mPhone.notifyPhoneStateChanged();
            mMetrics.writePhoneState(mPhone.getPhoneId(), mState);
        }
    }
```

在这个过程中，最后要通过GsmCdmaPhone的notifyPhoneStateChanged()方法来通知其他对象
这样一来，DefaultPhoneNotifier就将RILJ与TelephonyRegistry联系起来了，当RILJ接收到RIL上报的Phone状态时，就会通过DefaultPhoneNotifier发送给TelephonyRegistry。
### GsmCdmaCallTracker的更新机制

手机通话功能可以支持多路通话。比如最基本的情况是，在和A通话过程中（线路A），有新的来电时（线路B），如果选择接听B，那么A线路将处于“呼叫保持”状态，此时如果B线路被挂断，那么A线路重新被激活。

而GsmCdmaCallTracker的更新机制核心任务就是维护这些不同线路，包括对各个线路的操作（比如接听、挂断、保持），以及各个线路状态的维护。为了达到这个目的，GsmCallTracker内部创建了两个非常重要的对象：GsmCdmaConnection和GsmCdmaCall。

#### GsmCdmaConnection

为了管理不同的线路，Android定义了GsmCdmaConnection类，简单来说，就是一条通话线路，就是一个GsmCdmaConnection类型的对象。

在GsmCdmaCallTracker的成员变量中，创建了GsmCdmaConnection类型的数组变量来维护所有的线路

```Java
public static final int MAX_CONNECTIONS_GSM = 19;   //7 allowed in GSM + 12 from IMS for SRVCC

private GsmCdmaConnection mConnections[];

mConnections = new GsmCdmaConnection[MAX_CONNECTIONS_GSM];
```

#### GsmCdmaCall

这个对象和GsmCdmaConnection的作用类似，每一个通话线路都可以是一个GsmCall对象，但实际上并不是一个GsmCdmaConnection对应一个GsmCdmaCall。

一个通话线路状态分为以下9种

状态|说明
---|---
IDEL|没有通话
ACTIVE|被激活状态
HOLDING|被保持状态
DIALING|正在呼出状态
ALERTING|正在呼出已经处于响铃的状态
INCOMING|正在来电状态
WAITING|已经通话中，又有新的来电
DISCONNECTED|被挂断
DISCONNECTING|正在挂断

在GsmCdmaCallTracker中，又将不同的线路状态分为3种

+ ForegroundCall
+ BackgroundCall
+ RingingCall

然后创建三个GsmCdmaCall对象
+ mForegroundCall
+ mBackgroundCall
+ mRingingCall

对应关系如下：

对象|状态
---|---
mForegroundCall|ACTIVE、DIALING、ALERTING
mBackgroundCall|HOLDING
mRingingCall|INCOMING、WAITING

这样做的好处是，GsmCdmaCall不再面对具体的线路，而是面对当前Phone的状态，被激活的线路就是mForegroundCall，被保持的线路就是mBackgroundCall，而正处于响铃状态的线路就是mRingingCall，从这里我们可以想到，他和GsmConnection的区别在于，一个GsmCdmaCall可能包含多个GsmCdmaConnection对象（比如同时有两通电话处于被保持状态）。

而GsmCdmaCall要做的主要功能就是维护不同GsmCall的状态。

#### GsmCdmaCallTracker的更新机制

GsmCdmaCallTracker运行机制的核心就是要及时更新GsmCdmaConnection和GsmCall的状态，因此弄明白这两个对象的更新机制就会明白GsmCdmaCallTracker的更新机制。

现在回到GsmCdmaCallTracker的构造方法中，刚才我们看到，GsmCdmaCallTracker在构造方法的最后注册了对通话和Radio状态的监听器，下面我们从这些监听器入手分析GsmCdmaCallTracker的运行机制。

第一个监听器
```Java
mCi.registerForCallStateChanged(this, EVENT_CALL_STATE_CHANGE, null);
```

这个监听器监听的是RIL层通话的状态，当有新的状态到来时（比如新的来电），就会通过EVENT_CALL_STATE_CHANGE消息通知到GsmCdmaCallTracker，然后就会在handleMessage中进行处理：

```Java
public void handleMessage (Message msg) {
    AsyncResult ar;
    switch (msg.what) {
        case EVENT_REPOLL_AFTER_DELAY:
        case EVENT_CALL_STATE_CHANGE:
            //得到RIL层消息，通话状态有变
            pollCallsWhenSafe();
            break;
    }
}
```
然后就会调用pollCallsWhenSafe()方法去获取当前最新的通话状态。

然后看看第二个监听器

```Java
mCi.registerForOn(this, EVENT_RADIO_AVAILABLE, null);
```
这个监听器是用来监听Radio的可用状态，当Radio的状态上来后处理

```Java
public void handleMessage (Message msg) {
    AsyncResult ar;
    switch (msg.what) {
        case EVENT_RADIO_AVAILABLE:
            handleRadioAvailable();
            break;
    }
}
```
然后进入handleRadioAvailable()中处理：

```Java
protected void handleRadioAvailable() {
    pollCallsWhenSafe();
}
```

这里就与第一个监听器一样了，然后看看第三个监听器：

```Java
mCi.registerForNotAvailable(this, EVENT_RADIO_NOT_AVAILABLE, null);
```

这个监听器用来监听Radio的不可用状态，当监听的消息上来后，在handleMessage中处理：
```Java
public void handleMessage (Message msg) {
    AsyncResult ar;
    switch (msg.what) {
        case EVENT_RADIO_NOT_AVAILABLE:
            handleRadioNotAvailable();
            break;
    }
}
```

然后会进入handleRadioNotAvailable()的流程：

```Java
private void handleRadioNotAvailable() {
    pollCallsWhenSafe();
}
```
接下来又是pollCallsWhenSafe()的操作，到这里我们发现，在GsmCdmaCallTracker构造函数中注册的三个监听器，无论哪一个被触发都会进入pollCallsWhenSafe的流程，接下来的分析我们将会看到，GsmCdmaCallTracker将会主动请求最新的通话状态，然后根据当前状态去更新GsmCdmaConnection和GsmCdmaCall对象。

然后看看pollCallsWhenSafe()的流程：

```
    protected void pollCallsWhenSafe() {
        mNeedsPoll = true;

        if (checkNoOperationsPending()) {
            mLastRelevantPoll = obtainMessage(EVENT_POLL_CALLS_RESULT);
            // 通过RILJ获取当前的最新通话状态
            mCi.getCurrentCalls(mLastRelevantPoll);
        }
    }
```
这里看到，在pollCallsWhenSafe中通过RILJ（也就是mCi）去向Modem查询当前的通话状态，并注册了回调的消息EVENT_POLL_CALLS_RESULT，当拿到Modem返回值后，就会再次通过handleMessage()来处理最新的通话状态：

```Java
public void handleMessage (Message msg) {
    AsyncResult ar;
    switch (msg.what) {
        case EVENT_POLL_CALLS_RESULT:
            // 拿到最新通话状态
            ar = (AsyncResult)msg.obj;
            if (msg == mLastRelevantPoll) {
                mNeedsPoll = false;
                mLastRelevantPoll = null;
                handlePollCalls((AsyncResult)msg.obj);
            }
            break;
    }
}
```

然后将数据拿到后，交由handlePollCalls()来处理，在这个方法里，就需要将当前的Modem通话状态数据进行解析，更新GsmCdmaConnection和GsmCdmaCall对象：

```Java
protected synchronized void handlePollCalls(AsyncResult ar) {
    List polledCalls;
    Connection newRinging = null; //or waiting
    boolean hasNonHangupStateChanged = false;   // Any change besides
    boolean hasAnyCallDisconnected = false;
    boolean needsPollDelay = false;
    boolean unknownConnectionAppeared = false;

    for (int i = 0, curDC = 0, dcSize = polledCalls.size() ; i < mConnections.length; i++) {
        //拿到当前GsmCallTracker中的通话线路
        GsmConnection conn = mConnections[i];
        DriverCall dc = null;

        if (curDC < dcSize) {
            //拿到当前的Modem中的通话线路状态
            dc = (DriverCall) polledCalls.get(curDC);
            if (dc.index == i+1) {
                curDC++;
            } else {
                dc = null;
            }
        }


        if (conn == null && dc != null) {
            if (mPendingMO != null && mPendingMO.compareTo(dc)) {
                //mConnections中没有当前线路，而且当前线路是匹配mPendingMO的，说明是最新发起的呼出线路
                mConnections[i] = mPendingMO;
                mPendingMO.mIndex = i;
                mPendingMO.update(dc);
                mPendingMO = null;

                if (mHangupPendingMO) {
                    //是否在呼出之后用户立刻挂断了线路
                    mHangupPendingMO = false;
                    try {
                        //挂断这通线路
                        hangup(mConnections[i]);
                    } catch (CallStateException ex) {
                    }
                    return;
                }
            } else {
                //Modem中有该线路，而GsmConnection中没有该线路，说明有新的通话来临，需要创建新的线路连接
                mConnections[i] = new GsmConnection(mPhone.getContext(), dc, this, i);
                if (mConnections[i].getCall() == mRingingCall) {
                    //新来电
                    newRinging = mConnections[i];
                } else {
                    //异常通话线路
                    if (dc.state != DriverCall.State.ALERTING && dc.state != DriverCall.State.DIALING) {
                        mConnections[i].onConnectedInOrOut();
                        if (dc.state == DriverCall.State.HOLDING) {
                            mConnections[i].onStartedHolding();
                        }
                    }
                    unknownConnectionAppeared = true;
                }
            }
            hasNonHangupStateChanged = true;
        } else if (conn != null && dc == null) {
            //Modem中已经没有当前的链接，说明该线路已经被挂断，需要从mConnections中删除（置为null）
            mDroppedDuringPoll.add(conn);
            mConnections[i] = null;
        } else if (conn != null && dc != null && !conn.compareTo(dc)) {
            //Modem中的链接信息与当前的不匹配，可能发生了掉话或者新的通话
            mDroppedDuringPoll.add(conn);
            //需要创建新的链接
            mConnections[i] = new GsmConnection (mPhone.getContext(), dc, this, i);

            if (mConnections[i].getCall() == mRingingCall) {
                newRinging = mConnections[i];
            }
            hasNonHangupStateChanged = true;
        } else if (conn != null && dc != null) {
            //当前线路与Modem匹配，更新当前的链路信息
            boolean changed;
            changed = conn.update(dc);
            hasNonHangupStateChanged = hasNonHangupStateChanged || changed;
        }

    }

    //异常
    if (mPendingMO != null) {
        mDroppedDuringPoll.add(mPendingMO);
        mPendingMO = null;
        mHangupPendingMO = false;
    }

    if (newRinging != null) {
        //新的来电，需要通知registerForNewRingingConnection的监听者
        mPhone.notifyNewRingingConnection(newRinging);
    }

    //对于挂断的链接，需要标明挂断的原因
    for (int i = mDroppedDuringPoll.size() - 1; i >= 0 ; i--) {
        GsmConnection conn = mDroppedDuringPoll.get(i);

        if (conn.isIncoming() && conn.getConnectTime() == 0) {
            // Missed or rejected call
            Connection.DisconnectCause cause;
            if (conn.mCause == Connection.DisconnectCause.LOCAL) {
                //被拒掉
                cause = Connection.DisconnectCause.INCOMING_REJECTED;
            } else {
                //未接来电
                cause = Connection.DisconnectCause.INCOMING_MISSED;
            }

            mDroppedDuringPoll.remove(i);
            hasAnyCallDisconnected |= conn.onDisconnect(cause);
        } else if (conn.mCause == Connection.DisconnectCause.LOCAL
                || conn.mCause == Connection.DisconnectCause.INVALID_NUMBER) {
            mDroppedDuringPoll.remove(i);
            hasAnyCallDisconnected |= conn.onDisconnect(conn.mCause);
        }
    }

    if (mDroppedDuringPoll.size() > 0) {
        mCi.getLastCallFailCause( obtainNoPollCompleteMessage(EVENT_GET_LAST_CALL_FAIL_CAUSE));
    }
    if (needsPollDelay) {
        pollCallsAfterDelay();
    }
    if (newRinging != null || hasNonHangupStateChanged || hasAnyCallDisconnected) {
        internalClearDisconnected();
    }

    //更新通话状态
    updatePhoneState();

    if (unknownConnectionAppeared) {
        mPhone.notifyUnknownConnection();
    }

    if (hasNonHangupStateChanged || newRinging != null || hasAnyCallDisconnected) {
        mPhone.notifyPreciseCallStateChanged();
    }

}
```

上面的更新过程中，用从Modem获取到的通话线路信息与mConnections中存储的信息做对比，从而更新mConnections中线路的状态，比如：

+ Modem中存在，而mConnections中不存在，则说明是新来电，或者新的去电，需要在mConnections中创建新的GsmConnection对象；
+ Modem中不存在，而mConnections中存在，说明该线路已经被挂断，需要从mConnections中删除该线路的GsmConnection对象；
+ Modem中和mConnections都存在，但是信息不匹配，则说明该线路的状态有改变，需要在mConnections中更新信息；

更新线路之后，对于最新挂断的线路，还需要更新挂断的原因，比如是被对方拒接还是未接的来电，然后在更新的最后，通知所有监听者，Radio状态已经改变。我们简单看一下通知的过程：

```Java
    private void updatePhoneState() {
        PhoneConstants.State oldState = mState;
        if (mRingingCall.isRinging()) {
            // 响铃状态
            mState = PhoneConstants.State.RINGING;
        } else if (mPendingMO != null ||
                !(mForegroundCall.isIdle() && mBackgroundCall.isIdle())) {
            // 通话状态
            mState = PhoneConstants.State.OFFHOOK;
        } else {
            Phone imsPhone = mPhone.getImsPhone();
            if ( mState == PhoneConstants.State.OFFHOOK && (imsPhone != null)){
                imsPhone.callEndCleanupHandOverCallIfAny();
            }
            // 待机状态
            mState = PhoneConstants.State.IDLE;
        }

        if (mState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallEndedRegistrants.notifyRegistrants(
                new AsyncResult(null, null, null));
        } else if (oldState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallStartedRegistrants.notifyRegistrants (
                    new AsyncResult(null, null, null));
        }
        if (Phone.DEBUG_PHONE) {
            log("update phone state, old=" + oldState + " new="+ mState);
        }
        if (mState != oldState) {
            // 状态有更新，通过Phone对象发送给监听者
            mPhone.notifyPhoneStateChanged();
            mMetrics.writePhoneState(mPhone.getPhoneId(), mState);
        }
    }
```

其实通知的过程就是通过mPhone的notifyPhoneStateChanged()方法来实现，这里的mPhone，也就是GsmCdmaPhone对象，会把该广播发送给监听者们。


## Telephony之TelephonyRegistry

### TelephonyRegistry概述

TelephonyRegistry的作用是检测当前Radio的状态，包括通话、短信、数据连接等状态，当这些状态发生改变时，通知所有向他注册过的客户端。也就是说，他负责Radio状态的通知。

注册过程：

```Java
telephonyRegistry = new TelephonyRegistry(context);
ServiceManager.addService("telephony.registry", telephonyRegistry);
```

### TelephonyRegistry通知机制

TelephonyRegistry负责状态的通知，需要完成两个工作：

+ 从Radio拿到通知
+ 将通知发送给相应的监听者

#### 从Radio拿到通知消息

TelephonyRegistry的通知，是从他的客户端得到的，这个客户端就是**DefaultPhoneNotifier**。

创建GsmCdmaPhone时需要传递两个重要参数，其中一个是RILJ对象，另一个就是DefaultPhoneNotifier对象，这里的DefaultPhoneNotifier就是TelephonyRegistry的Client。

DefaultPhoneNotifier的构造函数：
```Java
    public DefaultPhoneNotifier() {
        mRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService(
                    "telephony.registry"));
    }
```

在这里，我们看到DefaultPhoneNotifier从ServiceManager中获取了TelephonyRegistry的服务，也就是说，DefaultPhoneNotifier是TelephonyRegistry的Client。

这样一来，DefaultPhoneNotifier就将RILJ与TelephonyRegistry联系起来了，当RILJ接收到RIL上报的Phone状态时，就会通过DefaultPhoneNotifier发送给TelephonyRegistry。

例如对于通话状态的改变，GsmCdmaCallTracker会通过如下方式将状态发送给GsmCdmaPhone

```Java
    private void updatePhoneState() {
        PhoneConstants.State oldState = mState;
        if (mRingingCall.isRinging()) {
            // 响铃状态
            mState = PhoneConstants.State.RINGING;
        } else if (mPendingMO != null ||
                !(mForegroundCall.isIdle() && mBackgroundCall.isIdle())) {
            // 通话状态
            mState = PhoneConstants.State.OFFHOOK;
        } else {
            Phone imsPhone = mPhone.getImsPhone();
            if ( mState == PhoneConstants.State.OFFHOOK && (imsPhone != null)){
                imsPhone.callEndCleanupHandOverCallIfAny();
            }
            // 待机状态
            mState = PhoneConstants.State.IDLE;
        }

        if (mState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallEndedRegistrants.notifyRegistrants(
                new AsyncResult(null, null, null));
        } else if (oldState == PhoneConstants.State.IDLE && oldState != mState) {
            mVoiceCallStartedRegistrants.notifyRegistrants (
                    new AsyncResult(null, null, null));
        }
        if (Phone.DEBUG_PHONE) {
            log("update phone state, old=" + oldState + " new="+ mState);
        }
        if (mState != oldState) {
            // 状态有更新，通过Phone对象发送给监听者
            mPhone.notifyPhoneStateChanged();
            mMetrics.writePhoneState(mPhone.getPhoneId(), mState);
        }
    }
```

mPhone就是GsmCdmaPhone，接下来，它会把通知转交给DefaultPhoneNotifier来处理：

```Java
void notifyPhoneStateChanged() {
    mNotifier.notifyPhoneState(this);
}
```

这里的mNotifier就是创建GsmCdmaPhone对象时传递的DefaultPhoneNotifier对象，这样的话，就将通知发送到了DefaultPhoneNotifier内部：

```Java
    public void notifyPhoneState(Phone sender) {
        Call ringingCall = sender.getRingingCall();
        int subId = sender.getSubId();
        int phoneId = sender.getPhoneId();
        String incomingNumber = "";
        if (ringingCall != null && ringingCall.getEarliestConnection() != null) {
            incomingNumber = ringingCall.getEarliestConnection().getAddress();
        }
        try {
            if (mRegistry != null) {
                  // 将状态发送给TelephonyRegistry
                  mRegistry.notifyCallStateForPhoneId(phoneId, subId,
                        PhoneConstantConversions.convertCallState(
                            sender.getState()), incomingNumber);
            }
        } catch (RemoteException ex) {
            // system process is dead
        }
    }
```
从这里我们看到，DefaultPhoneNotifier将当前GsmCdmaCallTracker中的状态（sender.getState()）通过convertCallState()转换后，传递给TelephonyRegistry，这个转换的作用就是，将GsmCdmaCallTracker中的IDLE、RINGING、OFFHOOK状态转换为TelephonyManager中的对应状态：

```Java
public static final int CALL_STATE_IDLE = 0;
public static final int CALL_STATE_RINGING = 1;
public static final int CALL_STATE_OFFHOOK = 2;
```
就这样，DefaultPhoneNotifier作为TelephonyRegistry的Client，将当前通话状态传递给了TelephonyRegistry。

#### 将消息发送给其他客户端

TelephonyRegistry拿到相应的通知后，是如何将消息发送给其他客户端呢？我们继续用通话状态来跟踪这一流程。

在TelephonyRegistry拿到消息后，需要向两个渠道分发消息，一个是通过系统广播，另一个是向自己注册的监听者。我们主要来看向监听者发送消息的流程。

##### 监听者注册监听

首先我们来看如何成为TelephonyRegistry的监听者。

由于TelephonyRegistry提供关于Radio的多种状态监测，包括通话、信号、呼叫转移、数据连接等状态，所以在向其申请监听时，需要说明监听那种状态，可以通过调用listen()方法来实现，我们来看这个接口：

```Java
public void listen(String pkgForDebug, IPhoneStateListener callback, int events, boolean notifyNow) {
    //获取调用者的UID，监测权限
    int callerUid = UserHandle.getCallingUserId();
    int myUid = UserHandle.myUserId();

    if (events != 0) {
        //权限监测
        checkListenerPermission(events);

        synchronized (mRecords) {
            Record r = null;
            find_and_add: {
                  //获取当前监听者信息
                  IBinder b = callback.asBinder();
                  final int N = mRecords.size();
                  for (int i = 0; i < N; i++) {
                      r = mRecords.get(i);
                      if (b == r.binder) {
                          break find_and_add;
                      }
                  }
                  r = new Record();
                  r.binder = b;
                  r.callback = callback;
                  r.pkgForDebug = pkgForDebug;
                  r.callerUid = callerUid;
                  //添加当前的监听者信息
                  mRecords.add(r);
            }
            int send = events & (events ^ r.events);
            r.events = events;
            //需要立刻通知
            if (notifyNow) {
                if ((events & PhoneStateListener.LISTEN_SERVICE_STATE) != 0) {
                    //监听通话状态
                    try {
                        r.callback.onServiceStateChanged(new ServiceState(mServiceState));
                    } catch (RemoteException ex) {
                        remove(r.binder);
                    }
                }
                if ((events & PhoneStateListener.LISTEN_SIGNAL_STRENGTH) != 0) {
                    //监听信号改变
                    try {
                        int gsmSignalStrength = mSignalStrength.getGsmSignalStrength();
                        r.callback.onSignalStrengthChanged((gsmSignalStrength == 99 ? -1 : gsmSignalStrength));
                    } catch (RemoteException ex) {
                        remove(r.binder);
                    }
                }
                if ((events & PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR) != 0) {
                    try {
                        r.callback.onMessageWaitingIndicatorChanged(mMessageWaiting);
                    } catch (RemoteException ex) {
                        remove(r.binder);
                    }
                }
                if ((events & PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR) != 0) {
                    //监听呼叫转移状态
                    try {
                        r.callback.onCallForwardingIndicatorChanged(mCallForwarding);
                    } catch (RemoteException ex) {
                        remove(r.binder);
                    }
                }
            }
        }
    } else {
        remove(callback.asBinder());
    }
}
```

上面的listen()操作，依次做了：

+ 通过checkListenerPermission()监测调用者是否有监听权限；
+ 将调用者的相关信息保存在mRecords中，保存信息包含IBinder、回调方法、UID、监听事件等；
+ 如果listen的同时要求立刻通知，则会立刻调用监听者的回调方法；

经过这些操作，客户端就完成了对TelephonyRegistry的监听注册，等待接收通知。

##### 如何将消息通知到监听者

DefaultPhoneNotifier作为TelephonyRegistry的Client，通过notifyCallState()将当前通话状态通知到TelephonyRegistry，我们来看这个方法：

```Java
    public void notifyCallState(int state, String incomingNumber) {
        // 权限检查
        if (!checkNotifyPermission("notifyCallState()")) {
            return;
        }

        if (VDBG) {
            log("notifyCallState: state=" + state + " incomingNumber=" + incomingNumber);
        }

        synchronized (mRecords) {
            for (Record r : mRecords) {
                if (r.matchPhoneStateListenerEvent(PhoneStateListener.LISTEN_CALL_STATE) &&
                        (r.subId == SubscriptionManager.DEFAULT_SUBSCRIPTION_ID)) {
                    try {
                        String incomingNumberOrEmpty = r.canReadPhoneState ? incomingNumber : "";
                        // 通知所有监听者
                        r.callback.onCallStateChanged(state, incomingNumberOrEmpty);
                    } catch (RemoteException ex) {
                        mRemoveList.add(r.binder);
                    }
                }
            }
            // 发送广播通知
            handleRemoveListLocked();
        }

        // Called only by Telecomm to communicate call state across different phone accounts. So
        // there is no need to add a valid subId or slotId.
        broadcastCallStateChanged(state, incomingNumber,
                SubscriptionManager.INVALID_PHONE_INDEX,
                SubscriptionManager.INVALID_SUBSCRIPTION_ID);
    }

```

在上面这个方法中，先对通知者进行权限检查，然后在mRecords中查找曾经注册了该事件的监听者，并调用他们的回调方法。最后，又将该消息发送到系统广播中。

至此，通话状态改变的消息就从GsmCallTracker通过DefaultPhoneNotifier传递给了TelephonyRegistry，并从此扩散。类似的，对于数据连接状态来说，将会由DcTracker通过DefaultPhoneNotifier传递给TelephonyRegistry，然后进行扩散。

#### 小结

从以上的分析中我们看到，TelephonyRegistry作为一个Service，成为他的Client后可以拥有两种功能：
+ Client可以将当前Radio的状态发送给TelephonyRegistry，比如GsmCdmaCallTracker；
+ Client可以向TelephonyRegistry申请监听Radio的相关状态，比如TelephonyManager；


## Telephony之PhoneInterfaceManager

### 概述

PhoneInterfaceManager是一个Service，在被创建时通过ServiceManager注册自己，他作为Telephony对外的接口，可以接受其他进程向Telephony的请求，我们通过该Service所继承的AIDL文件就能看到他所提供的具体功能：

```Java
interface ITelephony {
    //发起通话
    void dial(String number);
    void call(String callingPackage, String number);
    boolean showCallScreen();
    boolean showCallScreenWithDialpad(boolean showDialpad);
    //挂断通话
    boolean endCall();
    //接听通话
    void answerRingingCall();
    void silenceRinger();
    //通话状态判断
    boolean isOffhook();
    boolean isRinging();
    boolean isIdle();
    boolean isRadioOn();
    boolean isSimPinEnabled();
    void cancelMissedCallsNotification();
    //Pin/Puk码查询
    boolean supplyPin(String pin);
    int getIccPin1RetryCount();
    boolean supplyPuk(String puk, String pin);
    int[] supplyPinReportResult(String pin);
    int[] supplyPukReportResult(String puk, String pin);
    boolean handlePinMmi(String dialString);
    void toggleRadioOnOff();
    boolean setRadio(boolean turnOn);
    boolean setRadioPower(boolean turnOn);
    void updateServiceLocation();
    void enableLocationUpdates();
    void disableLocationUpdates();
    //数据连接业务
    int enableApnType(String type);
    int disableApnType(String type);
    boolean enableDataConnectivity();
    boolean disableDataConnectivity();
    boolean isDataConnectivityPossible();
    Bundle getCellLocation();
    List<NeighboringCellInfo> getNeighboringCellInfo(String callingPkg);
    //通话状态获取
    int getCallState();
    int getDataActivity();
    int getDataState();
    int getActivePhoneType();
    int getCdmaEriIconIndex();
    int getCdmaEriIconMode();
    String getCdmaEriText();
    boolean needsOtaServiceProvisioning();
    int getVoiceMessageCount();
    //网络、数据类型
    int getNetworkType();
    int getDataNetworkType();
    int getVoiceNetworkType();
    boolean hasIccCard();
    int getLteOnCdmaMode();
    List<CellInfo> getAllCellInfo();
    void setCellInfoListRate(int rateInMillis);
    String transmitIccLogicalChannel(int cla, int command, int channel, int p1, int p2, int p3, String data);
    String transmitIccBasicChannel(int cla, int command, int p1, int p2, int p3, String data);
    int openIccLogicalChannel(String AID);
    boolean closeIccLogicalChannel(int channel);
    int getLastError();
    byte[] transmitIccSimIO(int fileID, int command, int p1, int p2, int p3, String filePath);
    byte[] getATR();
    //通话中合并、切换、静音、Dtmf处理
    void toggleHold();
    void merge();
    void swap();
    void mute(boolean mute);
    void playDtmfTone(char digit, boolean timedShortCode);
    void stopDtmfTone();
    //添加、删除监听器
    void addListener(ITelephonyListener listener);
    void removeListener(ITelephonyListener listener);
}
```

从他所提供的接口来看，其提供了Telephony比较全面的功能，包括：通话、Pin/Puk、Radio状态、数据连接业务等功能的查询或控制。

### PhoneInterfaceManager的创建过程

PhoneInterfaceManager是在PhoneGlobals的onCreate()创建的：

```Java
phoneMgr = PhoneInterfaceManager.init(this, PhoneFactory.getDefaultPhone());
```

具体创建过程：
```Java
    static PhoneInterfaceManager init(PhoneGlobals app, Phone phone) {
        synchronized (PhoneInterfaceManager.class) {
            if (sInstance == null) {
                // 创建对象
                sInstance = new PhoneInterfaceManager(app, phone);
            } else {
                Log.wtf(LOG_TAG, "init() called multiple times!  sInstance = " + sInstance);
            }
            return sInstance;
        }
    }
```

然后看构造方法：

```Java
    private PhoneInterfaceManager(PhoneGlobals app, Phone phone) {
        mApp = app;
        mPhone = phone;
        mCM = PhoneGlobals.getInstance().mCM;
        mUserManager = (UserManager) app.getSystemService(Context.USER_SERVICE);
        mAppOps = (AppOpsManager)app.getSystemService(Context.APP_OPS_SERVICE);
        mMainThreadHandler = new MainThreadHandler();
        mTelephonySharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mPhone.getContext());
        mSubscriptionController = SubscriptionController.getInstance();

        publish();
    }
```

在构造方法中除了将PhoneGlobals中初始化的Phone、CallManager等信息传递给PhoneInterfaceManager外，还将PhoneInterfaceManager通过publish()注册给ServiceManager:

```Java
    private void publish() {
        if (DBG) log("publish: " + this);

        // 注册给ServiceManager， 名字是"Phone"
        ServiceManager.addService("phone", this);
    }
```

这里看到，PhoneInterfaceManager将自己注册为SystemServer，其他模块可以通过ServiceManager来获取他的服务。

### PhoneInterfaceManager对客户端请求的处理

客户端通过ServiceManager获取到PhoneInterfaceManager的代理对象后，就可以对其发起各种请求，我们挑选几个比较重要的事务来简要分析。

#### 拨号

通过PhoneInterfaceManager拨号时，进入以下流程：

```Java
    public void dial(String number) {
        dialForSubscriber(getPreferredVoiceSubscription(), number);
    }

    public void dialForSubscriber(int subId, String number) {
        if (DBG) log("dial: " + number);
        // No permission check needed here: This is just a wrapper around the
        // ACTION_DIAL intent, which is available to any app since it puts up
        // the UI before it does anything.

        String url = createTelUrl(number);
        if (url == null) {
            return;
        }

        // PENDING: should we just silently fail if phone is offhook or ringing?
        PhoneConstants.State state = mCM.getState(subId);
        if (state != PhoneConstants.State.OFFHOOK && state != PhoneConstants.State.RINGING) {
            // 发送intent实现拨号
            Intent  intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApp.startActivity(intent);
        }
    }
```
PhoneInterfaceManager通过intent的形式发起拨号任务。

#### 挂断电话

挂断电话相关：

```Java
    public boolean endCall() {
        return endCallForSubscriber(getDefaultSubscription());
    }

    /**
     * End a call based on the call state of the subId
     * @return true is a call was ended
     */
    public boolean endCallForSubscriber(int subId) {
        // 权限检查
        enforceCallPermission();
        // 发送CMD_END_CALL的Message
        return (Boolean) sendRequest(CMD_END_CALL, null, new Integer(subId));
    }

    private final class MainThreadHandler extends Handler {
        ...
        @Override
        public void handleMessage(Message msg) {
            ...
            switch (msg.what) {
                case CMD_END_CALL:
                    request = (MainThreadRequest) msg.obj;
                    int end_subId = request.subId;
                    final boolean hungUp;
                    Phone phone = getPhone(end_subId);
                    if (phone == null) {
                        if (DBG) log("CMD_END_CALL: no phone for id: " + end_subId);
                        break;
                    }
                    int phoneType = phone.getPhoneType();
                    if (phoneType == PhoneConstants.PHONE_TYPE_CDMA) {
                        // CDMA: If the user presses the Power button we treat it as
                        // ending the complete call session
                        // 通过PhoneUtils挂断电话
                        hungUp = PhoneUtils.hangupRingingAndActive(getPhone(end_subId));
                    } else if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
                        // GSM: End the call as per the Phone state
                        // 通过PhoneUtils挂断电话
                        hungUp = PhoneUtils.hangup(mCM);
                    } else {
                        throw new IllegalStateException("Unexpected phone type: " + phoneType);
                    }
                    if (DBG) log("CMD_END_CALL: " + (hungUp ? "hung up!" : "no call to hang up"));
                    request.result = hungUp;
                    // Wake up the requesting thread
                    synchronized (request) {
                        request.notifyAll();
                    }
                    break;
                    ...
            }
            ...
        }
        ...
    }
```

在这里，PhoneInterfaceManager又将请求传给了PhoneUtils来处理结束通话的操作。

#### 查询Pin码

下面看Pin码相关的操作：

```Java
    public int[] supplyPinReportResultForSubscriber(int subId, String pin) {
        // 权限检查
        enforceModifyPermission();
        // 通过IccCard来操作
        final UnlockSim checkSimPin = new UnlockSim(getPhone(subId).getIccCard());
        // 启用查询线程
        checkSimPin.start();
        return checkSimPin.unlockSim(null, pin);
    }
```

#### 数据连接业务

禁用数据连接：

```Java
    @Override
    public boolean disableDataConnectivity() {
        enforceModifyPermission();
        int subId = mSubscriptionController.getDefaultDataSubId();
        final Phone phone = getPhone(subId);
        if (phone != null) {
            phone.setDataEnabled(false);
            return true;
        } else {
            return false;
        }
    }
```

通过ConnectivityManager禁用数据连接业务。

#### 小结

经过以上几个请求的解析，我们发现，PhoneInterfaceManager自身并没有完成请求的功能，而是把客户端的请求分配给相应领域的管理者，也可以这样理解，PhoneInterfaceManager作为Telephony框架对外的“接口人”，接收客户的请求后，将请求发送给真正的“主人”去实现。


## Telephony之TelephonyManager

### TelephonyManager概述

TelephonyManager主要提供Telephony相关实务的处理能力，我们从他所提供的public方法来总览一下其所能提供的功能：

```Java
//得到软件版本
getDeviceSoftwareVersion()
//得到设备的ID，IMEI或者MEID
getDeviceId()
//得到位置信息，主要是当前注册小区的位置码
getCellLocation()
//得到附近小区信息
getNeighboringCellInfo()
//得到当前Phone的类型，GSM/CDMA
getCurrentPhoneType()
//得到/proc/cmdline文件当前的内容
getProcCmdLine()
//得到运营商名字
getNetworkOperatorName()
//得到MCC+MNC
getNetworkOperator()
//得到是否漫游的状态
isNetworkRoaming()
//得到网络状态，NETWORK_TYPE_GPRS、NETWORK_TYPE_EDGE、NETWORK_TYPE_CDMA等等
getNetworkType()
//得到SIM卡状态
getSimState()
//得到SIM卡MCC+MNC
getSimOperator()
//得到SIM卡SPN
getSimOperatorName()
//得到SIM卡串号
getSimSerialNumber()
//得到MSISDN
getMsisdn()
//得到语音信箱号码
getVoiceMailNumber()
//得到语音信箱短信条数
getVoiceMessageCount()
//得到语音信箱名称
getVoiceMailAlphaTag()
//得到数据连接状态：DATA_DISCONNECTED、DATA_CONNECTING、DATA_CONNECTED、DATA_SUSPENDED等
getDataState()
//注册监听器监听Phone状态
listen()
//得到所有Phone的信息
getAllCellInfo()
```

从这些方法来看，TelephonyManager提供了与PhoneInterfaceManager类似的功能，但是又有本质的区别，其共同点是：都向其他模块提供了全面的操作Telephony相关事务的能力，其他模块可以在获取到这两个服务后，对Telephony进行各种操作。而区别在于：
+ 从本质上来讲，TelephonyManager本质不是一个Service，没有继承任何类，而PhoneInterfaceManager的本质是一个Service
+ 从注册方式上来讲，TelephonyManager是在ContextImpl中通过registerService的形式进行注册，而PhoneInterfaceManager是通过ServiceManager进行注册
+ 从获取方式上来讲，需要TelephonyManager服务时，可以通过Context对象的getSystemService()方法来实现，而PhoneInterfaceManager服务需要通过ServiceManager的getService()方法来实现

### 获取TelephonyManager服务

如果调用者是系统应用，可以直接创建TelephonyManager的对象，只需要传递Context类型的参数就行：

```Java
boolean isPhone() {
    if (!mIsPhoneInitialized) {
        //创建TelephonyManager对象，并调用isVoiceCapable()方法
        mIsPhone = new TelephonyManager(getContext()).isVoiceCapable();
        mIsPhoneInitialized = true;
    }
    return mIsPhone;
}
```

或者通过TelephonyManager的getDefault()方法来获取TelephonyManager对象：

```Java
    private static TelephonyManager sInstance = new TelephonyManager();

    /** @hide
    /* @deprecated - use getSystemService as described above */
    public static TelephonyManager getDefault() {
        return sInstance;
    }
```

如果调用者不是系统应用的话，如何获取他的服务呢？

这里就要介绍TelephonyManager的注册过程了。

ContextImpl在初始化时注册了一些常用的Service，其中就包括TelephonyManager：

```Java
registerService(TELEPHONY_SERVICE, new ServiceFetcher() {
    public Object createService(ContextImpl ctx) {
        return new TelephonyManager(ctx.getOuterContext());
    }});
```

经过这样的注册，其他进程就可以通过Context对象的getSystemService()方法来获取其服务

```Java
TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
```

或者直接调用TelephonyManager的from()方法获取：

```Java
public static TelephonyManager from(Context context) {
    return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
}
```

### TelephonyManager的内部机制

TelephonyManager如何实现客户端对他的请求。

TelephonyManager并没有继承任何的父类，那么他是如何实现各项功能的呢？

在TelephonyManager内部，获取到了三个Service的客户端，其中构造函数中获取了TelephonyRegistry的服务：

```Java
    public TelephonyManager(Context context, int subId) {
        mSubId = subId;
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            mContext = appContext;
        } else {
            mContext = context;
        }
        mSubscriptionManager = SubscriptionManager.from(mContext);

        if (sRegistry == null) {
            获取TelephonyRegistry服务
            sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService(
                    "telephony.registry"));
        }
    }
```

通过getSubscriberInfo()获取了PhoneSubInfoProxy服务：

```
    private IPhoneSubInfo getSubscriberInfo() {
        // get it each time because that process crashes a lot
        return IPhoneSubInfo.Stub.asInterface(ServiceManager.getService("iphonesubinfo"));
    }

```

通过getITelephony()获取PhoneInterfaceManager服务：

```Java
    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
    }
```

TelephonyManager拿到这三个Service之后，就用三个Service提供的服务，以及SystemProperties提供的属性，搭建了自己的public方法集合。

也就是说，TelephonyManager自己并不具备处理事务的能力，而是汇集了其他三个Service的功能，向他的所有请求者提供便利的处理Telephony事务的能力。

### TelephonyManager小结

为什么需要构建这么一个东西来同时注册3个SystemService？

假如现在有3个模块A、B、C，都需要做一些Phone有关的操作，他们的需求如下：

+ A 需要用到TelephonyRegistry和PhoneSubInfoProxy的服务，那么他就要去分别申请这两个服务的代理对象
+ B 需要用到TelephonyRegistry和PhoneInterfaceManager服务，他也需要分别申请代理对象。
+ C 需要用到上面的3个服务，那么就需要申请3个代理对象

对于这样的情况，我们当然可以在每个需要的模块内部分别调用系统接口（ServiceManager.getService）去得到相应的代理对象。这种情况下我们需要调用7次getService方法得到7个SystemService的远程对象。

如果通过TelephonyRegistry的方式去实现，只需要在3个模块中，分别调用Context的getSystemService方法就能同时得到3个SystemService远程代理对象。而且我们得到的3个TelephonyManager对象是同一个对象，3个模块公用了同一个SystemService。因此，我们实际上只调用了3此getService方法，得到了3个SystemService远程对象。

TelephonyManager整合3个SystemService的意义就在于减轻系统负担，特别是一些SystemService的负担,提高了效率。

既然TelephonyManager大大减轻了一些SystemService的负担，为什么只整合了3个SystemService呢？或者说，为什么选中了这3个SystemService来整合

我们再来梳理以下TelephonyManager的运行原理。经过TelephonyManager的整合，当我们通过Context去得到TelephonyManager对象时，得到的是同一个TelephonyManager对象，那么我们进一步得到的SystemService也是同一个，此时我们调用TelephonyManager中的方法时，得到的返回值也是完全相同的。

这就说明了，TelephonyManager整合的SystemService，有一个共同特点：这些服务无论谁去调用，方法的返回值都是相同的。比如SIM卡的状态、当前的运营商信息、设备的ID号等。

而对于存在差异的SystemService，由于对于不同的客户端需要返回不同的值，当然就无法放到TelephonyManager中处理了。

## 总结

关于Telephony的大体内容就先记到这里，实际上的内容不止这些，只能在后续的使用中多多积累了。
