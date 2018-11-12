package com.greatchn.bean;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前后台交互数据载体
 * </p>
 * 
 * @author ZLi 2016-11-22
 * 
 */
public class PageData extends HashMap<Object, Object> implements Map<Object, Object> {

	private static final long serialVersionUID = 1L;

	Map<Object, Object> map ;
	HttpServletRequest request;

	public HttpServletRequest getRequest() {
		return request;
	}

	public PageData(HttpServletRequest request) {
		this.request = request;
		Map<String, String[]> properties = request.getParameterMap();
		Map<Object, Object> returnMap = new HashMap<>();
		Iterator<Entry<String, String[]>> entries = properties.entrySet().iterator();
		Entry<String, String[]> entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = entries.next();
			name = entry.getKey();
			String[] valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else {
				for (int i = 0; i < valueObj.length; i++) {
					value = valueObj[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			}
			returnMap.put(name, value);
		}
		map = returnMap;
	}

	public PageData() {
		map = new HashMap<>();
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	public String getString(Object key) {
		if (key != null) {
			return (String) get(key);
		}
		return null;
	}

	public Integer getInteger(Object key) {
		if (key != null) {
			return !key.equals("") ? Integer.valueOf(getString(key)) : null;
		}
		return null;
	}

	public Long getLong(Object key) {
		if (key != null) {
			return Long.valueOf(getString(key));
		}
		return null;
	}

	public boolean getBoolean(Object key) {
		if (key != null) {
			return Boolean.valueOf(getString(key));
		}
		return false;
	}

	public Timestamp getTimestamp(Object key) {
		if (key != null) {
			return Timestamp.valueOf(getString(key));
		}
		return null;
	}

	@Override
	public Object put(Object key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}
	@Override
	public void clear() {
		map.clear();
	}
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	@Override
	public Set<Entry<Object, Object>> entrySet() {
		return map.entrySet();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override
	public Set<Object> keySet() {
		return map.keySet();
	}
	@Override
	public void putAll(Map<? extends Object, ? extends Object> t) {
		map.putAll(t);
	}
	@Override
	public int size() {
		return map.size();
	}
	@Override
	public Collection<Object> values() {
		return map.values();
	}

}
