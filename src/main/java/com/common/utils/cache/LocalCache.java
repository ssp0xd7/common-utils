package com.common.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 具备有效期的本地缓存<br>
 * 可以注入其他的dao或者service等
 *
 * @author kevin(ssp0xd7@gmail.com) 2017/7/13
 */
public abstract class LocalCache {

    /**
     * 保存键值对
     */
    private Map<String, Object> objectMap = new ConcurrentHashMap<String, Object>();

    /**
     * 记录当前缓存中数据的更新时间
     */
    private Map<String, AtomicLong> refreshTimeMap = new ConcurrentHashMap<String, AtomicLong>();

    /**
     * 缓存刷新时间间隔，秒为单位<br>
     * 默认一小时
     */
    private long refreshInterval = 3600 * 1000;

    /**
     * 初始化某个可以的缓存数据
     *
     * @param key
     */
    protected abstract void init(String key);

    /**
     * 通过key获取缓存value
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        AtomicLong refreshTime = refreshTimeMap.get(key);
        if (refreshTime == null) {
            init(key);
        } else if (System.currentTimeMillis() / this.refreshInterval > refreshTime.get() / this.refreshInterval) {
            //避免其他进程进入init方法
            refreshTime.set(System.currentTimeMillis());
            init(key);
        }
        return objectMap.get(key);
    }

    /**
     * 通过key获取缓存value，不检查过期
     *
     * @param key
     * @return
     */
    public Object getWithoutRefresh(String key) {
        return objectMap.get(key);
    }

    /**
     * 手动设置缓存键值对
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        refreshTimeMap.put(key, new AtomicLong(System.currentTimeMillis()));
        objectMap.put(key, value);
    }

    /**
     * 刷新某个key的value
     *
     * @param key
     */
    public void refresh(String key) {
        init(key);
    }

    /**
     * 获取缓存刷新时间间隔
     *
     * @return
     */
    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * 设置缓存刷新时间间隔，秒为单位
     *
     * @param refreshInterval
     */
    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval * 1000;
    }

    /**
     * 释放内存
     */
    public void clear() {
        objectMap.clear();
        refreshTimeMap.clear();
    }
}
