import java.util.Iterator;
import java.util.ListIterator;

public class TestCase {
    public static void main(String[] args) {

        System.out.println("------------------------Map Test------------------------");
        MyMap<Integer, Integer> map = new MyMap<>();
        MyMap<Integer, Integer> map2 = new MyMap<>();
        for (int i = 0; i < 5; i++) {
            map.put(i, i);
            map2.put(i, i + 1);
        }
        System.out.println(map.entrySet().toString());
        System.out.println(map2.entrySet().toString());
        map.putAll(map2);
        System.out.println(map.entrySet().toString());
        System.out.println(map.size());
        System.out.println(map.isEmpty());
        System.out.println(map.keySet().toString());
        System.out.println(map.values().toString());
        System.out.println(map.containsKey(2));
        System.out.println(map.containsValue(2));
        System.out.println(map.get(2));
        System.out.println(map.remove(2));
        System.out.println(map.entrySet().toString());
        map.clear();
        System.out.println(map.entrySet().toString());

        System.out.println("------------------------List Test------------------------");
        Integer a[] = new Integer[0];
        MyList<Integer> list1 = new MyList<>(a);
        MyList<Integer> list2 = new MyList<>(a);

//        ArrayList<Integer> list3 = new ArrayList<Integer>();
//        ArrayList<Integer> list4 = new ArrayList<Integer>();

        for (int i = 0; i < 5; i++) {
            list1.add(i);
            list2.add(i);
//            list3.add(i);
//            list3.add(i);
//            list4.add(i);
        }
        System.out.println(list1.toString());
        System.out.println(list2.toString());

        list1.remove(2);
        System.out.println(list1.toString());
        list1.add(2, 2);
        System.out.println(list1.toString());
        list1.addAll(list2);
        System.out.println(list1.toString());
        Iterator itr = list1.iterator();
        System.out.println(itr.next());
        ListIterator itr2 = list1.listIterator();
        itr2.next();
        itr2.next();
        System.out.println(itr2.previous());
        System.out.println(itr2.nextIndex());
        System.out.println(itr2.previousIndex());
        itr2.set(7);
        System.out.println(list1.toString());
        System.out.println(list1.lastIndexOf(3));
        System.out.println(list1.indexOf(3));
        list1.retainAll(list2);
        System.out.println(list1.toString());
//        list3.retainAll(list4);
//        System.out.println(list3.toString());
    }

}
