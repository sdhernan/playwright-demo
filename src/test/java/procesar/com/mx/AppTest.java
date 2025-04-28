package procesar.com.mx;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Page;

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

	@Test
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
	@Test
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
	@Test
	@Story("Navegar y validar sección Get Started")
	@Description("Navega al sitio oficial de Playwright y valida que se muestra el encabezado correcto en la sección Get Started.")
	@Severity(SeverityLevel.CRITICAL)
	@DisplayName("Validar acceso a Get Started en Playwright.dev")
	void testPlaywright(TestInfo testInfo) throws InterruptedException, IOException {
		page.navigate("https://playwright.dev");
		page.click("text=Get started");

		String headingText = page.locator("h1.hero__title").textContent();
		assertNotNull(headingText);

		takeFullPageScreenshot(testInfo);

		Thread.sleep(1000); // Pausa breve para visualizar
	}

	/**
	 * Adjunta un screenshot al reporte de Allure.
	 *
	 * @param screenshotPath Ruta del archivo de captura.
	 * @return the byte[]
	 */
	@Attachment(value = "{screenshotPath}", type = "image/png")
	private byte[] attachScreenshotToAllure(Path screenshotPath) {
		try {
			return Files.readAllBytes(screenshotPath);
		} catch (Exception e) {
			System.err.println("Error al adjuntar el screenshot: " + e.getMessage());
			return new byte[0];
		}
	}

	/**
	 * Toma un screenshot de toda la página y lo guarda por clase y nombre de test.
	 *
	 * @param testInfo Información del test en ejecución.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void takeFullPageScreenshot(TestInfo testInfo) throws IOException {
		String className = testInfo.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
		String methodName = testInfo.getTestMethod().map(m -> m.getName()).orElse("UnknownTest");

		Path directory = Paths.get("target/screenshots", className);
		Files.createDirectories(directory);

		Path screenshotPath = directory.resolve(methodName + ".png");
		page.screenshot(new Page.ScreenshotOptions().setFullPage(true).setPath(screenshotPath));

		attachScreenshotToAllure(screenshotPath);
	}
}