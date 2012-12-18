package com.btmatthews.testapp;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.btmatthews.selenium.junit4.runner.SeleniumJUnit4ClassRunner;
import com.btmatthews.selenium.junit4.runner.SeleniumWebDriver;
import com.btmatthews.selenium.junit4.runner.WebDriverConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RunWith(SeleniumJUnit4ClassRunner.class)
@WebDriverConfiguration
public class ITestListUsers {

    @SeleniumWebDriver
    private WebDriver webDriver;

    @Test
    public void testListUsers() {
        webDriver.navigate().to("http://localhost:9080/users.html");
        assertEquals("List of Users", webDriver.getTitle());

        final WebElement usersTable = webDriver.findElement(By.id("users"));
        final List<WebElement> usersTableRows = usersTable.findElements(By.xpath("tbody/tr"));

        assertEquals(3, usersTableRows.size());
    }
}
