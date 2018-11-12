package com.stpl.gtn.gtn2o.ws;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GtnFrameworkPropertyManager {
	private GtnFrameworkPropertyManager() {
		/**
		 * empty constructor
		 */
	}

	private static final Properties properties = new Properties();

	private static final String GTN_FRAMEWORK_BASE_PATH = System.getProperty("com.stpl.gtnframework.base.path");

	private static final String GTN_FRAMEWORK_PROPERTY_FILE_PATH = GTN_FRAMEWORK_BASE_PATH + "/GtnFramework.properties";

	static {
		try {
			loadProperty();
		} catch (IOException ex) {
			Logger.getLogger(GtnFrameworkPropertyManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void loadProperty() throws IOException {
		properties.load(GtnFileNameUtils.getFileInputStream(GTN_FRAMEWORK_PROPERTY_FILE_PATH));
		properties.put("com.stpl.gtnframework.base.path", GTN_FRAMEWORK_BASE_PATH);
	}

	/**
	 * returns the Property for key present in GtnFrameWork.properties file
	 *
	 * @param key
	 * @return
	 */
	public static String getProperty(String key) {
		return String.valueOf(properties.get(key));
	}

	public static String getGtnFrameWorkBasePath() {
		return GTN_FRAMEWORK_BASE_PATH;
	}

	public static String getProperty(String bpiPropLoc, String key) throws IOException {
		Properties prop = new Properties();
		prop.load(GtnFileNameUtils.getFileInputStream(bpiPropLoc));
		return prop.getProperty(key);
	}

}
