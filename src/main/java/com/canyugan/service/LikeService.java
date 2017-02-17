package com.canyugan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.canyugan.util.JedisAdapter;
import com.canyugan.util.RedisKeyUtil;

@Service
public class LikeService 
{
    @Autowired
    JedisAdapter jedisAdapter;


    //某个实体的点赞数量
    public long getLikeCount(int entityType, int entityId) {
    	String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
    	return jedisAdapter.scard(likeKey);
    }

    //当前登录人是否对其点赞
    public int getLikeStatus(int userId, int entityType, int entityId) {
    	String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
    	//喜欢直接返回1
    	if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
    		return 1;
    	}
    	
    	String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
    	//不喜欢返回-1  无状态返回0
    	return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))?-1:0;
    }

    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        
        //点赞可能其他人也点赞了所以需要去查询数量
        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId) {
    	String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        //取消点赞可能其他人可能也操作了所以需要去查询数量
        return jedisAdapter.scard(likeKey);
    }
}
