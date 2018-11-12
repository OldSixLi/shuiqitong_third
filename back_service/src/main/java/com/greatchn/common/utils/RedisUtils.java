package com.greatchn.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * 设置了object缓存序列化方式为json串，可直接放入对象，使用的转化对象为json的工具为阿里的fastJson
 *
 * @author zy 2018-9-17
 * */
@Component
public class RedisUtils {

    @Resource
    RedisTemplate<String, Object> redisTemplate;
    /**
     *  税企通企业版后台项目缓存
     * */
    public static final String REDIS_PREFIX_ENT="ent_";
    /**
     *  税企通税务局版后台项目缓存
     * */
    public static final String REDIS_PREFIX_TAX="tax_";

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 为某个缓存设置过期时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    // ============================String/object=============================

    /**
     * 普通缓存获取
     *
     * @param key
     *            键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     *
     * <p>
     * 普通缓存获取，并转为对象
     * </p>
     *
     * @param key 键
     * @param clazz 要转化类的类型
     * @return 对象
     */
    public <T> T getByString(String key, Class<T> clazz) {
        if (key == null) {
            return null;
        } else {
            String json = (String) redisTemplate.opsForValue().get(key);
            return json != null ? JSONObject.parseObject(json, clazz) : null;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param time
     *            时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
