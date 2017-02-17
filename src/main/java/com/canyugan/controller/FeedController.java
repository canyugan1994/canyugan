package com.canyugan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.canyugan.model.EntityType;
import com.canyugan.model.Feed;
import com.canyugan.model.HostHolder;
import com.canyugan.service.FeedService;
import com.canyugan.service.FollowService;
import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class FeedController 
{
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    JedisAdapter jedisAdapter;

    //自动推送  在timeline队列中去取
    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPushFeeds(Model model) 
    {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        //从timeline中取出feed的id
        List<String> feedIds  = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<Feed>();
        for(String id:feedIds){
        	Feed feed = feedService.getById(Integer.parseInt(id));
        	if(feed!=null){
        		feeds.add(feed);
        	}
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    //手动拉取
    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPullFeeds(Model model) 
    {
        int localUserId = hostHolder.getUser() != null?hostHolder.getUser().getId():0;
        List<Integer> followees = new ArrayList<Integer>();
        if(localUserId!=0){
        	//登录状态
        	followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
