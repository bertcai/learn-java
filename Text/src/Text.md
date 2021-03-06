## 引子

好久没写东西了，看了看记录，上一次好像是12月份的事了，现在1月都快过了一半，就补几篇文章吧，这篇关于正则的主要是最近在写一个dialer应用，应用中查询联系人用到了这部分类容。

## 问题引入
在查询联系人的时候，我们通过电话号码进行查询，存储电话号码的数据库中，关于号码的字符串存储格式不同的手机有所不同，例如西小米的就是`XXX XXXX XXXX“
，而还有一部分手机是”X XXX-XXX-XXX";同时，在进行所要查询的输入时，如果是在联系人界面查询还好，但是如果狮子啊拨号界面进行东塔查询时，为了用户体验，在用户输入手机号或者电话号码的时候，会插入部分空格，这也会使查询出现问题。所以，针对这种情况，就要将所有关于号码的字符串进行过滤，只保留数字字符，然后对这些字符进行查询，问题就能得到解决。

## 解决

Java关于字符传替换有两个函数

```Java
public String replace(CharSequence target, CharSequence replacement)
// 传入两个字符序列，用后面的替换前面的

public String replaceAll(String regex, Sring replacement)
// 传入一个正则表达式字符串和一个目标字符串，对源字符串进行正则表达式匹配，将满足正则匹配的内容替换为replacement

```

对于上面的问题，只需要编写正则`[^0-9]`匹配所有不是数字的字符就能解决这个问题了，那么下面就是正式开始写写关于正则的事情了。

## 正则表达式

+ 正则表达式定义了字符串的模式
+ 正则表达式可以用来搜索、编辑和处理文本
+ 正则表达式并不局限于某一种语言，但是在每种语言中都有细微的区别

### 正则表达式的实例

一个字符传就是一个正则表达式，例如`”hello world“`，就是匹配`hello world`字符传的正则表达式，`.`也是一个正则表达式，它匹配任意一个字符，例如`a`或者`0`。

### Java中的正则表达式

Java中，正则表达式类是定义在`java.util.regex`包中，它主要包括三个类

+ Pattern
    + pattern 对象是一个正则表达式的编译表示。Pattern 类没有公共构造方法。要创建一个 Pattern 对象，你必须首先调用其公共静态编译方法，它返回一个 Pattern 对象。该方法接受一个正则表达式作为它的第一个参数。
+ Matcher
    + Matcher 对象是对输入字符串进行解释和匹配操作的引擎。与Pattern 类一样，Matcher 也没有公共构造方法。你需要调用 Pattern 对象的 matcher 方法来获得一个 Matcher 对象。
+ PatternSyntaxException
    + PatternSyntaxException 是一个非强制异常类，它表示一个正则表达式模式中的语法错误。



#### 示例
```Java
import java.util.regex.*;

