## 引子

为了学习Phone的创建过程，使用自己的手机查看log，分析Phone创建过程。

## PhoneGlobals




## PhoneFactory

Phone创建对像，进入PhoneFactory，makeDefaultPhone方法

```
01-19 11:25:42.189  2092  2092 I PhoneFactory: Cdma Subscription set to 0
//Rlog.i(LOG_TAG, "Cdma Subscription set to " + cdmaSubscription);
//设置卡槽，0号
01-19 11:25:42.193  2092  2092 I PhoneFactory: Network Mode set to 20
// Rlog.i(LOG_TAG, "Network Mode set to " + Integer.toString(networkModes[i]));
//设置网络模式，20，设置两次，因为是双卡的手机
01-19 11:25:42.205  2092  2092 I PhoneFactory: Network Mode set to 20
01-19 11:25:42.215  2092  2092 I PhoneFactory: Creating SubscriptionController
//创建卡槽控制
01-19 11:25:42.419  2092  2092 I PhoneFactory: Creating Phone with type = 1 sub = 0
01-19 11:25:42.504  2092  2092 I PhoneFactory: Creating Phone with type = 1 sub = 1
//Rlog.i(LOG_TAG, "Creating Phone with type = " + phoneType + " sub = " + i);
//GSM类型Phone对象，分别在0号卡槽和1号卡槽
01-19 11:25:42.508  2092  2092 I PhoneFactory: defaultSmsApplication: NONE
//Rlog.i(LOG_TAG, "defaultSmsApplication: " + packageName);
01-19 11:25:42.510  2092  2092 I PhoneFactory: Creating SubInfoRecordUpdater
//Rlog.i(LOG_TAG, "Creating SubInfoRecordUpdater ");
```

## TelephonyComponentFactory

