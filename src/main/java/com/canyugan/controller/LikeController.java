package com.canyugan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.canyugan.async.EventModel;
import com.canyugan.async.EventProducer;
import com.canyugan.async.EventType;
import com.canyugan.model.Comment;
import com.canyugan.model.EntityType;
import com.canyugan.model.HostHolder;
import com.canyugan.service.CommentService;
import com.canyugan.service.LikeService;
import com.canyugan.util.WendaUtil;

@Controller
public class LikeController 
{
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) 
    {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        
        Comment comment = commentService.getCommentById(commentId);
        
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
        .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
        .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
        .setExt("questionId", String.valueOf(comment.getEntityId())));
        
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) 
    {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
