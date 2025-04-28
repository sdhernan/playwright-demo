package procesar.com.mx;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

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

import io.qameta.allure.Allure;

/**
 * Clase base para las pruebas automatizadas usando Playwright.
 * 
 * Maneja la inicialización y finalización de Playwright, navegador, contexto y
 * página. Permite cambiar el navegador utilizado mediante un parámetro de
 * Maven.
 *
 * <p>
 * <b>¿Cómo usar otro navegador?</b>
 * </p>
 *
 * Cuando quieras cambiar de navegador solo debes agregar un parámetro en la
 * ejecución de Maven:
 *
 * <pre>{@code
 * mvn test -Dbrowser=firefox
 * }</pre>
 *
 * o
 *
 * <pre>{@code
 * mvn test -Dbrowser=webkit
 * }</pre>
 *
 * o
 *
 * <pre>{@code
 * mvn test -Dbrowser=chromium
 * }</pre>
 *
 * <p>
 * <b>Nota:</b> Si no se proporciona el parámetro, por defecto se utiliza
 * Chromium.
 * </p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

	protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	/** Parámetro para elegir el navegador (chromium, firefox o webkit). */
	private static final String BROWSER_NAME = System.getProperty("browser", "chromium");
	protected Playwright playwright;
	protected Browser browser;
	protected BrowserContext context;

	protected Page page;

	@BeforeEach
	void createContextAndPage() {
		browser = launchBrowser();
		context = browser.newContext();

		context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));

		page = context.newPage();
	}

	@BeforeAll
	void setUp() {
		playwright = Playwright.create();
	}

	@AfterAll
	void tearDown() {
		if (playwright != null) {
			playwright.close();
		}
	}

	@AfterEach
	void tearDownContext() {
		if (context != null) {
			String tracePath = "target/trace.zip";
			context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
			// Adjuntar el trace al reporte de Allure
			attachTraceToAllure(tracePath);
			context.close();
		}
		if (browser != null) {
			browser.close();
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
			Allure.addAttachment("Playwright Trace", "application/zip", new ByteArrayInputStream(traceBytes), ".zip");
		} catch (Exception e) {
			System.err.println("No se pudo adjuntar el trace a Allure: " + e.getMessage());
		}
	}

	/**
	 * Lanza el navegador basado en el parámetro proporcionado.
	 * 
	 * @return instancia del navegador
	 */
	private Browser launchBrowser() {
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);

		switch (BROWSER_NAME.toLowerCase()) {
		case "firefox":
			return playwright.firefox().launch(options);
		case "webkit":
			return playwright.webkit().launch(options);
		case "chromium":
		default:
			return playwright.chromium().launch(options);
		}
	}
}