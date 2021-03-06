package com.jadaptive.app.tasks;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.tasks.TaskImpl;
import com.jadaptive.api.tasks.TaskService;
import com.jadaptive.api.template.ObjectDefinition;

@Service
public class TaskServiceImpl extends AuthenticatedService implements TaskService {

	Map<String,TaskImpl<?>> taskImplementations = new HashMap<>();
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Override
	public <T extends Task> TaskImpl<T> getTaskImplementation(T task) {
		
		loadTaskImplementations();
		
		ObjectDefinition template = task.getClass().getAnnotation(ObjectDefinition.class);
		if(Objects.isNull(template)) {
			throw new IllegalStateException(String.format("%s is not annontated template", task.getClass().getSimpleName()));
		}
		 @SuppressWarnings("unchecked")
		TaskImpl<T> taskImpl = (TaskImpl<T>) taskImplementations.get(template.resourceKey());
		
		 if(Objects.isNull(taskImpl)) {
			 throw new IllegalStateException(String.format("Task %s does not have an implementation class", template.resourceKey()));
		 }
		 
		 return taskImpl;
	}

	@Override
	public Collection<String> getTaskResourceKeys() {
		loadTaskImplementations();
		return Collections.unmodifiableCollection(taskImplementations.keySet());
	}
	
	private void loadTaskImplementations() {
		
		if(taskImplementations.isEmpty()) {
			for(TaskImpl<?> taskImpl : applicationService.getBeans(TaskImpl.class)) {
				taskImplementations.put(taskImpl.getResourceKey(), taskImpl);
			}
		}
	}
}
