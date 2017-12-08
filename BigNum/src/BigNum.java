/**想法
 * 将大数在进行表示时使用字符串进行表示，计算时转化成数组，进行计算。
 */

public class BigNum {
    private String value;

    //获取值
    public String getValue() {
        return value;
    }

    //设置大数值
    public void setValue(String val) {
        value = val;
    }

    //将大数字符串转化成字符数组
    public char[] toArray() {
        char[] tArray = new StringBuffer(value).reverse().toString().toCharArray();
        return tArray;
    }
}
