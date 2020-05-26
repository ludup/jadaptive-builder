package com.jadaptive.app.auth.quota;

import java.util.Date;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name = "Quota", resourceKey = Quota.RESOURCE_KEY, 
	hidden = true, system = true, scope = ObjectScope.GLOBAL, type = ObjectType.COLLECTION)
public class Quota extends AbstractUUIDEntity {

	public static final String RESOURCE_KEY = "quota";
	
	@ObjectField(name = "Group", description = "A group to identify the quote type", type = FieldType.TEXT)
	String group;
	
	@ObjectField(name = "Key", description = "An individual quote within the group", type = FieldType.TEXT)
	String key;
	
	@ObjectField(name = "Quota", description = "The maximum available value of this quota", type = FieldType.LONG)
	long quota;
	
	@ObjectField(name = "Period", description = "The period of time in seconds that the quote applies", type = FieldType.LONG)
	long period;
	
	@ObjectField(name = "Value", description = "The current value of this quota", type = FieldType.LONG)
	long value;

	Date quotaStarted;
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public Date getQuotaStarted() {
		return quotaStarted;
	}

	public void setQuotaStarted(Date quotaStarted) {
		this.quotaStarted = quotaStarted;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
