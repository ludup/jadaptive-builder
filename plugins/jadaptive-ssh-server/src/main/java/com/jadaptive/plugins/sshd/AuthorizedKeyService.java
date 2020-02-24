package com.jadaptive.plugins.sshd;

import java.util.Collection;

import com.jadaptive.api.user.User;

public interface AuthorizedKeyService {

	Collection<AuthorizedKey> getAuthorizedKeys(User user);

}