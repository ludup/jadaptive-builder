package com.jadaptive.api.template;

import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.repository.UUIDEntity;

public interface TemplateService {

	ObjectTemplate get(String uuid)
			throws RepositoryException, ObjectException;

	Collection<ObjectTemplate> list() throws RepositoryException, ObjectException;
	
	Collection<ObjectTemplate> singletons() throws RepositoryException, ObjectException;
	
	Collection<ObjectTemplate> table(String searchField, String searchValue, String order, int start, int length) throws RepositoryException, ObjectException;

	void saveOrUpdate(ObjectTemplate template) throws RepositoryException, ObjectException;

	void delete(String uuid) throws ObjectException;

	long count();

	<T extends UUIDEntity> T createObject(Map<String, Object> values, Class<T> baseClass) throws ParseException;

	Collection<ObjectTemplate> children(String uuid);


}