public class RegexExample {
    public static void main(String args[]) {
        String content = "hello world";
        Srring pattern = ".*hello.*";
        if(Pattern.matches(pattern, content) {
            System.out.println("True");
        } else {
            System.out.println("False");
        }
    }
}
```
上面这段代码就是一个例子，它用来检测目标字符串是否含有子串hello，有则打印`true`，否则`false`，当然你可以使用`boolean contains(CharSequence s)`做到相同的事。

### 捕获组

捕获组是把多个字符当一个单独单元进行处理的方法，它通过对括号内的字符分组来创建。

例如，正则表达式 (dog) 创建了单一分组，组里包含"d"，"o"，和"g"。

捕获组是通过从左至右计算其开括号来编号。例如，在表达式（（A）（B（C））），有四个这样的组：

+ ((A)(B(C)))
+ (A)
+ (B(C))
+ (C)

可以通过调用 matcher 对象的 groupCount 方法来查看表达式有多少个分组。groupCount 方法返回一个 int 值，表示matcher对象当前有多个捕获组。

还有一个特殊的组（group(0)），它总是代表整个表达式。该组不包括在 groupCount 的返回值中。


#### 示例

```Java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches
{
    public static void main( String args[] ){

      // 按指定模式在字符串查找
      String line = "This order was placed for QT3000! OK?";
      String pattern = "(\\D*)(\\d+)(.*)";

      // 创建 Pattern 对象
      Pattern r = Pattern.compile(pattern);

      // 现在创建 matcher 对象
      Matcher m = r.matcher(line);
      if (m.find( )) {
         System.out.println("Found value: " + m.group(0) );
         System.out.println("Found value: " + m.group(1) );
         System.out.println("Found value: " + m.group(2) );
         System.out.println("Found value: " + m.group(3) );
      } else {
         System.out.println("NO MATCH");
      }
   }
}
```

结果

```
Found value: This order was placed for QT3000! OK?
Found value: This order was placed for QT
Found value: 3000
Found value: ! OK?
```

### 正则表达式的语法

这里只需要注意一点，对于正则来说，`\`是特殊意义字符，如果我们需要使用`\`，就需要使用`\`进行转义，像这样`\\`表示一个反斜杠，当在Java中，正则表达式一个反斜杠就要求两个反斜杠进行转义表示，所以在Java中使用时，想表示一个普通的反斜杠表达式为`\\\\`，表示一个数字`\\d`。

关于正则表达式有一张表，如下

字符 |说明
---|---
\\|将下一字符标记为特殊字符、文本、反向引用或八进制转义符。例如，"n"匹配字符"n"。"\n"匹配换行符。序列"\\\\"匹配"\\"，"\\("匹配"("。
^|匹配输入字符串开始的位置。如果设置了 RegExp 对象的 Multiline 属性，^ 还会与"\n"或"\r"之后的位置匹配。
$|匹配输入字符串结尾的位置。如果设置了 RegExp 对象的 Multiline 属性，$ 还会与"\n"或"\r"之前的位置匹配。
\*|零次或多次匹配前面的字符或子表达式。例如，zo* 匹配"z"和"zoo"。* 等效于 {0,}。
\+|一次或多次匹配前面的字符或子表达式。例如，"zo+"与"zo"和"zoo"匹配，但与"z"不匹配。+ 等效于 {1,}。
?|零次或一次匹配前面的字符或子表达式。例如，"do(es)?"匹配"do"或"does"中的"do"。? 等效于 {0,1}。
{n}|n 是非负整数。正好匹配 n 次。例如，"o{2}"与"Bob"中的"o"不匹配，但与"food"中的两个"o"匹配。
{n,}|n 是非负整数。至少匹配 n 次。例如，"o{2,}"不匹配"Bob"中的"o"，而匹配"foooood"中的所有 o。"o{1,}"等效于"o+"。"o{0,}"等效于"o*"。
{n,m}|M 和 n 是非负整数，其中 n <= m。匹配至少 n 次，至多 m 次。例如，"o{1,3}"匹配"fooooood"中的头三个 o。'o{0,1}' 等效于 'o?'。注意：您不能将空格插入逗号和数字之间。
?|当此字符紧随任何其他限定符（*、+、?、{n}、{n,}、{n,m}）之后时，匹配模式是"非贪心的"。"非贪心的"模式匹配搜索到的、尽可能短的字符串，而默认的"贪心的"模式匹配搜索到的、尽可能长的字符串。例如，在字符串"oooo"中，"o+?"只匹配单个"o"，而"o+"匹配所有"o"。
.|匹配除"\r\n"之外的任何单个字符。若要匹配包括"\r\n"在内的任意字符，请使用诸如"[\s\S]"之类的模式。
(pattern)|匹配 pattern 并捕获该匹配的子表达式。可以使用 $0…$9 属性从结果"匹配"集合中检索捕获的匹配。若要匹配括号字符 ( )，请使用"\("或者"\)"。
(?:pattern)|匹配 pattern 但不捕获该匹配的子表达式，即它是一个非捕获匹配，不存储供以后使用的匹配。这对于用"or"字符 (|) 组合模式部件的情况很有用。例如，'industr(?:y|ies) 是比 'industry|industries' 更经济的表达式。
(?=pattern)|执行正向预测先行搜索的子表达式，该表达式匹配处于匹配 pattern 的字符串的起始点的字符串。它是一个非捕获匹配，即不能捕获供以后使用的匹配。例如，'Windows (?=95|98|NT|2000)' 匹配"Windows 2000"中的"Windows"，但不匹配"Windows 3.1"中的"Windows"。预测先行不占用字符，即发生匹配后，下一匹配的搜索紧随上一匹配之后，而不是在组成预测先行的字符后。
(?!pattern)|执行反向预测先行搜索的子表达式，该表达式匹配不处于匹配 pattern 的字符串的起始点的搜索字符串。它是一个非捕获匹配，即不能捕获供以后使用的匹配。例如，'Windows (?!95|98|NT|2000)' 匹配"Windows 3.1"中的 "Windows"，但不匹配"Windows 2000"中的"Windows"。预测先行不占用字符，即发生匹配后，下一匹配的搜索紧随上一匹配之后，而不是在组成预测先行的字符后。
x\|y|匹配 x 或 y。例如，'z|food' 匹配"z"或"food"。'(z|f)ood' 匹配"zood"或"food"。
\[xyz]|字符集。匹配包含的任一字符。例如，"[abc]"匹配"plain"中的"a"。
\[^xyz]|反向字符集。匹配未包含的任何字符。例如，"[^abc]"匹配"plain"中"p"，"l"，"i"，"n"。
\[a-z]|字符范围。匹配指定范围内的任何字符。例如，"[a-z]"匹配"a"到"z"范围内的任何小写字母。
\[^a-z]|反向范围字符。匹配不在指定的范围内的任何字符。例如，"[^a-z]"匹配任何不在"a"到"z"范围内的任何字符。
\b|匹配一个字边界，即字与空格间的位置。例如，"er\b"匹配"never"中的"er"，但不匹配"verb"中的"er"。
\B|非字边界匹配。"er\B"匹配"verb"中的"er"，但不匹配"never"中的"er"。
\cx|匹配 x 指示的控制字符。例如，\cM 匹配 Control-M 或回车符。x 的值必须在 A-Z 或 a-z 之间。如果不是这样，则假定 c 就是"c"字符本身。
\d|数字字符匹配。等效于 [0-9]。
\D|非数字字符匹配。等效于 [^0-9]。
\f|换页符匹配。等效于 \x0c 和 \cL。
\n|换行符匹配。等效于 \x0a 和 \cJ。
\r|匹配一个回车符。等效于 \x0d 和 \cM。
\s|匹配任何空白字符，包括空格、制表符、换页符等。与 [ \f\n\r\t\v] 等效。
\S|匹配任何非空白字符。与 [^ \f\n\r\t\v] 等效。
\t|制表符匹配。与 \x09 和 \cI 等效。
\v|垂直制表符匹配。与 \x0b 和 \cK 等效。
\w|匹配任何字类字符，包括下划线。与"[A-Za-z0-9_]"等效。
\W|与任何非单词字符匹配。与"[^A-Za-z0-9_]"等效。
\xn|匹配 n，此处的 n 是一个十六进制转义码。十六进制转义码必须正好是两位数长。例如，"\x41"匹配"A"。"\x041"与"\x04"&"1"等效。允许在正则表达式中使用 ASCII 代码。
\num|匹配 num，此处的 num 是一个正整数。到捕获匹配的反向引用。例如，"(.)\1"匹配两个连续的相同字符。
\n|标识一个八进制转义码或反向引用。如果 \n 前面至少有 n 个捕获子表达式，那么 n 是反向引用。否则，如果 n 是八进制数 (0-7)，那么 n 是八进制转义码。
\nm|标识一个八进制转义码或反向引用。如果 \nm 前面至少有 nm 个捕获子表达式，那么 nm 是反向引用。如果 \nm 前面至少有 n 个捕获，则 n 是反向引用，后面跟有字符 m。如果两种前面的情况都不存在，则 \nm 匹配八进制值 nm，其中 n 和 m 是八进制数字 (0-7)。
\nml|当 n 是八进制数 (0-3)，m 和 l 是八进制数 (0-7) 时，匹配八进制转义码 nml。
\un|匹配 n，其中 n 是以四位十六进制数表示的 Unicode 字符。例如，\u00A9 匹配版权符号 (©)。

### 一个例子
下面给出一个匹配QQ号的例子
```Java
public class regex {
    public static void main(String[] args) {
            checkQQ2("0123134");
    }
    public static void checkQQ2(String qq) {
            String reg = "[1-9][0-9]{4,14}";
            System.out.println(qq.matches(reg)?"合法qq":"非法qq");
    }
}
```
使用正则能够极大的简化对字符传处理的操作。

## 小结
大体内容就这样了，更多详细内容可以多多参考Java 源码和官方手册。
