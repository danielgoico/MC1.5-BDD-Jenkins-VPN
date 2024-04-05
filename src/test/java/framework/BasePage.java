package framework;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;


public class BasePage {
    private final WebDriver driver;
    public final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void click(By locator) {
        findElement(locator).click();
    }
    public void clickDoble(By locator){
        try{click(locator);
        }
        catch (Exception e){
            click(locator);
        }
    }

    public void clickWithRetry(By locator) {
        int maxAttempts = 3; // Número máximo de intentos
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                click(locator);
                return; // Salir del método si la operación de clic tiene éxito
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                // Manejar la excepción (puede agregar un registro o tomar una captura de pantalla aquí)
                System.err.println("Error al hacer clic en el elemento: " + e.getMessage());
                // Esperar antes de intentar nuevamente
                waitForSeconds(1);
            }
        }
        // Si todos los intentos fallan, lanzar la excepción
        throw new ElementNotInteractableException("No se pudo interactuar con el elemento después de " + maxAttempts + " intentos");
    }

    public void moveToElementAndClick(By locator) {
        Actions actions = new Actions(driver);
        WebElement element = findElement(locator);
        actions.moveToElement(element).click().perform();
    }

    public boolean elementExists(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator)) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Método para seleccionar una opción de un dropdown por valor
    public void selectOptionFromDropdownByValue(String dropdownId, String value) {
        WebElement dropdownElement = driver.findElement(By.id(dropdownId));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByValue(value);
    }


    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public void assertURL(String expectedUrl) {
        String currentUrl = getCurrentURL();
        Assert.assertEquals("La URL actual no coincide con la URL esperada", expectedUrl, currentUrl);
    }

    public boolean waitForUrlToBe(String url, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.urlToBe(url));
        } catch (TimeoutException e) {
            return false; // El tiempo de espera se agotó antes de que la URL coincidiera
        }
    }

    public void writeText(By locator, String text) {
        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator) {
        return findElement(locator).getText().trim();
    }
    public boolean compararTextoConMensajeEsperado(By locator, String textoEsperado) {
        // Obtener el texto del sitio utilizando el localizador proporcionado
        String textoDelSitio = getText(locator);
        // Comparar el texto del sitio con el texto esperado
        return textoDelSitio.equals(textoEsperado);
    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void waitForElementToBeClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    protected List<WebElement> sendWebElements(By locator) {
        try {
            return driver.findElements(locator);
        } catch (NoSuchElementException e) {
            System.out.println("Elemento no encontrado con el localizador proporcionado: " + locator);
            return null;
        }
    }
    public void clickLastElementInDropdown(By locator) {
        List<WebElement> dropdownElements = sendWebElements(locator);

        if (dropdownElements != null && !dropdownElements.isEmpty()) {
            WebElement lastElement = dropdownElements.get(dropdownElements.size() - 1);
            try {
                lastElement.click();
            } catch (Exception e) {
                System.out.println("Error al hacer clic en el último elemento del menú desplegable: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontraron elementos en el dropdown con el locator proporcionado: " + locator);
        }
    }

    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void validarCampo(String nombreCampo, By locator, String textoEsperado) {
        String textoCampo = getText(locator); // Utiliza el método getText() de BasePage para obtener el texto del elemento
        // Verifica si el texto del campo coincide con el texto esperado
        if (textoCampo.equals(textoEsperado)) {
            System.out.println("El campo '" + nombreCampo + "' está correctamente llenado: " + textoCampo);
        } else {
            System.out.println("El campo '" + nombreCampo + "' no coincide con el texto esperado.");
            System.out.println("Texto esperado: " + textoEsperado);
            System.out.println("Texto actual: " + textoCampo);
        }
    }
    public boolean validarCampoExistenteYEditable(By locator) {
        try {
            // Buscar el elemento por el locator proporcionado
            WebElement element = findElement(locator);

            // Validar que el elemento existe
            Assert.assertTrue(element.isDisplayed());

            // Validar que el elemento es editable (en este caso, solo para campos de texto)
            Assert.assertTrue(element.isEnabled());

            // Si se llega a este punto, la validación fue exitosa
            return true;
        } catch (Exception e) {
            // En caso de cualquier excepción, capturar y mostrar el mensaje de error
            e.printStackTrace();
            System.out.println("Error al validar el campo: " + e.getMessage());
            // La validación no fue exitosa
            return false;
        }
    }

    // Método personalizado para cambiar al contexto de un iframe específico
    public void switchToFrame(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    // Método personalizado para regresar al contexto del iframe padre
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }

    // Método para cambiar al contexto predeterminado de la página
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // Método para obtener los identificadores de todas las ventanas abiertas
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    // Método para cambiar al contexto de una ventana específica
    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    // Cambia al contexto de un iframe utilizando su índice.
    public void switchToFrameByIndex(int index) {
        driver.switchTo().frame(index);
    }

    // Cambia al contexto de un iframe utilizando su nombre o ID.
    public void switchToFrameByNameOrId(String nameOrId) {
        driver.switchTo().frame(nameOrId);
    }

}
