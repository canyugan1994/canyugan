package com.canyugan.async;

import com.alibaba.fastjson.JSONObject;
import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送事件的入口
 * 订阅发布模式的发布端
 * @author caorui
 *
 */
@Service
public class EventProducer 
{
    @Autowired
    JedisAdapter jedisAdapter;

    //把事件发送到订阅通道
    public boolean fireEvent(EventModel eventModel) 
    {
    	/*
    	 * 订阅和发布之间的通道可以采用redis的list实现
    	 * 利用list的brop方法阻塞(没有元素就阻塞)读取最后一个元素  
    	 */
        try {
        	String json = JSONObject.toJSONString(eventModel);
        	String key = RedisKeyUtil.getEventQueueKey();
        	jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
