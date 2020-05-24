package com.jadaptive.api.entity;

import java.util.Collection;
import java.util.Map;

import com.jadaptive.api.repository.UUIDDocument;
import com.jadaptive.api.template.FieldDefinition;

public interface AbstractObject extends UUIDDocument {

	AbstractObject getChild(FieldDefinition c);

	String getResourceKey();

	void setResourceKey(String resourceKey);

	Object getValue(String fieldName);

	Object getValue(FieldDefinition t);

	void setValue(FieldDefinition t, Object value);

	Boolean isSystem();

	String getUuid();

	Map<String, Object> getDocument();

	Boolean isHidden();

	Collection<AbstractObject> getObjectCollection(String resourceKey);

	Collection<String> getCollection(String resourceKey);

	void addChild(AbstractObject e);

	void setSystem(Boolean val);

	void setHidden(Boolean val);

}
