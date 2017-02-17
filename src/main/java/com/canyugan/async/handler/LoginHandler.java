package com.canyugan.async.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.canyugan.async.EventHandler;
import com.canyugan.async.EventModel;
import com.canyugan.async.EventType;
import com.canyugan.util.MailSender;
import com.mysql.fabric.xmlrpc.base.Array;
@Component
public class LoginHandler implements EventHandler 
{
	@Autowired
    MailSender mailSender;
	@Override
	public void doHandle(EventModel model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆", "mails/login_exception.html", map);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		
		return Arrays.asList(EventType.LOGIN);
	}

}
