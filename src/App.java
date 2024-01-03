import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.*;

import exceptions.*;

/*
 * TODO:
 * 1. add gui
 * 2. add caching
 * 3. add config
 */

public class App {
    public static List<List<String>> timetableScraper(
        int timeframe, String login, String password) {

        // - use the passed arguments
        // - move declarations here for clarity 
        // - rework exceptions
        List<List<String>> output = new ArrayList<>();

        System.setProperty("webdriver.edge.driver", System.getProperty("user.dir") + "/EDriver/msedgedriver.exe");
        WebDriver driver = new EdgeDriver(new EdgeOptions().addArguments("--headless"));

        driver.get("https://extranet.vizja.net/");

        WebElement loginEl = driver.findElement(By.name("login"));
        WebElement passwordEl = driver.findElement(By.name("haslo"));

        try {
            List<String> data = Files.readAllLines(Paths.get("src\\credentials\\passwordAndLogin.txt"));
            loginEl.sendKeys(data.get(0));
            passwordEl.sendKeys(data.get(1));
        } catch (Exception e) {
            driver.quit();
            throw new NoLoginDataException("Invalid data in passwordAndLogin file");
        }
        passwordEl.submit();

        if (driver.getCurrentUrl().equals("https://extranet.vizja.net/")) {
            driver.quit();
            throw new LoginFailureException("Invalid login credentials");
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
    public static void main(String[] args) {
        
    }
}
