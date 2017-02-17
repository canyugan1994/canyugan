package com.canyugan.async;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

/**
 * 事故发生的现场
 * -------------
 * |    |   |  |
 * -------------
 * @author caorui
 *
 */
public class EventModel {
    private EventType eventType;
    private int actorId;//触发者id
    private int entityType;//触发类型
    private int entityId;//触发载体id
    private int entityOwnerId;//触发载体所属id
    
    private Map<String,String> exts = new HashedMap();

    
	public EventModel() {
		
	}
	
public EventModel(EventType eventType) {
	this.eventType = eventType;
	}

	public EventModel(int entityType) {
		this.entityType = entityType;
	}

	public EventType getEventType() {
		return eventType;
	}

	public EventModel setEventType(EventType eventType) {
		this.eventType = eventType;
		return this;
	}

	public int getActorId() {
		return actorId;
	}

	public EventModel setActorId(int actorId) {
		this.actorId = actorId;
		return this;
	}

	public int getEntityType() {
		return entityType;
	}

	public EventModel setEntityType(int entityType) {
		this.entityType = entityType;
		return this;
	}

	public int getEntityId() {
		return entityId;
	}

	public EventModel setEntityId(int entityId) {
		this.entityId = entityId;
		return this;
	}

	public int getEntityOwnerId() {
		return entityOwnerId;
	}

	public EventModel setEntityOwnerId(int entityOwnerId) {
		this.entityOwnerId = entityOwnerId;
		return this;
	}

	public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }
	
	public String getExt(String key) {
        return exts.get(key);
    }
	
	public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
