package com.testinium.base;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.testinium.model.ElementInfo;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.BeforeSpec;
import com.thoughtworks.gauge.BeforeSuite;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class BaseTest {

    protected static WebDriver driver;
    protected static Actions actions;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    DesiredCapabilities capabilities;
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;

    String browserName = "chrome";
    String selectPlatform = "win";

    String currentWorkingDir = System.getProperty("user.dir");
    ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();
    static File[] fileList = null;




    @BeforeScenario
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        logger.info("************************************  BeforeScenario  ************************************");
        try {
            if (StringUtils.isEmpty(System.getenv("key"))) {
                logger.info("Local cihazda " + selectPlatform + " ortamında " + browserName + " browserında test ayağa kalkacak");
                if ("win".equalsIgnoreCase(selectPlatform)) {
                    if ("chrome".equalsIgnoreCase(browserName)) {
                        driver = new ChromeDriver();
                        //driver = new ChromeDriver(chromeOptions());
                    } else if ("firefox".equalsIgnoreCase(browserName)) {
                        driver = new FirefoxDriver();
                        //driver = new FirefoxDriver(firefoxOptions());
                    }
                } else if ("mac".equalsIgnoreCase(selectPlatform)) {
                    if ("chrome".equalsIgnoreCase(browserName)) {
                        driver = new ChromeDriver();
                        //driver = new ChromeDriver(chromeOptions());
                    } else if ("firefox".equalsIgnoreCase(browserName)) {
                        driver = new FirefoxDriver();
                        //driver = new FirefoxDriver(firefoxOptions());
                    }
                }
                actions = new Actions(driver);
                //driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                driver.manage().window().maximize();

            } else {
                logger.info("************************************   Testiniumda test ayağa kalkacak   ************************************");
                ChromeOptions options = new ChromeOptions();
                //capabilities = DesiredCapabilities.chrome();
                options.setExperimentalOption("w3c", false);
                options.addArguments("disable-translate");
                options.addArguments("--disable-notifications");
                options.addArguments("--start-fullscreen");
                Map<String, Object> prefs = new HashMap<>();
                options.setExperimentalOption("prefs", prefs);
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                capabilities.setCapability("key", System.getenv("key"));
                browserName = System.getenv("browser");
                driver = new RemoteWebDriver(new URL("http://hub.testinium.io/wd/hub"), capabilities);
                actions = new Actions(driver);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @AfterScenario
    public void tearDown() {
        driver.quit();
    }

    public void initMap(List<File> fileList) {
        elementMapList = new ConcurrentHashMap<>();
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                try{
                    FileReader filez = new FileReader(file);
                    elementInfoList = gson
                            .fromJson(new FileReader(file), elementType);
                    elementInfoList.parallelStream()
                            .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
                    System.out.println(elementInfoList);

                }catch (NullPointerException ne){
                    System.out.println(ne.getMessage());
                }
            } catch (FileNotFoundException e) {

            }
        }
    }

/*    public void initMap(File[] fileList) {
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                elementInfoList = gson
                        .fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream()
                        .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
            } catch (FileNotFoundException e) {
                logger.warn("{} not found", e);
            }
        }
    }*/

    public List<File> getFileList(String directoryName) throws IOException {
    List<File> dirList = new ArrayList<>();
    try (Stream<Path> walkStream = Files.walk(Paths.get(directoryName))) {
        walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
            if (f.toString().endsWith(".json")) {
                logger.info(f.toFile().getName() + " adlı json dosyası bulundu.");
                dirList.add(f.toFile());
            }
        });
    }
    return dirList;
}

/*    public File[] getFileList() {
        File[] fileList = new File(
                this.getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_PATH).getFile())
                .listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".json"));
        if (fileList == null) {
            logger.warn(
                    "File Directory Is Not Found! Please Check Directory Location. Default Directory Path = {}",
                    DEFAULT_DIRECTORY_PATH);
            throw new NullPointerException();
        }
        return fileList;
    }*/

    /**
     * Set Chrome options
     *
     * @return the chrome options
     */
//    public ChromeOptions chromeOptions() {
//        chromeOptions = new ChromeOptions();
//        capabilities = DesiredCapabilities.chrome();
//        Map<String, Object> prefs = new HashMap<String, Object>();
//        prefs.put("profile.default_content_setting_values.notifications", 2);
//        chromeOptions.setExperimentalOption("prefs", prefs);
//        chromeOptions.addArguments("--kiosk");
//        chromeOptions.addArguments("--disable-notifications");
//        chromeOptions.addArguments("--start-fullscreen");
//        System.setProperty("webdriver.chrome.driver", "web_driver/chromedriver.exe");
//        chromeOptions.merge(capabilities);
//        return chromeOptions;
//    }

    /**
     * Set Firefox options
     *
     * @return the firefox options
     */
//    public FirefoxOptions firefoxOptions() {
//        firefoxOptions = new FirefoxOptions();
//        capabilities = DesiredCapabilities.firefox();
//        Map<String, Object> prefs = new HashMap<>();
//        prefs.put("profile.default_content_setting_values.notifications", 2);
//        firefoxOptions.addArguments("--kiosk");
//        firefoxOptions.addArguments("--disable-notifications");
//        firefoxOptions.addArguments("--start-fullscreen");
//        FirefoxProfile profile = new FirefoxProfile();
//        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
//        capabilities.setCapability("marionette", true);
//        firefoxOptions.merge(capabilities);
//        System.setProperty("webdriver.gecko.driver", "web_driver/geckodriver");
//        return firefoxOptions;
//    }

    public ElementInfo findElementInfoByKey(String key) {
        return (ElementInfo) elementMapList.get(key);
    }

    public void saveValue(String key, String value) {
        elementMapList.put(key, value);
    }

    public String getValue(String key) {
        return elementMapList.get(key).toString();
    }

}