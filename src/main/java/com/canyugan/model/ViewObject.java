package com.canyugan.model;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;


public class ViewObject 
{
	private Map<String,Object> map = new HashedMap();
	
	public Object get(String key)
	{
		return map.get(key);
	}
	public void set(String key,Object value)
	{
		map.put(key, value);
	}
}
