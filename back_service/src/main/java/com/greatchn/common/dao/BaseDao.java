package com.greatchn.common.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.*;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;

/**
 * 将list<Object[]>转化为transformToMap
 *
 * @author zy 2018-9-11
 */
@Repository
public class BaseDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 将list<Object[]>转化为transformToMap
     *
     * @param list   要转化的list
     * @param keyStr map中的key,以逗号隔开
     * @author zy 2018-9-11
     */
    public static List<Map<String, Object>> transformToMap(List<Object[]> list, String keyStr) {
        List<Map<String, Object>> listMap = null;
        // 判断传入数据是否为空
        if (list != null && list.size() > 0) {
            listMap = new ArrayList<>();
            //判断keys是否为空
            if (StringUtils.isNotBlank(keyStr)) {
                String[] keys = keyStr.split(",");
                // 将key对应数据放入map
                for (Object[] object : list) {
                    if (object != null) {
                        // 判断要转换的信息数量与传入的key无法对应
                        if (object.length == keys.length && keys.length > 0) {
                            Map<String, Object> map = new HashMap<>(16);
                            for (int j = 0; j < object.length; j++) {
                                map.put(keys[j], object[j]);
                            }
                            listMap.add(map);
                        }
                    }
                }
            }
        }
        return listMap;
    }

    /**
     * 根据主键查询对应的PO对象(get方式)
     *
     * @param clazz 查询对象的类的类型
     * @param id    主键id
     */
    public <T> T get(Class<T> clazz, Serializable id) {
        Session session = (Session) entityManager.getDelegate();
        return session.get(clazz, id);
    }

    /**
     * 根据主键查询对应的PO对象(load方式)
     *
     * @param clazz 查询对象的类的类型
     * @param id    主键id
     */
    public <T> T load(Class<T> clazz, Serializable id) {
        Session session = (Session) entityManager.getDelegate();
        return session.load(clazz, id);

    }

    /**
     * <p>
     * 新增
     * </p>
     *
     * @param pojo 要新增的数据
     */
    public void save(Object... pojo) {
        Session session = (Session) entityManager.getDelegate();
        for (Object po : pojo) {
            if (po != null) {
                session.save(po);
            }
        }
        session.flush();

    }

    /**
     * <p>
     * 修改
     * </p>
     *
     * @param pojo 要修改的数据
     */
    public void update(Object... pojo) {
        Session session = (Session) entityManager.getDelegate();
        for (Object po : pojo) {
            if (po != null) {
                session.update(po);
            }
        }
    }

    /**
     * <p>
     * 保存或修改
     * </p>
     *
     * @param pojo 要保存或修改的数据
     */
    public void saveOrUpdate(Object... pojo) {
        Session session = (Session) entityManager.getDelegate();
        for (Object po : pojo) {
            if (po != null) {
                session.saveOrUpdate(po);
            }
        }
    }

    /**
     * <p>
     * 删除 <span style='color: red'>危险操作，不建议使用，除特殊数据外应该采用更新作废标记的方式</span>
     * </p>
     *
     * @param pojo 要移除的对象
     */
    public void remove(Object... pojo) {
        Session session = (Session) entityManager.getDelegate();
        for (Object po : pojo) {
            session.delete(po);
        }
    }


    /**
     * <p>
     * 检索唯一符合条件的数据
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public <T> T unique(String hql, Object... param) {
        T result;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
        result = (T) query.uniqueResult();
        return result;
    }


    /**
     * <p>
     * 根据HQL进行查询，并将查询结果转换为数组
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public <T> T[] query(String hql, Class<T> clazz, Object... param) {
        T[] result = null;
        List<T> list = query(hql, param);
        if (list != null) {
            result = (T[]) Array.newInstance(clazz, list.size());
            result = list.toArray(result);
        }
        return result;
    }

    /**
     * <p>
     * 根据HQL进行查询
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param param 不定参
     */
    public <T> List<T> query(String hql, Object... param) {
        return query(hql, 0, 0, param);
    }

    /**
     * <p>
     * 根据条件分页查询
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param page  页数
     * @param rows  每页行数
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String hql, int page, int rows, Object... param) {
        List<T> result;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
        if (page > 0) {
            query.setFirstResult((page - 1) * rows);
        }
        if (rows > 0) {
            query.setMaxResults(rows);
        }
        result = query.list();
        return result;
    }

    /**
     * <p>
     * 根据丰富类型参数查询结果
     * </p>
     *
     * @param hql  要查询的hql字符串
     * @param maps 参数
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String hql, Map<String, Object> maps) {
        List<T> result;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (String key : maps.keySet()) {
            Object value = maps.get(key);
            if (value instanceof Collection<?>) {
                query.setParameterList(key, (Collection<?>) value);
            } else if (value instanceof Object[]) {
                query.setParameterList(key, (Object[]) value);
            } else {
                query.setParameter(key, value);
            }
        }
        result = query.list();
        return result;
    }

    /**
     * <p>
     * 需要的结果为部分条件，根据HQL进行查询，并将结果转换为MAP
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param keys  map的key，必传
     * @param param 不定参
     */
    public List<Map<String, Object>> queryToMap(String hql, String keys, Object... param) {
        return queryToMap(hql, 0, 0, keys, param);
    }

    /**
     * <p>
     * 需要的结果为部分条件，根据HQL条件分页查询，并将结果转换为MAP
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param page  页数
     * @param rows  每页行数
     * @param keys  map中的key,以逗号隔开
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryToMap(String hql, int page, int rows, String keys, Object... param) {
        List<Object[]> result;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
        if (page > 0) {
            query.setFirstResult((page - 1) * rows);
        }
        if (rows > 0) {
            query.setMaxResults(rows);
        }
        result = query.list();
        return transformToMap(result, keys);
    }

    /**
     * <p>
     * 根据HQL进行查询，直接返回查询结果
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param param 不定参
     */
    public List<Object[]> queryObject(String hql, Object... param) {
        return queryObject(hql, 0, 0, param);
    }

    /**
     * <p>
     * 根据条件分页查询，直接返回查询结果
     * </p>
     *
     * @param hql   查询HQL字符串
     * @param page  页数
     * @param rows  每页行数
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> queryObject(String hql, int page, int rows, Object... param) {
        List<Object[]> result;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
        if (page > 0) {
            query.setFirstResult((page - 1) * rows);
        }
        if (rows > 0) {
            query.setMaxResults(rows);
        }
        result = query.list();
        return result;
    }

    /**
     * <p>
     * 执行HQL语句方法
     * </p>
     *
     * @param hql 要执行的HQL字符串
     */
    public int execute(String hql, Object... param) {
        int count;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < param.length; i++) {
            query.setParameter(i, param[i]);
        }
        count = query.executeUpdate();
        return count;
    }

    /**
     * 根据HQL语句来获取对应记录数
     *
     * @param hql    要查询的hql字符串
     * @param params 不定参
     */
    public long count(String hql, Object... params) {
        long count;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i, params[i]);
        }
        count = (Long) query.uniqueResult();
        return count;
    }

    /**
     * <p>
     * 执行HQL语句，批量参数
     * </p>
     *
     * @param hql    hql语句
     * @param params 不定参
     */
    public int executeBatch(String hql, Object[]... params) {
        int count = 0;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createQuery(hql);
        for (Object[] param : params) {
            for (int i = 0; i < param.length; i++) {
                query.setParameter(i, param[i]);
            }
            count += query.executeUpdate();
        }
        return count;
    }

    /**
     * <p>
     * 执行HQL语句，批量参数
     * </p>
     *
     * @param hql    hql语句
     * @param params 不定参
     */
    public int executeBatch(String hql, List<Object[]> params) {
        Object[][] paramArray = new Object[params.size()][];
        paramArray = params.toArray(paramArray);
        return executeBatch(hql, paramArray);
    }

    /**
     * <p>
     * 执行SQL语句方法
     * </p>
     *
     * @param sql   要执行的SQL字符串
     * @param param 参数
     */
    public int executeSQL(String sql, Object... param) {
        int count;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createNativeQuery(sql);
        for (int i = 1; i < param.length + 1; i++) {
            query.setParameter(i, param[i - 1]);
        }
        count = query.executeUpdate();
        return count;
    }

    /**
     * <p>
     * 执行SQL语句，批量参数
     * </p>
     *
     * @param sql    查询的sql字符串
     * @param params 参数
     */
    public int executeSQLBatch(String sql, Object[]... params) {
        int count = 0;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createNativeQuery(sql);
        for (Object[] param : params) {
            for (int i = 1; i < param.length + 1; i++) {
                query.setParameter(i, param[i - 1]);
            }
            count = query.executeUpdate();
        }
        return count;
    }

    /**
     * <p>
     * 执行SQL语句，批量参数
     * </p>
     *
     * @param sql    查询的sql字符串
     * @param params 参数
     */
    public int executeSQLBatch(String sql, List<Object[]> params) {
        Object[][] paramArray = new Object[params.size()][];
        paramArray = params.toArray(paramArray);
        return executeSQLBatch(sql, paramArray);
    }


    /**
     * 通过SQL进行数据查询
     *
     * @param sql    查询的sql的字符串
     * @param types  查询结果的类型
     * @param params 参数
     */
    public List<Map<String, Object>> queryBySQL(String sql, Map<String, Type> types, Object... params) {
        return queryBySQL(sql, types, 0, 0, params);
    }

    /**
     * 通过SQL进行数据查询
     *
     * @param sql    查询的sql字符串
     * @param types  查询结果的类型
     * @param page   查询页数
     * @param rows   每页记录数
     * @param params 其他参数
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryBySQL(String sql, Map<String, Type> types, int page, int rows, Object... params) {
        List<Map<String, Object>> result;
        Session session = (Session) entityManager.getDelegate();
        NativeQuery query = session.createNativeQuery(sql);
        //设置查询结果集为List<Map>
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (String key : types.keySet()) {
            Type type = types.get(key);
            query.addScalar(key, type);
        }
        for (int i = 1; i < params.length + 1; i++) {
            query.setParameter(i, params[i - 1]);
        }
        if (page > 0) {
            query.setFirstResult((page - 1) * rows);
        }
        if (rows > 0) {
            query.setMaxResults(rows);
        }
        result = query.list();
        return result;
    }

    /**
     * 清空Hibernate缓存
     */
    public void clear() {
        Session session = (Session) entityManager.getDelegate();
        session.clear();
    }

    /**
     * 从Hibernate缓存中移除指定对象
     *
     * @param po 要从缓存中移除的对象
     */
    public void evict(Object po) {
        Session session = (Session) entityManager.getDelegate();
        session.evict(po);
    }

    /**
     * 获取in操作累加
     *
     * @param size 累加占位符数量
     */
    public String getInReplacement(int size) {
        String[] array = new String[size];
        return StringUtils.join(array, "?,") + "?";
    }

    /**
     * 通过Sql语句查询，有唯一结果
     *
     * @param sql   查询sql的字符串
     * @param types 查询
     * @param param 不定参
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> uniqueBySQL(String sql, Map<String, Type> types, Object... param) {
        Map<String, Object> result;
        Session session = (Session) entityManager.getDelegate();
        NativeQuery query = session.createNativeQuery(sql);
        for (int i = 1; i < param.length + 1; i++) {
            query.setParameter(i, param[i - 1]);
        }
        for (String key : types.keySet()) {
            Type type = types.get(key);
            query.addScalar(key, type);
        }
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        result = (Map<String, Object>) query.uniqueResult();
        return result;
    }


    /**
     * 根据SQL语句来获取对应记录数
     *
     * @param sql    要查询的sql字符串
     * @param params 不定参
     */
    public BigInteger countBySql(String sql, Object... params) {
        BigInteger count;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createNativeQuery(sql);
        for (int i = 1; i < params.length + 1; i++) {
            query.setParameter(i, params[i - 1]);
        }
        count = (BigInteger) query.uniqueResult();
        return count;
    }

    /**
     * <p>
     * 将sql查询的结果转换成对象结合,只使用简单的select a.* from a ....
     * </p>
     *
     * @param sql    要查询的sql的字符串
     * @param clazz  要转换的对象的类的类型
     * @param params 不定参
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryBySql(final String sql, Class<T> clazz, Object... params) {
        List<T> list;
        Session session = (Session) entityManager.getDelegate();
        NativeQuery query = session.createNativeQuery(sql);
        for (int i = 1; i < params.length + 1; i++) {
            query.setParameter(i, params[i - 1]);
        }
        list = query.addEntity(clazz).list();
        return list;
    }

    /**
     * <p>
     * 将sql查询的结果转换成对象结合,分页显示,只使用简单的select a.* from a ....,
     * </p>
     *
     * @param sql    要查询的sql的字符串
     * @param clazz  要转换的对象的类的类型
     * @param page   当前页
     * @param rows   每页记录数
     * @param params 不定参
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryBySql(final String sql, Class<T> clazz, int page, int rows, Object... params) {
        List<T> list;
        Session session = (Session) entityManager.getDelegate();
        NativeQuery query = session.createNativeQuery(sql);
        for (int i = 1; i < params.length + 1; i++) {
            query.setParameter(i, params[i - 1]);
        }
        if (page > 0) {
            query.setFirstResult((page - 1) * rows);
        }
        if (rows > 0) {
            query.setMaxResults(rows);
        }
        list = query.addEntity(clazz).list();
        return list;
    }

    /**
     * <p>
     * 通过sql进行查询，并返回ScrollableResult
     * </p>
     *
     * @param sql   查询HQL字符串
     * @param param 查询条件
     */
    public ScrollableResults findForScrollableResults(String sql, Object... param) {
        ScrollableResults srs;
        Session session = (Session) entityManager.getDelegate();
        Query query = session.createNativeQuery(sql);
        for (int i = 1; i < param.length + 1; i++) {
            query.setParameter(i, param[i]);
        }
        srs = query.scroll();
        return srs;
    }
}



