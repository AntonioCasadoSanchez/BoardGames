package edu.uclm.esi.web.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class Test_Pruebas {
	  private  WebDriver driverA, driverB;
	  private  String baseUrl;
	  private boolean acceptNextAlert = true;
	  private static StringBuffer verificationErrors = new StringBuffer();

	  @Before
	  public  void setUp() throws Exception {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Oliva\\Documents\\chromedriver.exe");
		driverA = new ChromeDriver();
		driverB = new ChromeDriver();
	    baseUrl = "https://www.katalon.com/";
	    driverA.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    driverB.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	  }

	  @Test
	  public void testPruebas() throws Exception {
		  login ();
		  testSudoku();
	  }
	  
	  public void login () {
		  driverA.get("http://localhost:8080/index.html");
		  driverB.get("http://localhost:8080/");
		  driverA.findElement(By.id("UserName")).click();
		  driverA.findElement(By.id("UserName")).clear();
		  driverA.findElement(By.id("UserName")).sendKeys("oliva");
		  driverA.findElement(By.id("Pwd")).click();
		  driverA.findElement(By.id("Pwd")).clear();
		  driverA.findElement(By.id("Pwd")).sendKeys("oliva123");
		  driverA.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Acceder'])[1]/following::button[1]")).click();
		  driverB.findElement(By.id("UserName")).click();
		  driverB.findElement(By.id("UserName")).clear();
		  driverB.findElement(By.id("UserName")).sendKeys("jose");
		  driverB.findElement(By.id("Pwd")).click();
		  driverB.findElement(By.id("Pwd")).clear();
		  driverB.findElement(By.id("Pwd")).sendKeys("jose123");
		  driverB.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Acceder'])[1]/following::button[1]")).click();
	  }
	  public void testSudoku () {
		  driverA.get("http://localhost:8080/salaEspera.html#close");
		  driverA.findElement(By.id("boton_destape")).click();
		  driverA.findElement(
				By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='X'])[2]/following::button[1]")).click();
		  driverB.get("http://localhost:8080/salaEspera.html#close");
		  driverB.findElement(By.id("boton_destape")).click();
		  driverB.findElement(
				By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='X'])[2]/following::button[1]")).click();
		  driverA.findElement(By.id("c00")).click();
		  driverA.findElement(By.id("c00")).clear();
		  driverA.findElement(By.id("c00")).sendKeys("2");
		  driverB.findElement(By.id("c02")).click();
		  driverB.findElement(By.id("c02")).clear();
		  driverB.findElement(By.id("c02")).sendKeys("5");
		  driverA.findElement(By.xpath("//div")).click();
		  driverA.findElement(By.id("limpiar")).click();
		  acceptNextAlert = true;
		  
	  }

	  @After
	  public void tearDown() throws Exception {

	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) {
	      fail(verificationErrorString);
	    }
	  }

	  private boolean isElementPresent(By by) {
	    try {
	    	driverA.findElement(by);
	    	driverB.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	  private boolean isAlertPresent() {
	    try {
	    	driverA.switchTo().alert();
	    	driverB.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }

	  private String closeAlertAndGetItsText() {
	    try {
	      Alert alert = driverA.switchTo().alert();
	      Alert alert2 = driverB.switchTo().alert();
	      String alertText = alert.getText();
	      if (acceptNextAlert) {
	        alert.accept();
	      } else {
	        alert.dismiss();
	      }
	      return alertText;
	    } finally {
	      acceptNextAlert = true;
	    }
	  }
}
