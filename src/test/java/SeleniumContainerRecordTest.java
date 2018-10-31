import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.VncRecordingContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;


public class SeleniumContainerRecordTest {

    private static BrowserWebDriverContainer chrome = new BrowserWebDriverContainer<>()
            .withDesiredCapabilities(DesiredCapabilities.chrome())
            .withNetwork(Network.SHARED)
            .withNetworkAliases("vnchost")
            .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("target/"));

    private static VncRecordingContainer vnc = new VncRecordingContainer(chrome);


    @BeforeClass
    public static void setUp() {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        WebDriverRunner.setWebDriver(driver);
    }

    @AfterClass
    public static void tearDown() {
        chrome.stop();
    }

    @Test
    public void test() {

        vnc.start();

        Selenide.open("https://wikipedia.org");
        $("input#searchInput").val("Eminem").submit();
        boolean expectedTextFound = Selenide.$$("p")
                .stream()
                .anyMatch(element -> element.getText().contains("rapper"));
        assertTrue("The word 'rapper' is found on a page", expectedTextFound);

//        vnc.saveRecordingToFile(new File("target/" + System.currentTimeMillis() + ".flv"));
        vnc.stop();
    }

}