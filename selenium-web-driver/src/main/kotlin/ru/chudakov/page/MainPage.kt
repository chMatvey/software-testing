package ru.chudakov.page

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory

class MainPage(private val driver: WebDriver) : AbstractPage(driver) {
    override val pageUrl = "https://www.google.com/intl/ru_ru/slides/about/"

    @FindBy(xpath = "//*[@id=\"section-intro\"]/div[2]/div/div/a")
    lateinit var openPresentationButton: WebElement

    fun open() = driver.get(pageUrl)

    fun openPresentationButtonClick(): SignInPage {
        openPresentationButton.click();
        return SignInPage(driver)
    }
}
