package com.common.utils.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU 缓存简单实现，基于HashMap
 * 
 * @author kevin(ssp0xd7 @ gmail.com) 30/03/2018
 */
public class LRUCache<K, V> {

    /**
     * 默认加载因子
     */
    private static final float factor = 0.75F;

    private Map<K, V> map;

    private int cacheSize;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        int capacity = (int) Math.ceil((double) ((float) cacheSize / factor)) + 1;
        this.map = new LinkedHashMap<K, V>(capacity, 0.75F, true) {
            private static final long serialVersionUID = -6265505777659103537L;

            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return usedEntries() > LRUCache.this.cacheSize;
            }
        };
    }

    // TODO: 30/03/2018  synchronized -> ReentrantLock
    public synchronized V get(K key) {
        return this.map.get(key);
    }

    public synchronized void put(K key, V value) {
        this.map.put(key, value);
    }

    public synchronized void remove(K key) {
        this.map.remove(key);
    }

    public synchronized void clear() {
        this.map.clear();
    }

    public synchronized int usedEntries() {
        return this.map.size();
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection<Map.Entry<K, V>> getAll() {
        return new ArrayList(this.map.entrySet());
    }

    public interface CacheResizeListener<K, V> {
        void onEntryToDel(Map.Entry<K, V> var1);
    }
}
