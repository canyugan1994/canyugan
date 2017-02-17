package com.canyugan.async;

import com.alibaba.fastjson.JSONObject;
import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 发送事件的 出口
 * 订阅发布模式的订阅端
 * @author caorui
 *
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware 
{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //一个类型对应有多少订阅者
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    //根据上下文去获取有多少个handler的实现类
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception 
    {
        /*
         * 出口需要做的事情
         * 找出所有的handler实现类
         * 获取每个实现类所对应的支持type
         * 存储在config
         * 开启循环线程去不断处理通道里相关的事件
         */
    	//找出所有的eventHandler实现类
    	Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
    	for(Map.Entry<String,EventHandler> key:beans.entrySet()){
    		//每一个handler实现类所对应的订阅type
    		List<EventType> types = key.getValue().getSupportEventTypes();
    		for(EventType type:types)
    		{
    			if(!config.containsKey(type)){
    				//不存在这个类型  新增
    				config.put(type,new ArrayList<EventHandler>());
    			}
    			//添加handler
    			config.get(type).add(key.getValue());
    		}
    	}
    	
    	Thread thread = new Thread(new Runnable() 
    	{
			@Override
			public void run() 
			{
				while(true)
				{
					String key = RedisKeyUtil.getEventQueueKey();
					List<String> events = jedisAdapter.brpop(0,key);
					
					for(String event:events)
					{
						//brop方式取出的是key和value   此处是为了过滤key
						if(event.equals(key)){
							continue;
						}
						
						//取出事件  redis反序列化
						EventModel eventModel = JSONObject.parseObject(event, EventModel.class);
						if(!config.containsKey(eventModel.getEventType())){
							//取出来的事件类型不在config之内就是没有具体实现的handler类
							logger.error("发现不能识别的事件");
							continue;
						}
						
						//执行type对应的事件
						for(EventHandler handler:config.get(eventModel.getEventType())){
							handler.doHandle(eventModel);
						}
					}
				}
			}
		});
    	thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
