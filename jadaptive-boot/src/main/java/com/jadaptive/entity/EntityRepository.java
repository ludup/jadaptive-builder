package com.jadaptive.entity;

import java.util.Collection;

import com.jadaptive.repository.RepositoryException;
import com.jadaptive.templates.TemplateEnabledUUIDRepository;

public interface EntityRepository extends TemplateEnabledUUIDRepository<Entity> {

	Collection<Entity> list(String resourceKey) throws RepositoryException, EntityException;

	Entity get(String uuid, String resourceKey) throws RepositoryException, EntityException;

	void delete(String resourceKey, String uuid) throws RepositoryException, EntityException;

	void deleteAll(String resourceKey) throws RepositoryException, EntityException;

	void save(Entity entity) throws RepositoryException, EntityException;


}
