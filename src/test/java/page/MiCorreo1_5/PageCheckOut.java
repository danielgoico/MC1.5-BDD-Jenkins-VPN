package page.MiCorreo1_5;

import framework.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PageCheckOut extends BasePage {
    PageForm pageForm;
    private By integracionLocator = By.xpath("(//td[@class='table-text text-center'])[2]");
    private By origenLocator = By.xpath("(//td[@class='table-text text-center'])[4]");
    private By destinoLocator = By.xpath("(//td[@class='table-text text-center'])[5]");
    private By pesoInformadoLocator = By.xpath("(//td[@class='table-text text-center'])[6]");
    private By pesoVolumetricoLocator = By.xpath("(//td[@class='table-text text-center'])[7]");
    private By medidasCheckoutLocator = By.xpath("(//td[@class='table-text text-center'])[8]");
    private By precioUnitarioLocator = By.xpath("(//td[@class='table-text text-center'])[9]");
    private By pagoTarjeta = By.id("radioTarjeta");
    private By pagoSaldo = By.id("radioSaldo");
    private By pagoCtaCte = By.id("radioCuentaCorriente");
    public PageCheckOut(WebDriver driver){
        super(driver);
        this.pageForm = new PageForm(driver);
    }
    public void validarFormularioCheckout(){
        //assertURL("https://wcpzt.correo.local/MiCorreo/public/misEnviosCheckout");
        validarCampo("Integración", integracionLocator, getText(integracionLocator));
        validarCampo("Origen", origenLocator, getText(origenLocator));
        validarCampo("Destino", destinoLocator, getText(destinoLocator));
        validarCampo("Peso Informado", pesoInformadoLocator,getText(pesoInformadoLocator));
        validarCampo("Peso Volumetrico",pesoVolumetricoLocator,getText(pesoVolumetricoLocator));
        validarCampo("Medidas",medidasCheckoutLocator,getText(medidasCheckoutLocator));
        validarCampo("Precio Unitario", precioUnitarioLocator,getText(precioUnitarioLocator));
        System.out.println("¡Checkout correcto!");
    }
    public void presionarPagar(){
        clickWithRetry(By.xpath("(//button[@id='btnPagar' and normalize-space()='Pagar'])[1]"));
        waitForSeconds(5);
    }
    public void medioPago(String medioPago){
        if (medioPago.equals("Tarjeta")){
            presionarPagar();
            pageForm.pagoConTarjeta();
        }else if (medioPago.equals("Cuenta Corriente")){
            click(pagoCtaCte);
            presionarPagar();
        }else if (medioPago.equals("Saldo")){
            click(pagoSaldo);
            presionarPagar();
        }else {
            throw new IllegalArgumentException("Medio de pago no valido: " + medioPago);
        }
    }
}
