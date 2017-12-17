//interface map

import javax.sound.midi.Soundbank;
import java.util.*;

public class MyMap<K, V> implements Map<K, V> {
    int size;
    ArrayList<Node<K, V>> table = new ArrayList<>();

    static class Node<K, V> implements Map.Entry<K, V> {
        K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldvalue = this.value;
            this.value = value;
            return oldvalue;
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return key + " = " + value;
        }
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
    public boolean containsKey(Object key) {
        for (Node<K, V> node : table) {
            if (node.getKey() == key)
                return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Node<K, V> node : table) {
            if (node.getValue() == value)
                return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (table == null)
            return null;
        for (Node<K, V> node : table) {
            if (node.getKey() == key)
                return node.getValue();
        }
        return null;
    }

    @Override
    //put node, return oldvalue if key exist
    public V put(K key, V value) {
        if (table != null) {
            for (Node<K, V> node : table) {
                if (node.getKey() == key) {
                    V oldvalue = node.getValue();
                    node.setValue(value);
                    return oldvalue;
                }
            }
        }
        table.add(new Node(key, value));
        this.size = table.size();
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m.size() > 0) {
            for (Map.Entry<? extends K, ? extends V> mNode : m.entrySet()) {
                K key = mNode.getKey();
                V value = mNode.getValue();
                this.put(key, value);
            }
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Node<K, V> node : table) {
            set.add(node.getKey());
        }
        return set;
    }

    @Override
    public V remove(Object key) {
        for (Node<K, V> node : table) {
            if (node.getKey() == key) {
                table.remove(node);
                this.size = table.size();
                return node.getValue();
            }
        }
        return null;
    }

    @Override
    public Set entrySet() {
        if (table == null)
            return null;
        Set<Node<K, V>> set = new HashSet<>();
        for (Node<K, V> node : table) {
            set.add(node);
        }
        return set;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.table.clear();
    }

    @Override
    public Collection<V> values() {
        Collection<V> col = new ArrayList<>();
        for (Node<K, V> node : table) {
            col.add(node.getValue());
        }
        return col;
    }

    public static void main(String[] args) {
        MyMap<Integer, Integer> map = new MyMap<>();
        MyMap<Integer, Integer> map2 = new MyMap<>();
        for (int i = 0; i < 5; i++) {
            map.put(i, i);
            map2.put(i, i+1);
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
    }
}
