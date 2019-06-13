package ru.chudakov

import io.appium.java_client.MobileDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.TouchAction
import io.appium.java_client.android.Activity
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidElement
import io.appium.java_client.android.AndroidTouchAction
import io.appium.java_client.remote.MobileCapabilityType
import io.appium.java_client.touch.offset.ElementOption
import io.appium.java_client.touch.offset.PointOption
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.By
import org.openqa.selenium.Point
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.touch.TouchActions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.WebDriverWait
import ru.chudakov.pages.AuthorizationPage
import ru.chudakov.pages.HomePage
import ru.chudakov.pages.RegistrationPage
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterTest {
    private val driver: AndroidDriver<MobileElement>
    private val wait: WebDriverWait

    init {
        val capabilities = DesiredCapabilities()

        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Custom Phone")
        capabilities.setCapability(MobileCapabilityType.UDID, "192.168.147.105:5555")
        //capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "uiautomator2")
        capabilities.setCapability("appPackage", "com.twitter.android")
        capabilities.setCapability("appActivity", "com.twitter.app.main.MainActivity")
        //capabilities.setCapability("appWaitActivity","com.twitter.app.main.MainActivity")

        driver = AndroidDriver(URL("http://127.0.0.1:4723/wd/hub"), capabilities)
        wait = WebDriverWait(driver, 10)
    }

    @Test
    fun createAccount() {
        val registrationPage = RegistrationPage(driver)

        driver.startActivity(Activity("com.twitter.android", "LoginActivity"))

        registrationPage.run {
            wait.until { signUpButton.isDisplayed }
            signUpButton.click()

            wait.until { nameInput.isDisplayed }
            nameInput.sendKeys("Matvey")

            phoneNumberInput.sendKeys("1")
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
            assertFalse { nextButton.isEnabled }

            phoneNumberInput.clear()
            phoneNumberInput.sendKeys(System.getenv("PHONE_NUMBER"))
            wait.until { nextButton.isDisplayed }

            wait.until { emailInsteadButton.isDisplayed }
            emailInsteadButton.click()
            wait.until { emailInput.isDisplayed }

            emailInput.sendKeys("1")
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
            assertFalse { nextButton.isEnabled }

            emailInput.clear()
            emailInput.sendKeys(System.getenv("EMAIL"))
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
            wait.until { nextButton.isDisplayed }
        }
    }

    @Test
    fun login() {
        val authorizationPage = AuthorizationPage(driver)

        driver.startActivity(Activity("com.twitter.android", "LoginActivity"))

        authorizationPage.run {
            wait.until { resetPasswordButton.isDisplayed }
            resetPasswordButton.click()

            wait.until { navigateUp.isDisplayed }
            navigateUp.click()

            wait.until { loginInput.isDisplayed }
            loginInput.sendKeys("1")
            passwordInput.sendKeys("1")
            loginButton.click()

            wait.until { loginButton.isDisplayed }
            loginInput.clear()
            loginInput.sendKeys(System.getenv("PHONE_NUMBER"))
            passwordInput.clear()
            passwordInput.sendKeys(System.getenv("TWITTER_PASSWORD"))
            loginButton.click()

            wait.until { driver.findElement(By.id("com.twitter.android:id/toolbar")) != null }

            assertNotEquals("LoginActivity", driver.currentActivity())
        }
    }

    @Test
    fun tweetList() {
        //driver.startActivity(Activity("com.twitter.app.main", "MainActivity"))

        val homePage = HomePage(driver)

        val action = TouchAction<AndroidTouchAction>(driver)

        homePage.run {
            wait.until { homePage.tweetCurationAction.isDisplayed }
            tweetCurationAction.click()
            action.press(ElementOption())
            action.release()
            action.perform()
        }
    }

    @AfterAll
    fun afterAll() {
        driver.quit()
    }
}