package procesar.com.mx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class App.
 */
public class App {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		LOGGER.warn("Este es un mensaje de información (info).");
		LOGGER.warn("Este es un mensaje de advertencia (warning).");
		LOGGER.error("Este es un mensaje de error (error).");
	}

}