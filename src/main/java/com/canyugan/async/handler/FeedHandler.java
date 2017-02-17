package com.canyugan.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.canyugan.async.EventHandler;
import com.canyugan.async.EventModel;
import com.canyugan.async.EventType;
import com.canyugan.model.EntityType;
import com.canyugan.model.Feed;
import com.canyugan.model.Question;
import com.canyugan.model.User;
import com.canyugan.service.FeedService;
import com.canyugan.service.FollowService;
import com.canyugan.service.QuestionService;
import com.canyugan.service.UserService;
import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler 
{
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;


	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
	}


	@Override
	public void doHandle(EventModel model) {
		// 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getEventType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null) {
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);
        
        //给事件的粉丝推送
        List<Integer> followers = followService.getFollowers(model.getEntityType(),model.getEntityId(),Integer.MAX_VALUE);
        //0是系统  未登录只能看到系统内容
        followers.add(0);
        for(Integer follower:followers){
        	String timelineKey = RedisKeyUtil.getTimelineKey(follower);
        	jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
	}
	
	private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<String ,String>();
        User actor = userService.getUser(model.getActorId());
        if(actor==null){
        	return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());
        
        //评论或者关注问题时产生一条feed
        if(model.getEventType() == EventType.COMMENT  ||
        		(model.getEventType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)){
        	
        	//把问题信息塞进去
        	Question question = questionService.getById(model.getEntityId());
        	if(question==null){
        		return null;
        	}
        	map.put("questionId",String.valueOf(question.getId()));
        	map.put("questionTitle",question.getTitle());
        	return JSONObject.toJSONString(map);
        }
        return null;
    }
}
