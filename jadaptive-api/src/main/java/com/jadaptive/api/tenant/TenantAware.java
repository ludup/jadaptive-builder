package com.jadaptive.api.tenant;

public interface TenantAware {

	public void initializeSystem(boolean newSchema);
	
	public void initializeTenant(Tenant tenant, boolean newSchema);

	public default Integer getOrder() { return Integer.MAX_VALUE; };
}