```Bash
01-19 11:25:42.179  2092  2092 D TelephonyComponentFactory:
    classLoader = dalvik.system.PathClassLoader[DexPathList[
    [zip file "/system/framework/qti-telephony-common.jar"],
    nativeLibraryDirectories=[/system/lib64, /vendor/lib64]]]
//Rlog.d(LOG_TAG, "classLoader = " + classLoader);
01-19 11:25:42.180  2092  2092 D TelephonyComponentFactory:
    cls = class com.qualcomm.qti.
    internal.telephony.QtiTelephonyComponentFactory
//cls = Class.forName(fullClsName, false, classLoader);
//Rlog.d(LOG_TAG, "cls = " + cls);
01-19 11:25:42.180  2092  2092 D TelephonyComponentFactory:
    constructor method = public com.qualcomm.qti.internal
    .telephony.QtiTelephonyComponentFactory()
//Constructor custMethod = cls.getConstructor()
//Rlog.d(LOG_TAG, "constructor method = " + custMethod);

//下面全是一些初始化操作 在TelephonyComponentFactory内可以看到源码
01-19 11:25:42.215  2092  2092 D QtiTelephonyComponentFactory:
    initSubscriptionController
//初始化多卡管理
01-19 11:25:42.236  2092  2092 D QtiTelephonyComponentFactory:
    makePhone
//创建Phone实例，根据不同Phone类型构建
01-19 11:25:42.246  2092  2092 D QtiTelephonyComponentFactory:
    makeSmsStorageMonitor
//创建短信存储监视器
01-19 11:25:42.246  2092  2092 D TelephonyComponentFactory: makeSmsStorageMonitor
//由于QtiTelephonyComponentFactory是继承的TelephonyComponentFactory，
//所以直接super()调用父类的方法，创建短信存储监视器
01-19 11:25:42.247  2092  2092 D QtiTelephonyComponentFactory: makeSmsUsageMonitor
01-19 11:25:42.248  2092  2092 D TelephonyComponentFactory: makeSmsUsageMonitor
//创建短信使用监视器
01-19 11:25:42.268  2092  2092 D QtiTelephonyComponentFactory: makeGsmCdmaCallTracker
01-19 11:25:42.268  2092  2092 D TelephonyComponentFactory: makeGsmCdmaCallTracker
//创建CallTracker
//下面的都差不多
01-19 11:25:42.273  2092  2092 D QtiTelephonyComponentFactory: makeIccPhoneBookInterfaceManager
01-19 11:25:42.273  2092  2092 D TelephonyComponentFactory: makeIccPhoneBookInterfaceManager
01-19 11:25:42.280  2092  2092 D QtiTelephonyComponentFactory: makeIccSmsInterfaceManager
01-19 11:25:42.281  2092  2092 D TelephonyComponentFactory: makeIccSmsInterfaceManager
01-19 11:25:42.298  2092  2092 D QtiTelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.298  2092  2092 D TelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.300  2092  2092 D QtiTelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.300  2092  2092 D TelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.310  2092  2092 D QtiTelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.310  2092  2092 D TelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.312  2092  2092 D QtiTelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.312  2092  2092 D TelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.325  2092  2092 D QtiTelephonyComponentFactory: makeIccCardProxy
01-19 11:25:42.325  2092  2092 D TelephonyComponentFactory: makeIccCardProxy
01-19 11:25:42.333  2092  2092 D QtiTelephonyComponentFactory: getCdmaSubscriptionSourceManagerInstance
01-19 11:25:42.333  2092  2092 D TelephonyComponentFactory: getCdmaSubscriptionSourceManagerInstance
01-19 11:25:42.334  2092  2092 D QtiTelephonyComponentFactory: makeEriManager
01-19 11:25:42.334  2092  2092 D TelephonyComponentFactory: makeEriManager
01-19 11:25:42.342  2092  2092 D QtiTelephonyComponentFactory: makeQtiServiceStateTracker
01-19 11:25:42.374  2092  2092 D QtiTelephonyComponentFactory: makeQtiDcTracker
01-19 11:25:42.419  2092  2092 D QtiTelephonyComponentFactory: makePhone
01-19 11:25:42.420  2092  2092 D QtiTelephonyComponentFactory: makeSmsStorageMonitor
01-19 11:25:42.420  2092  2092 D TelephonyComponentFactory: makeSmsStorageMonitor
01-19 11:25:42.421  2092  2092 D QtiTelephonyComponentFactory: makeSmsUsageMonitor
01-19 11:25:42.421  2092  2092 D TelephonyComponentFactory: makeSmsUsageMonitor
01-19 11:25:42.428  2092  2092 D QtiTelephonyComponentFactory: makeGsmCdmaCallTracker
01-19 11:25:42.429  2092  2092 D TelephonyComponentFactory: makeGsmCdmaCallTracker
01-19 11:25:42.430  2092  2092 D QtiTelephonyComponentFactory: makeIccPhoneBookInterfaceManager
01-19 11:25:42.431  2092  2092 D TelephonyComponentFactory: makeIccPhoneBookInterfaceManager
01-19 11:25:42.433  2092  2092 D QtiTelephonyComponentFactory: makeIccSmsInterfaceManager
01-19 11:25:42.433  2092  2092 D TelephonyComponentFactory: makeIccSmsInterfaceManager
01-19 11:25:42.438  2092  2092 D QtiTelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.438  2092  2092 D TelephonyComponentFactory: getIDeviceIdleController
01-19 11:25:42.440  2092  2092 D QtiTelephonyComponentFact
```

## CdmaSubscriptionSourceManager

设置卡槽管理
```
01-19 11:25:42.189  2092  2092 D CdmaSSM : subscriptionSource from settings: 0
当前网络来源是0号卡槽
```


## UiccController

