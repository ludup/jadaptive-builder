package com.jadaptive.app.ui;

import java.io.FileNotFoundException;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codesmith.webbits.HTTPMethod;
import com.codesmith.webbits.In;
import com.codesmith.webbits.Out;
import com.codesmith.webbits.Page;
import com.codesmith.webbits.Resource;
import com.codesmith.webbits.View;
import com.codesmith.webbits.bootstrap.BootstrapTable;
import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.api.permissions.PermissionService;

@Page(BootstrapTable.class)
@View(contentType = "text/html", paths = { "/import/{resourceKey}"})
@Component
@Resource
public class Import extends TemplatePage {
    
	@Autowired
	private PermissionService permissionService; 
	
	@Override
	protected void onCreated() throws FileNotFoundException {

		super.onCreated();
		
		try {
			permissionService.assertReadWrite(getTemplate().getResourceKey());
		} catch(AccessDeniedException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	protected boolean isModal() {
		return true;
	}
	
	@Out(methods = HTTPMethod.POST)
    Document service(@In Document content) {
    	return content;
    }
}
