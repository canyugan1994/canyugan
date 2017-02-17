package com.canyugan.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.canyugan.async.EventHandler;
import com.canyugan.async.EventModel;
import com.canyugan.async.EventType;
import com.canyugan.model.EntityType;
import com.canyugan.model.Message;
import com.canyugan.model.User;
import com.canyugan.service.MessageService;
import com.canyugan.service.UserService;
import com.canyugan.util.WendaUtil;

public class FollowHandler  implements EventHandler 
{

	@Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

	@Override
	public void doHandle(EventModel model) {
		//给用户发站内信
    	Message message = new Message();
    	message.setCreatedDate(new Date());
    	message.setFromId(WendaUtil.SYSTEM_USERID);
    	message.setToId(model.getEntityOwnerId());
    	User user = userService.getUser(model.getActorId());
    	if(model.getEntityType() == EntityType.ENTITY_QUESTION){
    		message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
    	}else if(model.getEntityType() == EntityType.ENTITY_QUESTION){
    		message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
    	}
    	messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.FOLLOW);
	}

}