UiccController 是对SIM卡管理的控制器,它通过 UiccCard 来更新SIM卡的信息。
```
01-19 11:25:42.235  2092  2092 D UiccController: Creating UiccController
//if (DBG) log("Creating UiccController");
01-19 11:25:42.708  2092  2092 D UiccController: handleMessage slotId=0
//
01-19 11:25:42.709  2092  2092 D UiccController: Received EVENT_ICC_STATUS_CHANGED, calling getIccCardStatus
//if (DBG) log("Received EVENT_ICC_STATUS_CHANGED, calling getIccCardStatus");
01-19 11:25:42.709  2092  2092 D UiccController: handleMessage slotId=1
01-19 11:25:42.710  2092  2092 D UiccController: Received EVENT_ICC_STATUS_CHANGED, calling getIccCardStatus
01-19 11:25:43.193  2092  2092 D UiccController: handleMessage slotId=1
01-19 11:25:43.193  2092  2092 D UiccController: Received EVENT_GET_ICC_STATUS_DONE
//if (DBG) log("Received EVENT_GET_ICC_STATUS_DONE");
01-19 11:25:43.347  2092  2092 D UiccController: Notifying IccChangedRegistrants
//if (DBG) log("Notifying IccChangedRegistrants");

```


## QtiDcTracker

在看QitGsmDCT的log时，通过如下指令可以查询缓存区内所有相关log，简要adb命令说明，查阅[adb命令简析]()，详细说明请查阅相关文档。

```
admin@admin-OptiPlex-3020:~$ adb logcat -b all | grep "QtiGsmDCT"
```

```
01-19 11:25:42.410  2092  2092 D QtiGsmDCT: [0]QtiGsmDCT.constructor
//if (DBG) log(LOG_TAG + ".constructor"); 在构造函数里
01-19 11:25:42.504  2092  2092 D QtiGsmDCT: [1]QtiGsmDCT.constructor
01-19 11:25:42.752  2092  2092 D QtiGsmDCT: [0]QtiDcTracker handleMessage
    msg={ when=-378ms what=270369 obj=android.os.AsyncResult@fc50771
    target=com.qualcomm.qti.internal.telephony.dataconnection.QtiDcTracker
    planTime=1516332342375 dispatchTime=1516332342752 finishTime=0 }
/*    @Override
      public void handleMessage (Message msg) {
          if (DBG) log("QtiDcTracker handleMessage msg=" + msg);

          switch (msg.what) {
              case EVENT_MODEM_DATA_PROFILE_READY:
                  onModemApnProfileReady();
                  break;

              default:
                  super.handleMessage(msg);
                  break;
          }
      }
   */
   // 这里就是接收发送过来的信息进行相关的处理，看源码调用了父类的handleMessage，
   // 转到父类的handleMessage看看什么情况,下面大部分log都是在父类的handleMessage中产生

01-22 10:23:44.870  2098  2098 D QtiGsmDCT: [0]Wifi state changed
01-22 10:23:44.870  2098  2098 D QtiGsmDCT: [0]WIFI_STATE_CHANGED_ACTION: enabled=true mIsWifiConnected=false
// 关于WiFi的两行log，比较清楚，就是监听到了wifi状态的改变，wifi开关打开了，但是并没有连接到WiFi

01-22 10:23:44.871  2098  2098 D QtiGsmDCT: [0]onRadioAvailable
/*    private void onRadioAvailable() {
        if (DBG) log("onRadioAvailable");
        if (mPhone.getSimulatedRadioControl() != null) {
            // Assume data is connected on the simulator
            // FIXME  this can be improved
            // setState(DctConstants.State.CONNECTED);
            notifyDataConnection(null);

            log("onRadioAvailable: We're on the simulator; assuming data is connected");
        }

        IccRecords r = mIccRecords.get();
        if (r != null && r.getRecordsLoaded()) {
            notifyOffApnsOfAvailability(null);
        }

        if (getOverallState() != DctConstants.State.IDLE) {
            cleanUpConnection(true, null);
        }
    }
    */
    // 这里的意思我也不是很清楚，应该是指，无线功能现在是可用的了
    // getSimulatedRadioControl这个方法应该是获取无线连接控制实例，如果存在，就表示数据连接现在是连接状态了

01-22 10:23:44.871  2098  2098 D QtiGsmDCT: [0]onRadioOffOrNotAvailable: is off and clean up all connections
/*     private void onRadioOffOrNotAvailable() {
           // Make sure our reconnect delay starts at the initial value
           // next time the radio comes on

           mReregisterOnReconnectFailure = false;

           if (mPhone.getSimulatedRadioControl() != null) {
               // Assume data is connected on the simulator
               // FIXME  this can be improved
               log("We're on the simulator; assuming radio off is meaningless");
           } else {
               if (DBG) log("onRadioOffOrNotAvailable: is off and clean up all connections");
               cleanUpAllConnections(false, Phone.REASON_RADIO_TURNED_OFF);
           }
           notifyOffApnsOfAvailability(null);
       }*/
       // 这个方法是用来缺点当前的radio是否真的处于关闭状态，如果是，则打log表明，并且清除掉所有的连接
       // 否则说明情况
       // 这一行log表示现在raido确实是off态，清除掉所有的连接

01-22 10:23:44.871  2098  2098 D QtiGsmDCT: [0]cleanUpAllConnections: tearDown=false reason=radioTurnedOff
// 这里的源码太长了，贴在这很不方便，我就贴一个网址在这，自己查吧，并且我把注释贴在下面解释一下这个函数在干什么
```

