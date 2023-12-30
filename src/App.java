import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.nio.file.*;

import exceptions.*;

public class App {
    public static void main(String[] args) throws Exception {

        WebDriver driver = new EdgeDriver(new EdgeOptions().addArguments("--headless"));

        driver.get("https://extranet.vizja.net/");

        WebElement login = driver.findElement(By.name("login"));
        WebElement password = driver.findElement(By.name("haslo"));

        try {
            List<String> data = Files.readAllLines(Paths.get("src\\credentials\\passwordAndLogin.txt"));
            login.sendKeys(data.get(0));
            password.sendKeys(data.get(1));
        } catch (Exception e) {
            driver.quit();
            throw new NoLoginDataException("Invalid data in passwordAndLogin file");
        }
        password.submit();

        if (driver.getCurrentUrl().equals("https://extranet.vizja.net/")) {
            driver.quit();
            throw new LoginFailureException("Invalid login credentials");
        }
        WebElement next = driver.findElement(By.cssSelector(
            "div.fc-button-group > button.fc-corner-right"));
        WebElement date = driver.findElement(By.cssSelector("div.fc-center > h2"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(50));
        WebElement timeTable = null;
        final int TIMEFRAME = 100;
        int days = 0;
        while (timeTable == null) {
            try {
                timeTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.fc-list-table")));
            } catch (Exception e) {
                next.click();
                if (days > TIMEFRAME) {
                    driver.quit();
                    throw new NoTimetableException("No timetable in specifiec timeframe.");
                }
                days += 1;
            }
        }

        System.out.println("Success! Printing...\n");
        System.out.println("data: " + date.getText());

        List<WebElement> lessons = timeTable.findElements(By.cssSelector("tr.fc-list-item"));
        for (WebElement lesson : lessons) {
            String time = lesson.findElement(By.cssSelector("td.fc-list-item-time")).getText();
            String subject = lesson.findElement(By.cssSelector("td.fc-list-item-title > a")).getText();
            System.out.println(subject + " " + time);
        }

        driver.quit();
    }
}
