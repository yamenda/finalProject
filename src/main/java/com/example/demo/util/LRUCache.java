package com.example.demo.util;

import java.util.Hashtable;
import java.util.Vector;

public class LRUCache implements Cache {
    protected int capacity;
    protected Vector keys;
    protected Hashtable map;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        keys = new Vector(capacity);
        map = new Hashtable(capacity);
    }

    public synchronized void put(Object key, Object value) {
        remove(key);
        keys.insertElementAt(key, 0);
        map.put(key, value);
        if (keys.size() >= capacity) {
            remove(keys.elementAt(keys.size() - 1));
        }
    }

    public synchronized Object get(Object key) {
        Object value = map.get(key);
        if (value != null) {
            keys.removeElement(key);
            keys.insertElementAt(key, 0);
        }
        return value;
    }

    public synchronized void remove(Object key) {
        if (map.remove(key) != null) {
            keys.removeElement(key);
            --capacity;
        }
    }

    public synchronized void clear() {
        keys.removeAllElements();
        map.clear();
    }

}
