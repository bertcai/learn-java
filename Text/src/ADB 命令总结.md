## 引子
最近要经常使用adb，为了便于查阅，现在记录一些常见的adb命令，会不定期的更新相关命令。

## ADB命令

### log相关

格式：
```
Usage: logcat [options] [filterspecs]
```

参数
+ -s 设置默认的过滤器
    + 例如，输出art标签的信息
    ```
    cc@cc-OptiPlex-3020:~$ adb logcat -s art
    * daemon not running. starting it now on port 5037 *
    * daemon started successfully *
    --------- beginning of system
    --------- beginning of main
    01-17 09:58:47.516  1882  1887 I art     : Do partial code cache collection, code=7KB, data=6KB
    01-17 09:58:47.520  1882  1887 I art     : After code cache collection, code=6KB, data=6KB
    01-17 09:58:47.520  1882  1887 I art     : Increasing code cache capacity to 128KB
    ```
+ -f <filename> 输出log到指定文件,输出到手机上
    + 例如
    ```
    cc@cc-OptiPlex-3020:~$ adb logcat -f /Download/log.txt
    ```

+ -r 每千字节输出日志，需要在使用-f参数情况下使用

+ -n 设置日志输出的最大数目

+ -v 设置日志格式

+ -c 清空缓冲区日志信息

+ -d 将日志显示在控制台后退出

+ -e <expr> 只显示满足匹配的行的log

+ -m <count> 打印指定行log后退出

+ -t <count> 打印最近的count行log

+ -t ‘<time>' 打印从time到现在的log

+ -T <count> 打印最近的count行log，并不停止，继续打log

+ -T ’<time>‘ 打印从time到现在的log，并不停止，继续打log

+ -g 查看日志缓冲区信息

+ -b <buffer> 加载一个缓冲区，默认是main
    + 例如
    ```
    cc@cc-OptiPlex-3020:~$ adb logcat -b main -t 3
    01-17 10:31:38.181  4206  6461 D Q.msg.TroopMsgProxy: insertToList MessageRecord=friendUin:1418senderuin:5309,istroop:1,msgType:-1000,time:1516156298,shmsgseq:440917
    01-17 10:31:45.436  1870  1870 D HeadsetPhoneState: sendDeviceStateChanged. mService=1 mSignal=3 mRoam=0 mBatteryCharge=4
    01-17 10:31:45.437  1870  2404 D HeadsetStateMachine: Disconnected process message: 11, size: 0
    ```

+ -B 以二进制的方式输出日志

#### 过滤项参数
格式
```bash
<tag>[:priority]
eg：
adb logcat *:V
```

+ V : Verbose (明细);

+ D : Debug (调试);

+ I : Info (信息);

+ W : Warn (警告);

+ E : Error (错误);

+ F: Fatal (严重错误);

+ S : Silent(Super all output) (最高的优先级, 可能不会记载东西);

#### 使用管道过滤日志
格式

```
adb logcat | grep [option]
```
只要出现option就能过滤，无论它是不是标签

同时，也可以使用正则表达式

```
adb logcat | grep "^..Activity"
```
匹配XXActivity字符串

## 参考文档
下面附上help文档

```
Usage: logcat [options] [filterspecs]
options include:
  -s              Set default filter to silent.
                  Like specifying filterspec '*:S'
  -f <filename>   Log to file. Default is stdout
  --file=<filename>
  -r <kbytes>     Rotate log every kbytes. Requires -f
  --rotate-kbytes=<kbytes>
  -n <count>      Sets max number of rotated logs to <count>, default 4
  --rotate-count=<count>
  -v <format>     Sets the log print format, where <format> is:
  --format=<format>
                      brief color epoch long monotonic printable process raw
                      tag thread threadtime time uid usec UTC year zone

  -D              print dividers between each log buffer
  --dividers
  -c              clear (flush) the entire log and exit
  --clear
  -d              dump the log and then exit (don't block)
  -e <expr>       only print lines where the log message matches <expr>
  --regex <expr>  where <expr> is a regular expression
  -m <count>      quit after printing <count> lines. This is meant to be
  --max-count=<count> paired with --regex, but will work on its own.
  --print         paired with --regex and --max-count to let content bypass
                  regex filter but still stop at number of matches.
  -t <count>      print only the most recent <count> lines (implies -d)
  -t '<time>'     print most recent lines since specified time (implies -d)
  -T <count>      print only the most recent <count> lines (does not imply -d)
  -T '<time>'     print most recent lines since specified time (not imply -d)
                  count is pure numerical, time is 'MM-DD hh:mm:ss.mmm...'
                  'YYYY-MM-DD hh:mm:ss.mmm...' or 'sssss.mmm...' format
  -g              get the size of the log's ring buffer and exit
  --buffer-size
  -G <size>       set size of log ring buffer, may suffix with K or M.
  --buffer-size=<size>
  -L              dump logs from prior to last reboot
  --last
  -b <buffer>     Request alternate ring buffer, 'main', 'system', 'radio',
  --buffer=<buffer> 'events', 'crash', 'default' or 'all'. Multiple -b
                  parameters are allowed and results are interleaved. The
                  default is -b main -b system -b crash.
  -B              output the log in binary.
  --binary
  -S              output statistics.
  --statistics
  -p              print prune white and ~black list. Service is specified as
  --prune         UID, UID/PID or /PID. Weighed for quicker pruning if prefix
                  with ~, otherwise weighed for longevity if unadorned. All
                  other pruning activity is oldest first. Special case ~!
                  represents an automatic quicker pruning for the noisiest
                  UID as determined by the current statistics.
  -P '<list> ...' set prune white and ~black list, using same format as
  --prune='<list> ...'  printed above. Must be quoted.
  --pid=<pid>     Only prints logs from the given pid.
  --wrap          Sleep for 2 hours or when buffer about to wrap whichever
                  comes first. Improves efficiency of polling by providing
                  an about-to-wrap wakeup.

filterspecs are a series of
  <tag>[:priority]

where <tag> is a log component tag (or * for all) and priority is:
  V    Verbose (default for <tag>)
  D    Debug (default for '*')
  I    Info
  W    Warn
  E    Error
  F    Fatal
  S    Silent (suppress all output)

'*' by itself means '*:D' and <tag> by itself means <tag>:V.
If no '*' filterspec or -s on command line, all filter defaults to '*:V'.
eg: '*:S <tag>' prints only <tag>, '<tag>:S' suppresses all <tag> log messages.

If not specified on the command line, filterspec is set from ANDROID_LOG_TAGS.

If not specified with -v on command line, format is set from ANDROID_PRINTF_LOG
or defaults to "threadtime"
```
