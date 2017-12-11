//public class Test {
////    float pi = 3.14f;
////    //默认的小数时double类型的
////    double a = 2.769343;
////    int b = 365;
////    int month = 12;
////    char c = '吃';
////    boolean flag = false;
////    String s = "不可描述";
////    byte b = 2;
////    short s = 128;
////    int i = 65536;
////    long l = 213465L;
////    float f = 3.14f;
////    double d = 3.14159265357;
////    char c = 'c';
////    String str = "you are angle";
//
////    short a = 1;
////    short b = 2;
////    public void main(String[] args) {
////        System.out.println(a + b);
////    }
////    public void test(final int i){
////
////    }
//
//
//
//}

class Animal{
    public String food; //what to eat
    public String location; //where to live

    public void makeNoise(){ //make noise
        System.out.println("noise");
    }
    public void sleep(){
        System.out.println("sleeping");
    }
    public vodi eat(){
        System.out.println("eat");
    }
}

class Dog extends Animal{
    public void makeNoise(){
        System.out.println("Wha,Wha");
    }
}

class Cat extends Animal{
    public void makeNoise(){
        System.out.println("Miao,Miao");
    }
}

class Vet{
    public void giveShout(Animal a){
        a.makeNoise();
    }
}

class PetOwner{
    public void start(){
        Vet v = new Vet();
        Dog d = new Dog();
        Cat c = new Cat();

        v.giveShout(d);
        v.giveShout(c);
    }
}


public class HelloWorld {
    public static void main(String[] args) {
//        Scanner s = new Scanner(System.in);
//        System.out.println("输入月份：");
//        int a = s.nextInt();
//        switch (a){
//            case 1:
//            case 2:
//            case 3:
//                System.out.println("春");
//                break;
//            case 4:
//            case 5:
//            case 6:
//                System.out.println("夏");
//                break;
//            case 7:
//            case 8:
//            case 9:
//                System.out.println("秋");
//                break;
//            case 10:
//            case 11:
//            case 12:
//                System.out.println("冬");
//                break;
//        }
//        System.out.println("读取的数的值是："+a);

//        //黄金分割点
//        System.out.println("开始测试");
//        double a=0,b=0;
//        double temp = 100;
//        for(int i = 1;i<=20;i++){
//            for(int j = i;j<=20;j++){
//                double c = Math.abs((double)i/(double)j - 0.618);
//                if (c < temp) {
//                    temp = c;
//                    a = i;
//                    b = j;
//                }
//            }
//        }
//        temp = a/b;
//        System.out.println(a+" "+b+" "+temp);
        for(int i = 100;i<=999;i++){
            int a = i/100;
            int b = (i%100)/10;
            int c = i%10;
            if(i == (a*a*a + b*b*b + c*c*c))
                System.out.println(i);
        }
    }
}

