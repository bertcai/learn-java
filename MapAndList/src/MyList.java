
import java.util.*;


//using AbstractCollection is aim at using the toString()
public class MyList<E> extends AbstractCollection<E> implements List<E> {
    int size;
    E[] elementData;

    MyList() {

    }

    MyList(E[] elementData) {
        this.elementData = elementData;
        this.size = elementData.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        if (size == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public class Itr implements Iterator {
        int cursor = 0;
        int lastRet = -1;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public Object next() {
            Object[] elementData = MyList.this.elementData;
            int i = cursor;
            if (i < size) {
                cursor = i + 1;
                lastRet = i;
            }
            return elementData[lastRet];
        }
    }

    @Override
    public Iterator iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        if (size == 0)
            return null;
        return Arrays.copyOf(elementData, size);
    }

//    public String toString(){
//        String s = "";
//        for(int i = 0;i<size;i++){
//            s=s+elementData[i];
//            if(i!=size-1)
//                s+=" ";
//        }
//        return s;
//    }

    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    private String outOfBoundsMsg(int var1) {
        return "Index: " + var1 + ", Size: " + this.size;
    }


    private void rangeCheck(int var1) {
        if (var1 < 0 || var1 >= this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
        }
    }

    private void rangeCheckAdd(int var1) {
        if (var1 < 0 || var1 > this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
        }
    }


    @Override
    public E get(int index) {
        rangeCheck(index);
        return elementData[index];
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(this.size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckAdd(index);
        int cSize = c.size();
        E[] newElementData;
        Object[] a = c.toArray();
//        newElementData = Arrays.copyOf(elementData, index + cSize);
        newElementData = (E[])(new Object[index + cSize]);
        System.arraycopy(elementData, 0, newElementData, 0, size);
        elementData = newElementData;
        if (cSize == 0)
            return false;
        System.arraycopy(a, 0, elementData, index, cSize);
        size = index + cSize;
        return true;
    }

    @Override
    public boolean add(E e) {
        E[] newElementData;

        newElementData = (E[])(new Object[size+1]);
        System.arraycopy(elementData, 0, newElementData, 0, size);
        elementData = newElementData;
        elementData[size++] = e;
        return true;
    }

    @Override
    public void add(int index, E element) {
        rangeCheck(index);
        E[] newElementData;
        newElementData = Arrays.copyOf(elementData, size + 1);
        elementData = (E[]) newElementData;
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        size = size + 1;
        elementData[index] = element;
    }

    public void subListRangeCheck(int var0, int var1, int var2) {
        if (var0 < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + var0);
        } else if (var1 > var2) {
            throw new IndexOutOfBoundsException("toIndex = " + var1);
        } else if (var0 > var1) {
            throw new IllegalArgumentException("fromIndex(" + var0 + ") > toIndex(" + var1 + ")");
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex,toIndex,size);
        Object[] newElementData = new Object[toIndex - fromIndex];
        int temp = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            newElementData[temp] = this.elementData[i];
            temp++;
        }
        return new MyList((E[])newElementData);
    }

    @Override
    public E set(int index, E element) {
        rangeCheck(index);
        E oldElem = elementData[index];
        this.elementData[index] = element;
        return oldElem;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        rangeCheck(index);
        return new ListIterator<E>() {
            int cursor = index;

            @Override
            public E previous() {
                cursor--;
                return elementData[cursor];
            }

            @Override
            public E next() {
                int old = cursor;
                cursor++;
                return elementData[old];
            }

            @Override
            public boolean hasNext() {
                if (cursor < size)
                    return true;
                else
                    return false;
            }

            @Override
            public boolean hasPrevious() {
                if (cursor > 0)
                    return true;
                else
                    return false;
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                int temp = cursor - 1;
                return temp;
            }

            @Override
            public void add(E e) {
                MyList.this.add(cursor, e);
            }

            @Override
            public void set(E e) {
                MyList.this.set(cursor - 1, e);
            }

            @Override
            public void remove() {
                MyList.this.remove(elementData[cursor - 1]);
            }
        };
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    @Override
    public E remove(int index) {
        rangeCheck(index);
        E oldValue = elementData[index];
        fastRemove(index);
        return oldValue;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.toArray() == null)
            return false;
        for (Object item : c.toArray()) {
            boolean flag = this.remove(item);
            if (!flag)
                return false;
            size--;
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        for (Object item : this.toArray()) {
            if (c.contains(item))
                continue;
            this.remove(item);
        }
        if (this.size == 0)
            return false;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c.toArray()) {
            if (this.contains(item))
                continue;
            else
                return false;
        }
        return true;
    }

    private void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        elementData[--size] = null;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i] == null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--)
                if (elementData[i] == null)
                    return i;
        } else {
            for (int i = size - 1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }


    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elementData = null;
        }
        this.size = 0;
    }

//    public static void main(String[] args) {
//        Integer a[] = new Integer[0];
//        MyList<Integer> list1 = new MyList<>(a);
//        MyList<Integer> list2 = new MyList<>(a);
//
////        ArrayList<Integer> list3 = new ArrayList<Integer>();
////        ArrayList<Integer> list4 = new ArrayList<Integer>();
//
//        for (int i = 0; i < 5; i++) {
//            list1.add(i);
//            list2.add(i);
////            list3.add(i);
////            list3.add(i);
////            list4.add(i);
//        }
//        System.out.println(list1.toString());
//        System.out.println(list2.toString());
//
//        list1.remove(2);
//        System.out.println(list1.toString());
//        list1.add(2, 2);
//        System.out.println(list1.toString());
//        list1.addAll(list2);
//        System.out.println(list1.toString());
//        Iterator itr = list1.iterator();
//        System.out.println(itr.next());
//        ListIterator itr2 = list1.listIterator();
//        itr2.next();
//        itr2.next();
//        System.out.println(itr2.previous());
//        System.out.println(itr2.nextIndex());
//        System.out.println(itr2.previousIndex());
//        itr2.set(7);
//        System.out.println(list1.toString());
//        System.out.println(list1.lastIndexOf(3));
//        System.out.println(list1.indexOf(3));
//        list1.retainAll(list2);
//        System.out.println(list1.toString());
////        list3.retainAll(list4);
////        System.out.println(list3.toString());
//    }
}
