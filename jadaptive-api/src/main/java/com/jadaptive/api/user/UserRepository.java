package com.jadaptive.api.user;

import com.jadaptive.api.tenant.AbstractTenantAwareObjectDatabase;

public interface UserRepository extends AbstractTenantAwareObjectDatabase<DefaultUser> {

	void createUser(DefaultUser user, char[] password, boolean passwordChangeRequired);

	void setPassword(User user, char[] password, boolean passwordChangeRequired);

	boolean verifyPassword(User user, char[] password);

	User findUsername(String username);

}
