package com.jadaptive.app.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.entity.EntityException;
import com.jadaptive.api.entity.EntityRepository;
import com.jadaptive.api.entity.EntityService;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.repository.TransactionAdapter;
import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.api.templates.SystemTemplates;
import com.jadaptive.api.templates.TemplateEnabledService;

@Service
public class EntityServiceImpl implements EntityService<MongoEntity>, TemplateEnabledService<MongoEntity> {

	@Autowired
	EntityRepository<MongoEntity> entityRepository;
	
	@Autowired
	EntityTemplateService templateService;
	
	@Autowired
	PermissionService permissionService; 
	
	@Override
	public MongoEntity getSingleton(String resourceKey) throws RepositoryException, EntityException {

		EntityTemplate template = templateService.get(resourceKey);
		if(template.getType()!=EntityType.SINGLETON) {
			throw new EntityException(String.format("%s is not a singleton entity", resourceKey));
		}
		
		MongoEntity e = entityRepository.get(resourceKey, resourceKey);
		if(!resourceKey.equals(e.getResourceKey())) {
			throw new IllegalStateException();
		}
		return e;
 	}
	
	@Override
	public MongoEntity get(String resourceKey, String uuid) throws RepositoryException, EntityException {

		EntityTemplate template = templateService.get(resourceKey);
		if(template.getType()!=EntityType.COLLECTION) {
			throw new EntityException(String.format("%s is not a collection entity", resourceKey));
		}
		
		MongoEntity e = entityRepository.get(uuid, resourceKey);
		if(!resourceKey.equals(e.getResourceKey())) {
			throw new IllegalStateException();
		}
		return e;
 	}


	@Override
	public Collection<MongoEntity> list(String resourceKey) throws RepositoryException, EntityException {
		templateService.get(resourceKey);
		return entityRepository.list(resourceKey);
	}

	@Override
	public void saveOrUpdate(MongoEntity entity) throws RepositoryException, EntityException {
		EntityTemplate template = templateService.get(entity.getResourceKey());
		if(template.getType()==EntityType.SINGLETON && !entity.getUuid().equals(entity.getResourceKey())) {	
			throw new EntityException("You cannot save a Singleton Entity with a new UUID");
		}
		entityRepository.save(entity);
		
	}

	@Override
	public void delete(String resourceKey, String uuid) throws RepositoryException, EntityException {
		
		EntityTemplate template = templateService.get(resourceKey);
		if(template.getType()==EntityType.SINGLETON) {	
			throw new EntityException("You cannot delete a Singleton Entity");
		}
		
		MongoEntity e = get(resourceKey, uuid);
		if(e.getSystem()) {
			throw new EntityException("You cannot delete a system object");
		}
		entityRepository.delete(resourceKey, uuid);
	}

	@Override
	public void deleteAll(String resourceKey) throws EntityException {
		
		EntityTemplate template = templateService.get(resourceKey);
		if(template.getType()==EntityType.SINGLETON) {	
			throw new EntityException("You cannot delete a Singleton Entity");
		}
		
		entityRepository.deleteAll(resourceKey);
		
	}
	
	@Override
	public Integer getWeight() {
		return SystemTemplates.ENTITY.ordinal();
	}

	@Override
	public Class<MongoEntity> getResourceClass() {
		return MongoEntity.class;
	}

	@Override
	public String getName() {
		return "Entity";
	}

	@Override
	public MongoEntity createEntity() {
		return new MongoEntity();
	}

	@Override
	public String getResourceKey() {
		return "entity";
	}

	@Override
	public void saveTemplateObjects(List<MongoEntity> objects, @SuppressWarnings("unchecked") TransactionAdapter<MongoEntity>... ops) throws RepositoryException, EntityException {
		
		for(MongoEntity obj : objects) {
			saveOrUpdate(obj);
			for(TransactionAdapter<MongoEntity> op : ops) {
				op.afterSave(obj);
			}
		}
	}
		
	@Override
	public void onTemplatesComplete(String... resourceKeys) {
		
		for(String resourceKey : resourceKeys) {
			permissionService.registerStandardPermissions(resourceKey);
		}
		
	}

	@Override
	public boolean isSystemOnly() {
		return false;
	}

	@Override
	public String getTemplateFolder() {
		return "objects";
	}

	@Override
	public Collection<MongoEntity> table(String resourceKey, String searchField, String searchValue, int offset, int limit) {
		templateService.get(resourceKey);
		return entityRepository.table(resourceKey, searchField, searchValue, offset, limit);
	}

	@Override
	public long count(String resourceKey) {
		templateService.get(resourceKey);
		return entityRepository.count(resourceKey);
	}
	
	@Override
	public long count(String resourceKey, String searchField, String searchValue) {
		templateService.get(resourceKey);
		return entityRepository.count(resourceKey, searchField, searchValue);
	}
}