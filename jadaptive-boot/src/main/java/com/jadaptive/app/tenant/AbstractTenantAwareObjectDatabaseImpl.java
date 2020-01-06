package com.jadaptive.app.tenant;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.EntityException;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.tenant.AbstractTenantAwareObjectDatabase;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.app.db.AbstractObjectDatabaseImpl;
import com.jadaptive.app.db.DocumentDatabase;

public abstract class AbstractTenantAwareObjectDatabaseImpl<T extends AbstractUUIDEntity> 
		extends AbstractObjectDatabaseImpl implements AbstractTenantAwareObjectDatabase<T> {

	
	protected AbstractTenantAwareObjectDatabaseImpl(DocumentDatabase db) {
		super(db);
	}

	@Autowired
	protected TenantService tenantService;
	
	@Override
	public Collection<T> list() throws RepositoryException, EntityException {
		return listObjects(tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}
	
	@Override
	public Collection<T> list(String field, String value) {
		return listObjects(field, value, tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}
	
	@Override
	public Collection<T> matchCollectionObjects(String field, String value) {
		return matchCollectionObjects(field, value, tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}

	@Override
	public T get(String uuid) throws RepositoryException, EntityException {
		return getObject(uuid, tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}
	
	public T get(String field, String value) throws RepositoryException, EntityException {
		return getObject(field, value, tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}

	@Override
	public void delete(T obj) throws RepositoryException, EntityException {
		delete(obj.getUuid());
	}
	
	@Override
	public void delete(String uuid) throws RepositoryException, EntityException {
		deleteObject(get(uuid), tenantService.getCurrentTenant().getUuid());
	}

	@Override
	public void saveOrUpdate(T obj) throws RepositoryException, EntityException {
		saveObject(obj, tenantService.getCurrentTenant().getUuid());
	}

	@Override
	public Collection<T> table(String searchField, String searchValue, String order, int start, int length) {
		return tableObjects(tenantService.getCurrentTenant().getUuid(), getResourceClass(), searchField, searchValue, start, length);
	}

	@Override
	public long count() {
		return countObjects(tenantService.getCurrentTenant().getUuid(), getResourceClass());
	}

}