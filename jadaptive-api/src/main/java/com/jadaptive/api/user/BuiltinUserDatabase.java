package com.jadaptive.api.user;

public interface BuiltinUserDatabase extends UserDatabase {

	User createUser(String username, String name, String email, char[] password, boolean passwordChangeRequired);

	void saveOrUpdate(User user);

	void deleteUser(User user);
}
