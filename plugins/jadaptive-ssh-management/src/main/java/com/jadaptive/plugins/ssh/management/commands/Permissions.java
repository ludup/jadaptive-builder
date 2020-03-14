package com.jadaptive.plugins.ssh.management.commands;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.sshd.commands.AbstractTenantAwareCommand;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;

public class Permissions extends AbstractTenantAwareCommand {

	@Autowired
	PermissionService permissionService; 
	
	@Autowired
	UserService userService; 
	
	public Permissions() {
		super("permissions", "User Management", "permissions",
				"List all permissions");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		printPermissions();
	}

	private void printPermissions() {
		for(String perm : permissionService.getAllPermissions()) {
			console.println(perm);
		}
	}
}
