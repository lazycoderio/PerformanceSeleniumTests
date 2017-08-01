package tests;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import performance.HarLogProcessing;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by andrew on 12/3/16.
 */
@SuppressWarnings("Duplicates")
@Test(groups = {"mac", "windows"})
public class NormalTest {
    private WebDriver driver;
    BrowserMobProxyServer server;
    private String testname;
    @BeforeTest
    public void chromeSetup(ITestContext context){

        server = new BrowserMobProxyServer();
        int port = randomInt(6000, 10000);
        System.out.println("Port: " + Integer.toString(port));

        server.start(port);
        server.enableHarCaptureTypes(CaptureType.getAllContentCaptureTypes());

        Proxy proxy = ClientUtil.createSeleniumProxy(server);
        server.newHar("Lazy Coder Home");

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        capabilities.setCapability(CapabilityType.PROXY, proxy);

        driver = new ChromeDriver(capabilities);
    }
    @Test
    public void simple_test(Method context){

        this.testname = context.getName();

        driver.get("http://localhost:4000/");
        WebElement about = driver.findElement(By.cssSelector("a[href='/about.html']"));

        server.newPage(about.getAttribute("href"), "About Page");

        about.click();

        Assert.assertEquals(driver.getTitle(), "Lazy Coder Origins");
    }

    @AfterTest
    public void testTeardown() throws IOException {
        driver.quit();
        server.endPage();
        HarLog harlog = server.getHar().getLog();

        HarLogProcessing hlp = new HarLogProcessing();

        hlp.harToElasticSearch(testname, harlog);

        //hlp.processHarLog2ES(harlog, testname);

        hlp.processHarLog2Tick(harlog, testname);


        server.endHar();
    }

    /**
     * Returns an int between the min and max values
     *
     * @param min below number than needed
     * @param max higher number than needed
     * @return int between the 2 values
     */
    public int randomInt(int min, int max) {
        Random r = new Random();
        // System.out.println(String.valueOf(min) + " " + String.valueOf(max));
        return r.nextInt((max - min) + 1) + min;
    }
}
