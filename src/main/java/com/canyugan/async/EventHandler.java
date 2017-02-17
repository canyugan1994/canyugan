package com.canyugan.async;

import java.util.List;
import com.canyugan.async.EventType;
/**
 * 事件处理的抽象接口
 * @author caorui
 *
 */
public interface EventHandler 
{
	//做
    void doHandle(EventModel model);
    //订阅的类型
    List<EventType> getSupportEventTypes();
}