源码地址[DcTracker源码-Android7.0](http://androidxref.com/7.0.0_r1/xref/frameworks/opt/telephony/src/java/com/android/internal/telephony/dataconnection/DcTracker.java#1704)

```
// 接上文
    /**
     * If tearDown is true, this only tears down a CONNECTED session. Presently,
     * there is no mechanism for abandoning an CONNECTING session,
     * but would likely involve cancelling pending async requests or
     * setting a flag or new state to ignore them when they came in
     * @param tearDown true if the underlying DataConnection should be
     * disconnected.
     * @param reason reason for the clean up.
     * @return boolean - true if we did cleanup any connections, false if they
     *                   were already all disconnected.
     */
// 这个方法接收两个参数，一个tearDown表示下层DataConnection是否应该被断开（此处存疑，不是很确定是不是这个意思）
// 一个reason表示进行clean up的原因
// 返回一个boolean表示清除工作是否完全完成
// 上述log就表示下层DataConnection不应被断开，clean up的原因是radio关闭了

01-22 10:23:44.887  2098  2098 D QtiGsmDCT: [0]setupDataForSinglePdn: reason = radioTurnedOff isDisconnected = true
/*  protected void setupDataForSinglePdnArbitration(String reason) {
        // In single pdn case, if a higher priority call which was scheduled for retry gets
        // cleaned up due to say apn disabled, we need to try setup data on connectable apns
        // as there won't be any EVENT_DISCONNECT_DONE call back.
        if(DBG) {
            log("setupDataForSinglePdn: reason = " + reason
                    + " isDisconnected = " + isDisconnected());
        }
        if (isOnlySingleDcAllowed(mPhone.getServiceState().getRilDataRadioTechnology())
                && isDisconnected()
                && !Phone.REASON_SINGLE_PDN_ARBITRATION.equals(reason)
                && !Phone.REASON_RADIO_TURNED_OFF.equals(reason)) {
            sendMessage(obtainMessage(DctConstants.EVENT_TRY_SETUP_DATA,
                    Phone.REASON_SINGLE_PDN_ARBITRATION));
        }
    }*/

// cdma网络只支持单通道网络，在这种情况下，如果有个高优先级call由于apn失效而被清除，就需要
// 设置数据连接到可连接的apn上，这样就不会出现EVENT_DISCONNECT_DONE返回

01-22 10:23:44.887  2098  2098 D QtiGsmDCT: [0]isOnlySingleDcAllowed(0): false
// 这个也比较清楚，就是返回是否为仅支持一个网络，也就是单卡，这里我是用小米5s测的，小米5s是双卡手机
// 所以返回false

01-22 10:23:44.935  2098  2098 D QtiGsmDCT: [0]stopNetStatPoll
//这个就是在状态栏中关闭数据连接图标的，没有网时候，调用这个关掉数据连接的状态图标

01-22 10:23:44.936  2098  2098 D QtiGsmDCT: [0]cleanUpConnection: mDisconnectPendingCount = 0
//待清理的连接为0，即已经清理完所有连接了

01-22 10:23:44.937  2098  2098 D QtiGsmDCT: [0]isDataAllowed: No - SIM not loaded - Not attached
// 不支持数据服务，SIM卡未加载，未绑定到数据网络

01-22 10:23:44.961  2098  2098 D QtiGsmDCT: [0]onDataConnectionDetached: stop polling and notify detached

/*      private void onDataConnectionDetached() {
            /*
             * We presently believe it is unnecessary to tear down the PDP context
             * when GPRS detaches, but we should stop the network polling.
             */
            if (DBG) log ("onDataConnectionDetached: stop polling and notify detached");
            stopNetStatPoll();
            stopDataStallAlarm();
            notifyDataConnection(Phone.REASON_DATA_DETACHED);
            mAttached.set(false);
        }*/
//关掉数据连接图标，不再检测数据业务是否仅发送无接收。

01-22 10:23:45.031  2098  2098 D QtiGsmDCT:
    [0]setupDataOnConnectableApns: roamingOff
    mms:[state=IDLE,enabled=false]
    hipri:[state=IDLE,enabled=false]
    supl:[state=IDLE,enabled=false]
    cbs:[state=IDLE,enabled=false]
    dun:[state=IDLE,enabled=false]
    fota:[state=IDLE,enabled=false]
    ims:[state=IDLE,enabled=false]
    default:[state=IDLE,enabled=false]

// 设置数据业务，在可用的APN上

// 上面就是卡槽[0]的一系列DcTracker操作，卡槽0我插的是电信卡，卡槽1我插的是联通卡，二者的log都比较类似，我就不重复了

01-22 10:23:45.840  2098  2098 D QtiGsmDCT: [0]update sub = 1
01-22 10:23:45.840  2098  2098 D QtiGsmDCT: [0]update(): Active DDS, register for all events now!
01-22 10:23:45.840  2098  2098 D QtiGsmDCT: [0]New records found.
// 绑定Phone对象，注册各种事件到卡槽0，同时注册sim卡状态监听器

01-22 10:23:45.908  2098  2098 D QtiGsmDCT: [1]update sub = 2
01-22 10:23:45.908  2098  2098 D QtiGsmDCT: [1]update(): Active DDS, register for all events now!
01-22 10:23:45.909  2098  2098 D QtiGsmDCT: [1]New records found.
// 同上

01-22 10:23:48.092  2098  2098 D QtiGsmDCT: [0]SubscriptionListener.onSubscriptionInfoChanged
//卡槽情况改变

01-22 10:23:48.139  2098  2098 D QtiGsmDCT: [0]Provisioned Sim Detected on subId: 1
// sim1卡槽支持

01-22 10:23:48.140  2098  2098 D QtiGsmDCT: [0]onRecordsLoaded: createAllApnList
/*private void onRecordsLoaded() {
      mAutoAttachOnCreationConfig = mPhone.getContext().getResources().getBoolean(com.android.internal.R.bool.config_auto_attach_data_on_creation);
      //创建APN参数
      createAllApnList();
      setInitialAttachApn();
      if (mPhone.mCi.getRadioState().isOn()) {
          notifyOffApnsOfAvailability(Phone.REASON_SIM_LOADED);
      }
      //尝试发起数据业务
      setupDataOnConnectableApns(Phone.REASON_SIM_LOADED);
  }  */
// 加载记录，创建APNlist

01-22 10:23:48.156  2098  2098 D QtiGsmDCT:
    [0]getPreferredApn: mRequestedApnType=default
    cursor=android.content.ContentResolver$CursorWrapperInner@71024b2 cursor.count=1
// 获取前置apn参数，设置类型

01-22 10:23:48.156  2098  2098 D QtiGsmDCT: [0]createAllApnList: mPreferredApn=null
// 创建apn列表，前置APN为空

01-22 10:23:48.156  2098  2098 D QtiGsmDCT: [0]setDataProfilesAsNeeded
// 按照apn需要设置数据

// 下面又是联通卡中类似的操作，就不重复了

01-22 10:23:49.863  2098  2098 D QtiGsmDCT: [0]NETWORK_STATE_CHANGED_ACTION: mIsWifiConnected=false

// 网络状态改变，WiFi未连接

01-22 10:23:50.742  2098  2308 D QtiGsmDCT: [0]hasMatchedTetherApnSetting: APN=null

// 暂时不清楚，猜测是匹配APN设置

01-22 10:23:51.475  2098  2098 D QtiGsmDCT: [1]onDataConnectionAttached
/*    private void onDataConnectionAttached() {
          if (DBG) log("onDataConnectionAttached");
          mAttached.set(true);
          if (getOverallState() == DctConstants.State.CONNECTED) {
              if (DBG) log("onDataConnectionAttached: start polling notify attached");
              startNetStatPoll();
              startDataStallAlarm(DATA_STALL_NOT_SUSPECTED);
              notifyDataConnection(Phone.REASON_DATA_ATTACHED);
          } else {
              // update APN availability so that APN can be enabled.
              notifyOffApnsOfAvailability(Phone.REASON_DATA_ATTACHED);
          }
          if (mAutoAttachOnCreationConfig) {
              mAutoAttachOnCreation.set(true);
          }
          setupDataOnConnectableApns(Phone.REASON_DATA_ATTACHED);
      }

// 绑定数据连接，启动状态栏的数据连接图标等

01-22 10:23:51.571  2098  2098 D QtiGsmDCT: [1]isConnectable() call trySetupData
/*  private boolean trySetupData(ApnContext apnContext) {
      boolean isEmergencyApn = apnContext.getApnType().equals(PhoneConstants.APN_TYPE_EMERGENCY);
      boolean desiredPowerState = mPhone.getServiceStateTracker().getDesiredPowerState();
      boolean checkUserDataEnabled = !(apnContext.getApnType().equals(PhoneConstants.APN_TYPE_IMS));
      if (apnContext.isConnectable() && (isEmergencyApn || (isDataAllowed(apnContext) && getAnyDataEnabled(checkUserDataEnabled) && !isEmergency()))) {
          int radioTech = mPhone.getServiceState().getRilDataRadioTechnology();
          if (apnContext.getState() == DctConstants.State.IDLE) {
              ArrayList<ApnSetting> waitingApns = buildWaitingApns(apnContext.getApnType(), radioTech);
              if (waitingApns.isEmpty()) {
                  notifyNoData(DcFailCause.MISSING_UNKNOWN_APN, apnContext);
                  notifyOffApnsOfAvailability(apnContext.getReason());
                  return false;
              } else {
                  apnContext.setWaitingApns(waitingApns);
              }
          }
          //建立连接
          boolean retValue = setupData(apnContext, radioTech);
          notifyOffApnsOfAvailability(apnContext.getReason());
          return retValue;
      } else {
      }
    } */

// 发起连接请求,在trySetupData中主要做有效性检查
/*      apnContext.isConnectable()
            ----判断当前APN是否已经被激活；
        isEmergencyApn()
            ----当前APN是否为紧急APN；
        isDataAllowed()
            ----判断是否已经ATTACH成功，SIM是否初始化完毕，当前手机服务是否支持，漫游下是否允许上网等；
        getAnyDataEnabled()
            ----该条件主要判断用户是否打开了数据开关；
            */


01-22 10:23:51.601  2098  2098 D QtiGsmDCT: [1]isEmergency: result=false
// 是否需要紧急电话网络， 插了卡了 自然不需要了

01-23 10:46:20.838  2096  2096 D QtiGsmDCT: [0]trySetupData failed.
    apnContext = [type=default, mState=IDLE, mDataEnabled=true, mDependencyMet=true]
    data not allowed: isDataAllowed: No - SIM not loaded - Not attached.
// 由于我现在使用的是卡槽1的数据网络，所以卡槽0的数据网络连接失败，看后面的信息SIM not loaded
// 就可以了解到了

01-23 10:46:21.212  2096  2096 D QtiGsmDCT: [0]get all active apn types
// 获取所有活动的apn类型

01-23 10:46:21.855  2096  2096 D QtiGsmDCT: [1]createAllApnList: selection=numeric = '46001'
// 重头戏开始了，创建APNlist，46001就是联通的网络号，联通网要开始连接了
// 网络号
/* MCC     MNC        运营商
   460     00         中国移动
   460     01         中国联通
   460     02         中国移动
   460     03         中国电信
   460     06         中国联通
   460     07         中国移动
   460     20         中国铁通
   460     05         中国电信
   */

01-23 10:46:21.858  2096  2096 D QtiGsmDCT: [1]createApnList: X result=[[ApnSettingV3]
    沃宽带用户连接互联网, 2463, 46001, 3gnet, , , , , , -1, default
    | supl, IPV4V6, IPV4V6, true, 0, 0, 0, false, 0, 0, 0, 0, , , false,
    [ApnSettingV3] 沃宽带用户手机上网, 2464, 46001, 3gwap, 10.0.0.172, , , , 80, -1,
    default | supl, IPV4V6, IPV4V6, true, 0, 0, 0, false, 0, 0, 0, 0, , , false,
    [ApnSettingV3] 联通彩信, 2465, 46001, 3gwap, 10.0.0.172,
    http://mmsc.myuni.com.cn, 10.0.0.172, 80, 80, -1, mms, IPV4V6,
    IPV4V6, true, 0, 0, 0, false, 0, 0, 0, 0, , , false, [ApnSettingV3]
    联通IMS, 2466, 46001, ims, , , , , , -1, ims, IPV4V6, IPV4V6, true,
    0, 0, 0, false, 0, 0, 0, 0, , , false]

// 联通的apn参数

01-23 10:46:21.860  2096  2096 D QtiGsmDCT: [1]setInitialApn: E mPreferredApn=[ApnSettingV3]
    沃宽带用户连接互联网, 2463, 46001, 3gnet, , , , , , -1, default | supl, IPV4V6, IPV4V6, true,
    0, 0, 0, false, 0, 0, 0, 0, , , false
01-23 10:46:21.860  2096  2096 D QtiGsmDCT: [1]setInitialApn: firstApnSetting=[ApnSettingV3]
    沃宽带用户连接互联网, 2463, 46001, 3gnet, , , , , , -1, default | supl, IPV4V6, IPV4V6, true,
    0, 0, 0, false, 0, 0, 0, 0, , , false
01-23 10:46:21.860  2096  2096 D QtiGsmDCT: [1]setInitialApn: defaultApnSetting=[ApnSettingV3]
    沃宽带用户连接互联网, 2463, 46001, 3gnet, , , , , , -1, default | supl, IPV4V6, IPV4V6, true,
    0, 0, 0, false, 0, 0, 0, 0, , , false

// 设置初始化APN

01-23 10:46:21.860  2096  2096 D QtiGsmDCT: [1]setInitialAttachApn: using mPreferredApn
01-23 10:46:21.860  2096  2096 D QtiGsmDCT: [1]setInitialAttachApn: X selected Apn=[ApnSettingV3]
    沃宽带用户连接互联网, 2463, 46001, 3gnet, , , , , , -1, default | supl, IPV4V6, IPV4V6, true,
    0, 0, 0, false, 0, 0, 0, 0, , , false
// 使用前置apn参数，前置的apn参数是...
            // The priority of apn candidates from highest to lowest is:
            //   1) APN_TYPE_IA (Initial Attach)
            //   2) mPreferredApn, i.e. the current preferred apn
            //   3) The first apn that than handle APN_TYPE_DEFAULT
            //   4) The first APN we can find.
// APN优先级列表，1 APN_TYPE_IA (Initial Attach)，2 最近的apn，3 默认的第一个apn 4 我们能够找到的第一个APN


```



## 参考文章

[数据业务建立流程之发起网络连接过程](http://blog.csdn.net/u010961631/article/details/49612809)

