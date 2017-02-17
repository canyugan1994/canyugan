package com.canyugan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

import redis.clients.jedis.Transaction;

@Service
public class FollowService 
{
	@Autowired
    JedisAdapter jedisAdapter;
	
	public boolean follow(int userId,int entityType, int entityId) {
    	//关注对象加上操作对象id
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		//操作对象关注列表加上关注对象
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		
		Date date = new Date();
		Jedis jedis = jedisAdapter.getJedis();
		Transaction transaction = jedisAdapter.multi(jedis);
		transaction.zadd(followerKey, date.getTime(),String.valueOf(userId));
		transaction.zadd(followeeKey, date.getTime(),String.valueOf(entityId));
		List<Object> ret  = transaction.exec();
		return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }
	
	public boolean unfollow(int userId,int entityType, int entityId) {
    	//关注对象加上操作对象id
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		//操作对象关注列表加上关注对象
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		
		Jedis jedis = jedisAdapter.getJedis();
		Transaction transaction = jedisAdapter.multi(jedis);
		transaction.zrem(followerKey, String.valueOf(userId));
		transaction.zrem(followeeKey, String.valueOf(entityId));
		List<Object> ret  = transaction.exec();
		return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }
	
	private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<Integer>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
	
	//获取实体所有的粉丝id
	public List<Integer> getFollowers(int entityType, int entityId, int count) {
    	//关注对象加上操作对象id
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0,count));
    }
	
	//分页获取粉丝id
	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset+count));
    }
	
	//获取当前用户关注的实体id
	public List<Integer> getFollowees(int userId,int entityType,int count)
	{
		//操作对象关注列表加上关注对象
		String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
	}
	
	//分页获取关注的实体id
	public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset+count));
    }
	
	 public long getFollowerCount(int entityType, int entityId) {
	    String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
	    return jedisAdapter.zcard(followerKey);
	}

	public long getFolloweeCount(int userId, int entityType) {
	    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
	    return jedisAdapter.zcard(followeeKey);
	}
	
	/*
	 * 判断用户是否关注了某个实体
	 */
	public boolean isFollower(int userId,int entityType, int entityId){
		//关注对象加上操作对象id
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zscore(followerKey, String.valueOf(userId))!=null;
	}
}
