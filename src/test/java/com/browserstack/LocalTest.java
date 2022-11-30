package com.browserstack;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LocalTest extends SeleniumTest {

    @Test
    public void test() throws Exception {
        driver.get("http://bs-local.com:45454/");

        Assert.assertTrue(driver.getTitle().contains("BrowserStack Local"));
    }
}
