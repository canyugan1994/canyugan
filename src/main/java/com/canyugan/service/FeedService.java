package com.canyugan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.canyugan.dao.FeedDAO;
import com.canyugan.model.Feed;

@Service
public class FeedService 
{
	@Autowired
	private FeedDAO feedDAO;
	
	
	public List<Feed> getUserFeeds(int maxId,List<Integer> userIds,int count)
	{
		return feedDAO.selectUserFeeds(maxId, userIds, count);
	}
	
	public boolean addFeed(Feed feed)
	{
		feedDAO.addFeed(feed);
		//已结持久化  自增id已有
		return feed.getId() > 0;
	}
	
	public Feed getById(int id)
	{
		return feedDAO.getFeedById(id);
	}
}
