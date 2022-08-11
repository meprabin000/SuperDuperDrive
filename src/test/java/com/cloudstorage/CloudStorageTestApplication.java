package com.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageTestApplication {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    public void getLoginPage() {
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    private void doMockSignUp(String firstName, String lastName, String userName, String password){
        // Create a dummy account for logging in later.

        // Visit the sign-up page.
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);
        driver.get("http://localhost:" + this.port + "/signup");
        webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));

        // Fill out credentials
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
        WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
        inputFirstName.click();
        inputFirstName.sendKeys(firstName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
        WebElement inputLastName = driver.findElement(By.id("inputLastName"));
        inputLastName.click();
        inputLastName.sendKeys(lastName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        WebElement inputUsername = driver.findElement(By.id("inputUsername"));
        inputUsername.click();
        inputUsername.sendKeys(userName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        WebElement inputPassword = driver.findElement(By.id("inputPassword"));
        inputPassword.click();
        inputPassword.sendKeys(password);

        // Attempt to sign up.
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
        WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
        buttonSignUp.click();

		/* Check that the sign up was successful.
		// You may have to modify the element "success-msg" and the sign-up
		// success message below depening on the rest of your code.
		*/
        webDriverWait.until(ExpectedConditions.titleContains("Login"));
        Assertions.assertTrue(driver.findElement(By.id("successMsg")).getText().contains("You successfully signed up!"));
    }

    private void doLogIn(String userName, String password) {
        // Log in to our dummy account.
        driver.get("http://localhost:" + this.port + "/login");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        WebElement loginUserName = driver.findElement(By.id("inputUsername"));
        loginUserName.click();
        loginUserName.sendKeys(userName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        WebElement loginPassword = driver.findElement(By.id("inputPassword"));
        loginPassword.click();
        loginPassword.sendKeys(password);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        loginButton.click();

        webDriverWait.until(ExpectedConditions.titleContains("Home"));

    }

    public void createNote(String title, String description) {
        doMockSignUp("FirstnameDummy", "LastNameDummy", title, description);
        doLogIn(title, description);

        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        // navigate to the notes tab
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addANewNoteButton")));
        WebElement addNoteButton = driver.findElement(By.id("addANewNoteButton"));
        addNoteButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
        WebElement inputNoteTitle = driver.findElement(By.id("note-title"));
        inputNoteTitle.click();
        inputNoteTitle.sendKeys(title);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
        WebElement inputNoteDescription = driver.findElement(By.id("note-description"));
        inputNoteDescription.click();
        inputNoteDescription.sendKeys(description);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteSaveChangesButton")));
        WebElement buttonNoteSaveChanges = driver.findElement(By.id("noteSaveChangesButton"));
        buttonNoteSaveChanges.click();
    }

    public void createCredential(String url, String username, String password) {
        doMockSignUp("FirstnameDummy", "LastNameDummy", url, password);
        doLogIn(url, password);

        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        // navigate to the credentials tab
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addANewCredentialButton")));
        WebElement addCredentialButton = driver.findElement(By.id("addANewCredentialButton"));
        addCredentialButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        WebElement inputCredentialUrl = driver.findElement(By.id("credential-url"));
        inputCredentialUrl.click();
        inputCredentialUrl.sendKeys(url);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
        WebElement inputCredentialUsername = driver.findElement(By.id("credential-username"));
        inputCredentialUsername.click();
        inputCredentialUsername.sendKeys(username);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
        WebElement inputCredentialPassword = driver.findElement(By.id("credential-password"));
        inputCredentialPassword.click();
        inputCredentialPassword.sendKeys(password);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSaveChangesButton")));
        WebElement buttonCredentialSaveChanges = driver.findElement(By.id("credentialSaveChangesButton"));
        buttonCredentialSaveChanges.click();
    }

    @Test
    public void testRedirection() {
        // Create a test account
        doMockSignUp("Redirection","Test","RT","123");

        // Check if we have been redirected to the log in page.
        Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
    }

    @Test
    public void testBadUrl() {
        // Create a test account
        doMockSignUp("URL","Test","UT","123");
        doLogIn("UT", "123");

        // Try to access a random made-up URL.
        driver.get("http://localhost:" + this.port + "/some-random-page");
        Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }


    @Test
    public void testLargeUpload() {
        // Create a test account
        doMockSignUp("Large File","Test","LFT","123");
        doLogIn("LFT", "123");

        // Try to upload an arbitrary large file
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        String fileName = "largeFileTest.dmg";

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
        WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
        fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

        WebElement uploadButton = driver.findElement(By.id("uploadButton"));
        uploadButton.click();
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Large File upload failed");
        }
        Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

    }

    @Test
    public void testUnauthorizedAccess() {
        List<String> allNonAuthorizedEndPoints = new ArrayList<>(Arrays.asList("/home", "/addCredential", "/deleteCredential", "/note", "/deleteNote", "/addFile", "/deleteFile"));

        for(String endPoint: allNonAuthorizedEndPoints) {
            driver.get("http://localhost:" + this.port + endPoint);
            Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
        }
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertTrue(driver.getPageSource().contains("Login"));

        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertTrue(driver.getPageSource().contains("Sign Up"));

    }

    @Test
    public void testHomePageAuthorizedAccess() {
        doMockSignUp("testHomePageAuthorizedAccess", "testHomePageAuthorizedAccess", "testHomePageAuthorizedAccess", "testHomePageAuthorizedAccess");
        doLogIn("testHomePageAuthorizedAccess", "testHomePageAuthorizedAccess");

        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertTrue(driver.getPageSource().contains("Home"));

        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        webDriverWait.until(ExpectedConditions.titleContains("Home"));

        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();

        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertFalse(driver.getPageSource().contains("Home"));
    }

    @Test
    public void testCreateNote() {
        createNote("testCreateNote title", "testCreateNote description");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        Assertions.assertTrue(driver.getPageSource().contains("testCreateNote title"));
        Assertions.assertTrue(driver.getPageSource().contains("testCreateNote description"));
    }

    @Test
    public void testEditNote() {
        createNote("Title edit", "Description edit");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        String idOfNewElementRow = driver.findElement(By.xpath("//*[text()='Title edit']//ancestor::tr")).getAttribute("id");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Title edit']//ancestor::tr//*[contains(@class, 'editNoteButton')]")));
        WebElement editNoteButton = driver.findElement(By.xpath("//*[text()='Title edit']//ancestor::tr//*[contains(@class, 'editNoteButton')]"));
        editNoteButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
        WebElement inputNoteTitle = driver.findElement(By.id("note-title"));
        inputNoteTitle.click();
        inputNoteTitle.clear();
        inputNoteTitle.sendKeys("title edit changed");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
        WebElement inputNoteDescription = driver.findElement(By.id("note-description"));
        inputNoteDescription.click();
        inputNoteDescription.clear();
        inputNoteDescription.sendKeys("description edit changed");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteSaveChangesButton")));
        WebElement buttonNoteSaveChanges = driver.findElement(By.id("noteSaveChangesButton"));
        buttonNoteSaveChanges.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        // verifies that the changes are displayed.
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))));
        Assertions.assertEquals("title edit changed",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))).getText());
        Assertions.assertEquals("description edit changed",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::td[2]", idOfNewElementRow))).getText());
    }

    @Test
    public void testDeleteNote() {
        createNote("Title delete", "Description delete");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        WebElement notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Title delete']//ancestor::tr//*[contains(@class,'deleteNoteButton')]")));
        WebElement deleteButton = driver.findElement(By.xpath("//*[text()='Title delete']//ancestor::tr//*[contains(@class,'deleteNoteButton')]"));
        deleteButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        notesTabButton = driver.findElement(By.id("nav-notes-tab"));
        notesTabButton.click();

        Assertions.assertFalse(driver.getPageSource().contains("Title delete"));
        Assertions.assertFalse(driver.getPageSource().contains("Description delete"));
    }

    @Test
    public void testCreateCredential() {
        createCredential("Url create", "Username create", "Password create");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();

        String idOfNewElementRow = driver.findElement(By.xpath("//*[text()='Url create']//ancestor::tr")).getAttribute("id");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))));

        //verifies that the created credentials are displayed
        Assertions.assertEquals("Url create",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))).getText());
        Assertions.assertEquals("Username create",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::td[2]", idOfNewElementRow))).getText());

        // verifies that the password is encrypted
        Assertions.assertNotEquals("Password create",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::td[3]", idOfNewElementRow))).getText());
    }

    @Test
    public void testEditCredential() {
        createCredential("Url edit", "Username edit", "Password edit");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();

        String idOfNewElementRow = driver.findElement(By.xpath("//*[text()='Url edit']//ancestor::tr")).getAttribute("id");

        // verifies that the displayed password is encrypted
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Url edit']//ancestor::tr//child::td[3]")));
        WebElement passwordElement = driver.findElement(By.xpath("//*[text()='Url edit']//ancestor::tr//child::td[3]"));
        Assertions.assertNotEquals(passwordElement.getText(), "Password edit");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Url edit']//ancestor::tr//*[contains(@class,'editButton')]")));
        WebElement editCredentialButton = driver.findElement(By.xpath("//*[text()='Url edit']//ancestor::tr//*[contains(@class,'editButton')]"));
        editCredentialButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        WebElement inputCredentialUrl = driver.findElement(By.id("credential-url"));
        inputCredentialUrl.click();
        inputCredentialUrl.clear();
        inputCredentialUrl.sendKeys("url edit changed");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
        WebElement inputCredentialUsername = driver.findElement(By.id("credential-username"));
        inputCredentialUsername.click();
        inputCredentialUsername.clear();
        inputCredentialUsername.sendKeys("username edit changed");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
        WebElement inputCredentialPassword = driver.findElement(By.id("credential-password"));
        // verifies that the viewable password is unencrypted
        Assertions.assertEquals(inputCredentialPassword.getAttribute("value"), "Password edit");
        inputCredentialPassword.click();
        inputCredentialPassword.clear();
        inputCredentialPassword.sendKeys("password edit changed");

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialSaveChangesButton")));
        WebElement buttonCredentialSaveChanges = driver.findElement(By.id("credentialSaveChangesButton"));
        buttonCredentialSaveChanges.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();

        // verifies that the changes are displayed.
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))));
        Assertions.assertEquals("url edit changed",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::th", idOfNewElementRow))).getText());
        Assertions.assertEquals("username edit changed",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::td[2]", idOfNewElementRow))).getText());
        Assertions.assertNotEquals("password edit changed",driver.findElement(By.xpath(String.format("//tr[@id='%s']//child::td[3]", idOfNewElementRow))).getText());
    }

    @Test
    public void testDeleteCredential() {
        createCredential("Url delete", "Username delete", "Password delete");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        WebElement credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();


        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Url delete']//ancestor::tr//*[contains(@class,'deleteCredentialButton')]")));
        WebElement deleteButton = driver.findElement(By.xpath("//*[text()='Url delete']//ancestor::tr//*[contains(@class,'deleteCredentialButton')]"));
        deleteButton.click();

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
        credentialTabButton = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabButton.click();

        Assertions.assertFalse(driver.getPageSource().contains("Url delete"));
        Assertions.assertFalse(driver.getPageSource().contains("Username delete"));
    }

}