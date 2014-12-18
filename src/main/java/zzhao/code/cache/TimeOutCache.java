/*
 * @(#) TimeOutCache.java 2013-11-20
 * 
 * Copyright 2013 NetEase.com, Inc. All rights reserved.
 */
package zzhao.code.cache;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.TreeMultiset;

/**
 * 有超时特性的缓存，在写入或者访问后的一定时间失效
 * 这个东西只能在一个线程里面用，暂时不搞线程安全, 缓存失效在同一个线程里面完成
 * 不支持Integer和Long,因为那2个在 map.remove 里面有问题啊
 * 
 * @author zzhao
 * @version 2013-11-20
 */
public class TimeOutCache<T extends Comparable<T>> {
    private Map<T, Long> cache;
    private TreeMultiset<TimeOutObj> outList;
    private long timeOut = 0L;

    public TimeOutCache(long timeOut, int capicaty){
        this.timeOut = timeOut;
        this.cache = new ConcurrentHashMap<T, Long>(capicaty);
        outList = TreeMultiset.create(new Comparator<TimeOutObj>() {
            @Override
            public int compare(TimeOutObj o1, TimeOutObj o2) {
                // 先按时间排序,再按key值排序
                long x = o1.time;
                long y = o2.time;
                if(x < y){
                    return -1;
                } else if (x > y) {
                    return 1;
                }else{
                    return o1.key.compareTo(o2.key);
                }
            }
        });
    }
    
    /**
     * 检查是否还有对应的值,如果有则返回true，否则false
     * @param value
     * @return
     */
    public boolean checkValue(T value) {
        boolean result = false;
        long curr = System.currentTimeMillis();
        dotimeOut(curr);
        Long timeOut = cache.get(value);
        if (timeOut != null) {
            result = true;
        } else {
            TimeOutObj obj = new TimeOutObj();
            obj.key = value;
            obj.time = curr + this.timeOut;
            outList.add(obj);
        }
        cache.put(value, curr + this.timeOut); // 设置过期时间
        return result;
    }
    
    private void dotimeOut(long curr) {
        Entry<TimeOutObj> entry= outList.firstEntry();
        if(entry != null){
            TimeOutObj obj = entry.getElement();
            while (obj.time <= curr) {
                outList.pollFirstEntry();
                Long timeOut = cache.get(obj.key);
                if (timeOut != null && timeOut > curr) {
                    TimeOutObj tmp = new TimeOutObj();
                    tmp.key = obj.key;
                    tmp.time = timeOut;
                    outList.add(tmp);
                } else {
                    cache.remove(obj.key);
                }
                entry = outList.firstEntry();
                if (entry == null) {
                    break;
                }else{
                    obj = entry.getElement();
                }
            }
        }
    }
    
    private class TimeOutObj {
        private T key;
        private long time;

        @Override
        public String toString() {
            return key.toString() + ":" + time;
        }
    }
}
