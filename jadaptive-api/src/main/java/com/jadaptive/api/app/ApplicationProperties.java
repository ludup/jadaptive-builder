package com.jadaptive.api.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties {

	Logger log = LoggerFactory.getLogger(ApplicationProperties.class);
	
	static ApplicationProperties instance = new ApplicationProperties();
	static Properties properties;
	static File confFolder;
	
	ApplicationProperties() {
		
		checkBouncyCastleProvider();
		
		confFolder = new File(System.getProperty("jadaptive.conf", "conf"));
		try{
			properties = loadPropertiesFile(new File(confFolder, "jadaptive.properties"));
		} catch(IOException e) {
			log.warn("Could not load jadaptive.properties file [{}]", e.getMessage());
		}
		checkLoaded();
	}
	
	private void checkBouncyCastleProvider() {
		
		Provider provider = Security.getProvider("BC");
		if(Objects.isNull(provider)) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	public static Properties loadPropertiesFile(File propertiesFile) throws IOException {
		Properties properties = new Properties();
		try(InputStream in = new FileInputStream(propertiesFile)) {
			properties.load(in);
		} 
		return properties;
	}
	
	public static File getConfFolder() {
		return confFolder;
	}
	
	public static String getValue(String name, String defaultValue) {
		checkLoaded();
		return properties.getProperty(name, defaultValue);
	}
	
	public static boolean getValue(String name, boolean defaultValue) {
		checkLoaded();
		String val = properties.getProperty(name);
		if(Objects.isNull(val)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(val);
	}
	
	public static int getValue(String name, int defaultValue) {
		checkLoaded();
		String val = properties.getProperty(name);
		if(Objects.isNull(val)) {
			return defaultValue;
		}
		return Integer.parseInt(val);
	}


	private static void checkLoaded() {
		if(Objects.isNull(properties)) {
			throw new IllegalStateException("jadaptive.properties could not be loaded");
		}
	}


	
	
}
