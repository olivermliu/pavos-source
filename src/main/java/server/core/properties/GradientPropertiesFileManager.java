package server.core.properties;

import java.security.InvalidParameterException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerFactory;

public final class GradientPropertiesFileManager {
	
	private Properties properties;
	public final String gradientPropertyFilePath = "src/main/resources/defaultGradients.properties";
	private static GradientPropertiesFileManager instance;
	private Logger logger = new Log4jLoggerFactory().getLogger(this.getClass().toString());;
	
	/**
	 * Default Constructor
	 */
	private GradientPropertiesFileManager() {
		loadGradientProperties();
	}
	
	/**
	 * 
	 * @return it Self
	 */
	public static GradientPropertiesFileManager getInstance() {
		if (instance == null) {
			instance = new GradientPropertiesFileManager();
		}
		return instance;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	/**
	 * Load from File Properties
	 */
	
	private void loadGradientProperties() {
		try {
			properties = PropertyFileReader.readPropertyFile(gradientPropertyFilePath);

			// check if properties file is missing keys

			if (!properties.containsKey("temperature")
					|| !properties.containsKey("temperature.range.celsius")
					|| !properties.containsKey("temperature.range.fahrenheit")) {
				throw new InvalidParameterException();
			}
		}  catch (InvalidParameterException e) {
			logger.error("The configuration file is missing at least one of the following required arguments:\n"
					+ "\t- temperature\n" + "\t- temperature.range.celsius\n"
					+ "\t- temperature.range.fahrenheit", e);
			System.exit(-1);
		}
	}
	
}
