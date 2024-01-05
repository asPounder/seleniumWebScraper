import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.*;


/*
 * TODO:
 * 1. add gui
 * 2. add caching
 * 3. add config
 * 4. add serialization!
 */

public class App {
    private static List<List<String>> timetableScraper(int timeframe, String login, String password, String arg) {

        //  ! first row contains date !

        List<List<String>> output = new ArrayList<>();

        System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") + "/EDriver/msedgedriver.exe");
        WebDriver driver = new EdgeDriver(new EdgeOptions().addArguments(arg));

        driver.get("https://extranet.vizja.net/");

        WebElement loginEl = driver.findElement(By.name("login"));
        WebElement passwordEl = driver.findElement(By.name("haslo"));

        try {
            loginEl.sendKeys(login);
            passwordEl.sendKeys(password);
        } catch (Exception e) {
            driver.quit();
            throw new NotFoundException("login data not found;\nHint: edit the config.properties file");
        }
        passwordEl.submit();

        if (driver.getCurrentUrl().equals("https://extranet.vizja.net/")) {
            driver.quit();
            throw new IllegalArgumentException("Invalid login credentials; Could not login with given credentials");
        }

        WebElement date = driver.findElement(By.cssSelector("div.fc-center > h2"));
        output.add(new ArrayList<>(List.of(date.getText())));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(50));
        WebElement next = driver.findElement(By.cssSelector(
            "div.fc-button-group > button.fc-corner-right"));
        WebElement timeTable = null;
        int days = 0;
        while (timeTable == null) {
            try {
                timeTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.fc-list-table")));
            } catch (Exception e) {
                next.click();
                if (days > timeframe) {
                    driver.quit();
                    throw new NoSuchElementException("No timetable in specifiec timeframe.");
                }
                days += 1;
            }
        }

        List<WebElement> lessons = timeTable.findElements(By.cssSelector("tr.fc-list-item"));
        for (WebElement lesson : lessons) {
            String time = lesson.findElement(By.cssSelector("td.fc-list-item-time")).getText();
            String subject = lesson.findElement(By.cssSelector("td.fc-list-item-title > a")).getText();
            output.add(new ArrayList<>(List.of(subject + " " + time)));
        }

        driver.quit();
        return output;
    }
    
    private static String tryToGet(String property, Properties config) {
        try {
            String value = config.getProperty(property);
            return value;
        } catch (Exception e) {
            throw new NoSuchElementException("Unable to fetch " + property);
        }
    }

    public static void main(String[] args) throws IOException {

        String[] headers = {"Time", "Subject"};
        List<List<String>> rows = new ArrayList<>();
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {

            config.load(input);
            LocalDate newestDate = LocalDate.parse(tryToGet("date", config));
            if (newestDate == null || newestDate.isBefore(LocalDate.now())) {
                String timeframe = tryToGet("timeframe", config);
                String login = tryToGet("login", config);
                String password = tryToGet("password", config);
                String arg = tryToGet("arg", config);
                List<List<String>> timetable = timetableScraper(Integer.parseInt(timeframe), login, password, arg);
            }
            else {

            }
            
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Could not locate the config.properties file");
        }
    }
}
