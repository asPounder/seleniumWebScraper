import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * The Scraper class provides methods for scraping timetable data from a website.
 */
public class Scraper {
    /**
     * Scrapes timetable data from a website.
     *
     * @param timeframe The number of days to look ahead in the timetable.
     * @param login     The login username.
     * @param password  The login password.
     * @param arg       Additional argument for the web driver, such as "--headless".
     * @return The timetable data scraped from the website.
     */
    public static TimetableData timetableScraper(int timeframe, String login, String password, String arg) {

        WebDriver driver;
        TimetableData output = new TimetableData();
        System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") + "/EDriver/msedgedriver.exe");
        if (arg == "--headless") {
            driver = new EdgeDriver(new EdgeOptions().addArguments(arg));
        } else {
            driver = new EdgeDriver();
        }

        try {

            driver.get("https://extranet.vizja.net/");

            WebElement loginEl = driver.findElement(By.name("login"));
            WebElement passwordEl = driver.findElement(By.name("haslo"));

            try {
                loginEl.sendKeys(login);
                passwordEl.sendKeys(password);
            } catch (Exception e) {
                throw new NotFoundException("login data not found;\nHint: edit the config.properties file");
            }
            passwordEl.submit();

            try {
                WebElement close = driver.findElement(By.className("ui-dialog-titlebar-close"));
                close.click();
            } catch (Exception e) {}
            
            if (driver.getCurrentUrl().equals("https://extranet.vizja.net/")) {
                throw new IllegalArgumentException("Invalid login credentials; Could not login with given credentials");
            }

            WebElement date = driver.findElement(By.cssSelector("div.fc-center > h2"));
            output.date = ConfigManager.formatToLocalDate(date.getText());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(50));
            WebElement next = driver.findElement(By.cssSelector(
                "div.fc-button-group > button.fc-corner-right"));
            WebElement timetable = null;
            int days = 0;
            while (timetable == null) {
                try {
                    timetable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.fc-list-table")));
                } catch (Exception e) {
                    next.click();
                    if (days > timeframe) {
                        throw new NoSuchElementException("No timetable in specifiec timeframe.");
                    }
                    days += 1;
                }
            }

            List<WebElement> lessons = timetable.findElements(By.cssSelector("tr.fc-list-item"));
            List<List<String>> tempTimetable = new ArrayList<>();
            for (WebElement lesson : lessons) {
                String subject = lesson.findElement(By.cssSelector("td.fc-list-item-title > a")).getText();
                String time = lesson.findElement(By.cssSelector("td.fc-list-item-time")).getText();
                tempTimetable.add(new ArrayList<>(List.of(subject, time)));
            }
            output.timetable = TimetableUtils.formatTimetable(tempTimetable);

            return output;

        } finally {
            driver.quit();
        }
    }
}
