package procesar.com.mx;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * Pruebas específicas de navegación en Playwright.
 */
@Epic("Pruebas de navegación básica")
@Feature("Navegación en sitio Playwright")
class AppTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

	// Map to store screenshot counters for each test method execution
	// Using ConcurrentHashMap for thread safety, although default JUnit execution
	// is sequential per class
	private final Map<String, Integer> screenshotCounters = new ConcurrentHashMap<>();

	// Define a longer timeout in milliseconds (e.g., 60 seconds)
	private static final double LONG_TIMEOUT = 300000000;

	// @Test
	@Story("Navegar y validar sección Get Started")
	@Description("Forzar un fallo para probar categorías en Allure.")
	@Severity(SeverityLevel.CRITICAL)
	@DisplayName("Forzar fallo para probar Categories")
	void testFalloForzado() {
		page.navigate("https://playwright.dev");
		page.click("text=Get started");

		try {
			// Aquí forzamos una falla intencional
			assertTrue(false, "Forzamos un error para ver categorías");
		} catch (Exception e) {
			LOGGER.error("Exception: ", e);
		}

	}

	/**
	 * Test full page screenshot.
	 *
	 * @param testInfo the test info
	 * @throws Exception the exception
	 */
	// @Test
	@Story("Capturar página completa Playwright")
	@Description("Captura un screenshot de toda la página principal de Playwright y lo adjunta al reporte de Allure.")
	@Severity(SeverityLevel.NORMAL)
	@DisplayName("Captura de pantalla completa en Playwright.dev")
	void testFullPageScreenshot(TestInfo testInfo) throws Exception {
		page.navigate("https://playwright.dev");

		takeFullPageScreenshot(testInfo);

		Thread.sleep(1000); // Pausa breve para visualizar
	}

	/**
	 * Test playwright.
	 *
	 * @param testInfo the test info
	 * @throws InterruptedException the interrupted exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	// @Test
	@Story("Navegar y validar sección Get Started")
	@Description("Navega al sitio oficial de Playwright y valida que se muestra el encabezado correcto en la sección Get Started.")
	@Severity(SeverityLevel.CRITICAL)
	@DisplayName("Validar acceso a Get Started en Playwright.dev")
	void testPlaywright(TestInfo testInfo) throws InterruptedException, IOException {
		page.navigate("https://www.google.com.mx/");

		// Localizar el campo de búsqueda por su nombre (q) y escribir el texto
		page.locator("[name='q']").fill("Playwright");

		// Localizar el botón "Buscar con Google" por su atributo value y hacer clic
		page.locator("input[value='Buscar con Google']").first().click();

		page.waitForLoadState();

		takeFullPageScreenshot(testInfo);

	}

	/**
	 * Valida el flujo completo de la pantalla 360 de Banamex en un entorno local.
	 * Este método navega a una página HTML específica, realiza acciones como
	 * ingresar al sistema, completar formularios, realizar consultas y finalizar la
	 * sesión, capturando pantallas en cada paso.
	 *
	 * @author [Tu Nombre]
	 * @version 1.0
	 * @since 2025-04-05
	 * @see <a href="https://jira.example.com/feature/360-banamex">JIRA - PANTALLA
	 *      360 BANAMEX</a>
	 */
	@Test
	@Story("Validar pantalla 360 Banamex")
	@Description("Navega a una página local de Banamex, realiza acciones y captura pantallas en cada paso.")
	@Severity(SeverityLevel.CRITICAL)
	@DisplayName("Validar flujo pantalla 360 Banamex local")
	void testPantalla360(TestInfo testInfo) throws InterruptedException {
		try {
			// Navegar a archivo HTML local
			LOGGER.info("Navigating to local file...");
			page.navigate(
					"file:///D:/Documentacion/Accesar%20CitiBanamex/desarrollo-1688955908-Libre%20Agente%20-%20Susana.html");
			takeFullPageScreenshot(testInfo, "LoginPage"); // Screenshot 1: Login Page

			// Hacer clic en botón 'Ingresar'
			LOGGER.info("Clicking 'Ingresar' button...");
			page.locator("input[value='Ingresar']").click();

			// Esperar carga completa de la página
			LOGGER.info("Waiting for page load after login...");
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));
			takeFullPageScreenshot(testInfo, "AfterLogin"); // Screenshot 2: After clicking Ingresar

			// Hacer clic en elemento con ID 'botoni_FRONT'
			LOGGER.info("Clicking element with ID 'botoni_FRONT'...");

			page.locator("//*[@id='botoni_FRONT']").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			LOGGER.info("Waiting 20 seconds for redirection...");
			Thread.sleep(20000);

			// Paso 1: Seleccionar campo de hora
			LOGGER.info("Clicking on timepicker1 input...");
			page.locator("#timepicker1").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			// Paso 3: Continuar con el proceso
			LOGGER.info("Clicking on continue button...");
			page.locator("#btnClockContinuar").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			// Esperar carga después de continuar
			LOGGER.info("Waiting for page load after clicking continue button...");
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));

			// Paso 4: Ingresar CURP
			LOGGER.info("Entering CURP value...");
			page.locator("#idCurpConsulta").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));
			page.locator("#idCurpConsulta").fill("CAOE700723MSLSJV18");

			// Paso 5: Consultar información
			LOGGER.info("Clicking on consult button...");
			page.locator("#btnConsultar").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			// Esperar estabilización de la página
			LOGGER.info("Waiting additional 5 seconds for page to stabilize...");
			Thread.sleep(5000);

			// Capturar pantalla final
			takeFullPageScreenshot(testInfo, "FinalResult");

			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));

			// Finalizar proceso
			page.locator("a[href='/pulssar-bnmx/private/finalizaProceso.do']")
					.click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));

			// Paso 1: Seleccionar campo de hora
			LOGGER.info("Clicking on timepicker1 input...");
			page.locator("#timepicker1").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			// Paso 3: Continuar con el proceso
			LOGGER.info("Clicking on continue button...");
			page.locator("#btnClockContinuar").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));

			// Esperar carga después de continuar
			LOGGER.info("Waiting for page load after clicking continue button...");
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));

			// Cerrar sesión
			LOGGER.info("Logging out...");
			page.locator("#sesionLogout").click(new Locator.ClickOptions().setTimeout(LONG_TIMEOUT));
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));

			takeFullPageScreenshot(testInfo, "Final Sesion");

			Thread.sleep(5000);

			LOGGER.info("Test testPantalla360 completed.");

		} catch (Exception e) {
			// Manejo de errores: cerrar sesión si ocurre un error
			LOGGER.error("Error during test execution, logging out...", e);
			page.navigate("http://localhost:7001/comparador/A552/private/logout.do");
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));
			Thread.sleep(10000);
		}
	}

	/**
	 * Adjunta un screenshot al reporte de Allure con un nombre descriptivo.
	 *
	 * @param attachmentName Nombre descriptivo para la adjunción en Allure.
	 * @param screenshotPath Ruta del archivo de captura.
	 * @return the byte[] Contenido del archivo de imagen.
	 */
	@Attachment(value = "{attachmentName}", type = "image/png")
	private byte[] attachScreenshotToAllure(String attachmentName, Path screenshotPath) {
		try {
			LOGGER.info("Attaching screenshot to Allure: {} from path: {}", attachmentName, screenshotPath);
			return Files.readAllBytes(screenshotPath);
		} catch (IOException e) { // Catch IOException specifically
			LOGGER.error("Error reading screenshot file for Allure attachment: {}", screenshotPath, e);
			return new byte[0]; // Return empty byte array on error
		} catch (Exception e) {
			LOGGER.error("Unexpected error attaching screenshot {} to Allure: {}", screenshotPath, e.getMessage(), e);
			return new byte[0];
		}
	}

	/**
	 * Toma un screenshot de toda la página, lo guarda con un número secuencial para
	 * el test actual y lo adjunta a Allure. Usa el nombre del método y un contador.
	 *
	 * @param testInfo Información del test en ejecución.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void takeFullPageScreenshot(TestInfo testInfo) throws IOException {
		// Overload without description suffix - uses default naming
		takeFullPageScreenshot(testInfo, null);
	}

	/**
	 * Toma un screenshot de toda la página, lo guarda con un número secuencial para
	 * el test actual y un sufijo descriptivo opcional, y lo adjunta a Allure.
	 *
	 * @param testInfo          Información del test en ejecución.
	 * @param descriptionSuffix Sufijo descriptivo opcional para el nombre del
	 *                          archivo y la adjunción (e.g., "BeforeLogin",
	 *                          "AfterSubmit").
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void takeFullPageScreenshot(TestInfo testInfo, String descriptionSuffix) throws IOException {
		// Get class and method names safely
		String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
		String methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("UnknownTest");

		// Create a unique key for the test method instance
		String testKey = className + "#" + methodName;

		// Get and increment the counter for this specific test method atomically
		int counter = screenshotCounters.compute(testKey, (key, count) -> (count == null) ? 1 : count + 1);

		// Create directory if it doesn't exist
		Path directory = Paths.get("target/screenshots", className);
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			LOGGER.error("Failed to create screenshot directory: {}", directory, e);
			throw e; // Re-throw exception as we cannot save the screenshot
		}

		// Construct filename with method name, counter, and optional suffix
		String filenameSuffix = (descriptionSuffix != null && !descriptionSuffix.trim().isEmpty())
				? "_" + descriptionSuffix.trim()
				: "";
		String filename = String.format("%s_%d%s.png", methodName, counter, filenameSuffix);
		Path screenshotPath = directory.resolve(filename);

		// Take the screenshot
		try {
			page.screenshot(new Page.ScreenshotOptions().setFullPage(true).setPath(screenshotPath));
			LOGGER.info("Screenshot saved: {}", screenshotPath);
		} catch (Exception e) {
			LOGGER.error("Failed to take or save screenshot to path: {}", screenshotPath, e);
			// Decide if you want to re-throw or just log
			throw new IOException("Failed to take screenshot: " + e.getMessage(), e);
		}

		// Construct attachment name for Allure
		String attachmentName = String.format("Screenshot %d - %s%s", counter, methodName,
				filenameSuffix.replace("_", " "));

		// Attach screenshot to Allure report
		attachScreenshotToAllure(attachmentName, screenshotPath);
	}
}