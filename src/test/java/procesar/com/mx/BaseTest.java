package procesar.com.mx;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays; // Import Arrays for permissions list

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Geolocation; // Import Geolocation

import io.qameta.allure.Allure;

/**
 * Clase base para las pruebas automatizadas usando Playwright.
 *
 * Maneja la inicialización y finalización de Playwright, navegador, contexto y
 * página. Permite cambiar el navegador utilizado mediante un parámetro de
 * Maven. Otorga automáticamente el permiso de geolocalización.
 *
 * <p>
 * <b>¿Cómo usar otro navegador?</b>
 * </p>
 *
 * Cuando quieras cambiar de navegador solo debes agregar un parámetro en la
 * ejecución de Maven:
 *
 * <pre>
 * {@code
 * mvn test -Dbrowser=firefox
 * }
 * </pre>
 *
 * o
 *
 * <pre>
 * {@code
 * mvn test -Dbrowser=webkit
 * }
 * </pre>
 *
 * o
 *
 * <pre>
 * {@code
 * mvn test -Dbrowser=chrome // Changed from chromium
 * }
 * </pre>
 *
 * <p>
 * <b>Nota:</b> Si no se proporciona el parámetro, por defecto se utiliza Google
 * Chrome.
 * </p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	/**
	 * Parámetro para elegir el navegador (chrome, firefox o webkit). Default is
	 * chrome.
	 */
	private static final String BROWSER_NAME = System.getProperty("browser", "chrome"); // Default changed to chrome
	protected Playwright playwright;
	protected Browser browser;
	protected BrowserContext context;
	protected Page page;

	// Define default geolocation coordinates (e.g., Mexico City)
	// You can make these configurable if needed
	private static final double DEFAULT_LATITUDE = 19.4326;
	private static final double DEFAULT_LONGITUDE = -99.1332;

	@BeforeAll
	void setUp() {
		playwright = Playwright.create();
	}

	@BeforeEach
	void createContextAndPage() {
		browser = launchBrowser(); // Launch browser first
		if (browser == null) {
			throw new RuntimeException("Failed to launch browser: " + BROWSER_NAME);
		}

		// Configure Browser Context Options
		Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
				// Set Geolocation coordinates
				.setGeolocation(new Geolocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
				// Grant the geolocation permission automatically
				.setPermissions(Arrays.asList("geolocation"));
		// You can add other context options here if needed, e.g., viewport size
		// .setViewportSize(1920, 1080);

		// Create the context with the defined options
		context = browser.newContext(contextOptions);

		// Start tracing
		context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));

		page = context.newPage();
	}

	@AfterEach
	void tearDownContextAndBrowser() { // Renamed for clarity
		if (context != null) {
			String tracePath = "target/trace.zip";
			try {
				context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
				// Adjuntar el trace al reporte de Allure
				attachTraceToAllure(tracePath);
			} catch (Exception e) {
				System.err.println("Error stopping tracing or attaching trace: " + e.getMessage());
				// Optionally log the stack trace: e.printStackTrace();
			} finally {
				context.close(); // Ensure context is closed even if tracing fails
			}
		}
		if (browser != null) {
			browser.close();
		}
	}

	@AfterAll
	void tearDownPlaywright() { // Renamed for clarity
		if (playwright != null) {
			playwright.close();
		}
	}

	/**
	 * Adjunta el archivo trace.zip al reporte de Allure.
	 *
	 * @param tracePath Ruta del archivo de traza generado.
	 */
	private void attachTraceToAllure(String tracePath) {
		try {
			byte[] traceBytes = Files.readAllBytes(Paths.get(tracePath));
			Allure.addAttachment("Playwright Trace (" + BROWSER_NAME + ")", "application/zip",
					new ByteArrayInputStream(traceBytes), ".zip");
		} catch (Exception e) {
			System.err.println("No se pudo adjuntar el trace a Allure: " + e.getMessage());
			// Optionally log the stack trace: e.printStackTrace();
		}
	}

	/**
	 * Lanza el navegador basado en el parámetro proporcionado.
	 *
	 * @return instancia del navegador
	 */
	private Browser launchBrowser() {
		// Common launch options
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false); // Run in headed mode
																								// for visibility

		try {
			switch (BROWSER_NAME.toLowerCase()) {
			case "firefox":
				System.out.println("Launching Firefox...");
				return playwright.firefox().launch(options);
			case "webkit":
				System.out.println("Launching WebKit (Safari)...");
				return playwright.webkit().launch(options);
			case "chrome": // Explicitly handle chrome
				System.out.println("Launching Google Chrome (installed)...");
				// Use setChannel("chrome") to launch the installed Google Chrome
				options.setChannel("chrome");
				return playwright.chromium().launch(options);
			case "chromium": // Keep option for standard chromium if needed
				System.out.println("Launching Chromium (default)...");
				// Launch default Chromium (downloaded by Playwright)
				return playwright.chromium().launch(options);
			default:
				System.out.println("Browser name '" + BROWSER_NAME
						+ "' not recognized. Launching Google Chrome (installed) by default...");
				// Default to installed Google Chrome
				options.setChannel("chrome");
				return playwright.chromium().launch(options);
			}
		} catch (Exception e) {
			System.err.println("Error launching browser " + BROWSER_NAME + ": " + e.getMessage());
			// Log the stack trace for detailed debugging
			e.printStackTrace();
			return null; // Return null or handle the error as appropriate
		}
	}
}
