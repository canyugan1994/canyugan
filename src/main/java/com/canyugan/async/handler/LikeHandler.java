package com.canyugan.async.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.canyugan.async.EventHandler;
import com.canyugan.async.EventModel;
import com.canyugan.async.EventType;
import com.canyugan.model.Message;
import com.canyugan.model.User;
import com.canyugan.service.MessageService;
import com.canyugan.service.UserService;
import com.canyugan.util.WendaUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler 
{
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    
    public void doHandle(EventModel model) 
    {
    	//给用户发站内信
    	Message message = new Message();
    	message.setCreatedDate(new Date());
    	message.setFromId(WendaUtil.SYSTEM_USERID);
    	message.setToId(model.getEntityOwnerId());
    	User user = userService.getUser(model.getActorId());
    	message.setContent("用户" + user.getName()
                + "赞了你的评论,http://127.0.0.1:8080/question/" + model.getExt("questionId"));
    	messageService.addMessage(message);
    }

   
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
