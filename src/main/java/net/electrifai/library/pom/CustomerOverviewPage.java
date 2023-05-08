package net.electrifai.library.pom;

import io.cucumber.java.sl.In;
import net.electrifai.library.utils.DBUtils;
import net.electrifai.library.utils.GenericPageActions;
import net.electrifai.library.utils.LogManager;
import net.electrifai.library.utils.Wait;
import net.electrifai.library.utils.excelsheet.ReadAndWriteExcel;
import org.apache.xmlbeans.impl.values.XmlIntegerRestriction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.net.ConnectException;
import java.text.DecimalFormat;
import java.util.*;

import static net.electrifai.library.utils.DriverSetUp.driver;

public class CustomerOverviewPage extends HomePage {
    String excelFilePath = projectPath + property.getString("testDataPath");
    public static Map<String, String> data = null;
    ReportLandingPage reportLandingPage;

    @FindBy(xpath = "//span[contains(text(),'My Products')]")
    WebElement myProduct_Heading;
    @FindBy(xpath = "//input[@placeholder='Search for products']")
    WebElement searchProduct;
    @FindBy(xpath = "//div[@class='productItem_productName__m8VQb']")
    List<WebElement> product_Name;
    @FindBy(xpath = "//div[@class='component_title__koRQ4']")
    WebElement cross_sell_text;

    public List<String> getUIProducts() {
        List<String> UIList = new ArrayList<>();
        try {
            for (WebElement product : product_Name) {
                String products = product.getText();
                UIList.add(products);
            }
            LogManager.printInfoLog("The product list from UI is: " + UIList);
        } catch (Exception e) {
            e.printStackTrace();
            String logMessage = "The product list is not correct.";
            LogManager.printExceptionLog(e, logMessage);
        }
        return UIList;
    }

    public void getAndCompareProductName() {
        List<String> dbProducts = DBUtils.getGivenColumnValueFromStatement("SELECT product_name FROM customerengagementqa.products;", "product_name");
        LogManager.printInfoLog("The product list from DB is: " + dbProducts);
        GenericPageActions.compareGivenLists(getUIProducts(), "uiProductList", dbProducts, "dbProductList");
    }

    public void getProductName(String fileName, String sheetName, String dataRowNum) {
        fileName = "" + fileName + ".xlsx";
        data = ReadAndWriteExcel.readExcelTabRowNew(excelFilePath, fileName, sheetName, dataRowNum);
        List<String> productList = Arrays.asList(data.get("product name").split("\\|"));
        getUIProducts();
        try {
            Assert.assertEquals(productList, getUIProducts());
            LogManager.printInfoLog("The product list matches and are equal");
        } catch (Exception e) {
            e.printStackTrace();
            String logMessage = "The product list doesn't match";
            LogManager.printExceptionLog(e, logMessage);
        }


    }

    public void searchProduct(String fileName, String sheetName, String dataRowNum) {
        //validate the heading of the column
        GenericPageActions.isElementDisplayedWithExpectedText(myProduct_Heading, "My Products", "My Products");
        try {
            fileName = "" + fileName + ".xlsx";
            data = ReadAndWriteExcel.readExcelTabRowNew(excelFilePath, fileName, sheetName, dataRowNum);
            String productSearch = data.get("product name");
            Actions actions = new Actions(driver);
            actions.sendKeys(searchProduct, productSearch).perform();
            for (WebElement product : product_Name) {
                String products = product.getText();
                Assert.assertTrue(products.contains(productSearch));
                LogManager.printInfoLog("The Searched product is: " + products);
            }
            searchProduct.clear();
            searchProduct.clear();
            LogManager.printInfoLog("The search bar is cleared");
        } catch (Exception e) {
            e.printStackTrace();
            String logMessage = "The product name doesn't match";
            LogManager.printExceptionLog(e, logMessage);
        }
    }

    public void reachCustomerLink(String productName) {
        try {
            GenericPageActions.refreshPageViaJavascript();
            WebElement link = driver.findElement(By.xpath("//div[@title='" + productName + "']/ancestor::div[@class='productItem_top__y_f_w']/following-sibling::div[2]"));
            GenericPageActions.scrollToElementView(link);
            Assert.assertTrue(link.getText().equals("Reach to potential customers"));
            GenericPageActions.click(link, "Reach to potential customer link");
            Wait.explicitWaitTextVerification(cross_sell_text, "Select below filters to extract the results");
            GenericPageActions.isElementDisplayed(cross_sell_text, "heading is displayed");
            verifyUserLandedOnGivenPage("Product Cross-Sell");
        } catch (Exception e) {
            e.printStackTrace();
            String logMessage = "Reach to potential customer link not correct";
            LogManager.printExceptionLog(e, logMessage);
        }

    }

    public void getDataCountFromProductWidget() {
        for (WebElement product : product_Name) {
            String products = product.getText();
            WebElement activeCustomerData = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']/following-sibling::div/div[1]"));
            WebElement inactiveCustomerData = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']/following-sibling::div/div[2]"));
            WebElement percentage = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']//div[@class='productItem_percent__mYeBG']/span[1]"));
            //get string values for active and inactive customers
            String data1 = activeCustomerData.getText();
            String data2 = inactiveCustomerData.getText();
            //get string for percentage
            String percentageChange = percentage.getText();
            //split string values ie, number and string
            String activeCountString = data1.split("\\n")[0];
            String inactiveCountString = data2.split("\\n")[0];
            String countType1 = data1.split("\\n")[1];
            String countType2 = data2.split("\\n")[1];
            //split percentage string and convert to integer
            String changeValue = percentageChange.split("\\%")[0];
            try {
                //validate if it's an integer
                int activeCount = Integer.parseInt(activeCountString.replaceAll(",", ""));
                int inactiveCount = Integer.parseInt(inactiveCountString.replaceAll(",", ""));
                //convert percentage change float to integer
                double floatValue = Double.parseDouble(changeValue);
                int perValue = (int) floatValue;
                LogManager.printInfoLog("The count for: " + products + " is " + activeCount + " and the type is: " + countType1);
                LogManager.printInfoLog("The count for: " + products + " is " + inactiveCount + " and the type is: " + countType2);
                LogManager.printInfoLog("The percentage value for: " + products + " is " + perValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                String logMessage = "The number is not an integer..";
                LogManager.printExceptionLog(e, logMessage);
            }

        }
    }

    public Map<String, String> fetchActiveCustomerFromUI() {
        Map<String, String> activeCustomerKeyPairValue = null;
        activeCustomerKeyPairValue = new HashMap<>();
        try {
            for (WebElement product : product_Name) {
                String products = product.getText();
                WebElement activeCountElement = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']/following-sibling::div/div[1]/div[1]"));
                activeCustomerKeyPairValue.put(products, activeCountElement.getText());
            }
            LogManager.printInfoLog("The active customer count from UI is: " + activeCustomerKeyPairValue);
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
            String logMessage = "Element is not found.";
            LogManager.printExceptionLog(e, logMessage);
        }
        return activeCustomerKeyPairValue;
    }

    public Map<String, String> fetchActiveCustomerCountFromDB() {
        List<Map<String, String>> activeCustomerFromDB = DBUtils.getDataFromStatement("select prod.product_name,cust_prod.active_customer_count from customerengagementqa.products prod inner join \n" +
                "customer_products_aggregated cust_prod on(prod.product_id=cust_prod.product_id);");
        Map<String, String> countFromDB = new HashMap();
        try {
            for (Map<String, String> temp : activeCustomerFromDB) {
                countFromDB.put(temp.get("product_name"), temp.get("active_customer_count"));
            }
            LogManager.printInfoLog("The active customer count from DB is: " + countFromDB);
        }
        catch (Exception e) {
            e.printStackTrace();
            String logMessage = "Active Customer Data not fetched from DB ";
            LogManager.printExceptionLog(e, logMessage);
        }
        return countFromDB;
    }

    public void validateActiveCustomerCountForProduct() {
        Map<String, String> activeCountUI = fetchActiveCustomerFromUI();
        Map<String, String> activeCountDB = fetchActiveCustomerCountFromDB();
        for (WebElement product : product_Name) {
            String products = product.getText();
            try {
                Assert.assertEquals(activeCountUI.get(products), activeCountDB.get(products), "The active customer validation failed for: " + products);
            }
            catch (Exception e) {
                e.printStackTrace();
                String logMessage = "Active Customer Data doesn't not match ";
                LogManager.printExceptionLog(e, logMessage);
            }
        }

        LogManager.printInfoLog("The Active customer count validation successful.");
    }

    public Map<String, String> fetchLastYearCustomerCountFromDB() {
        List<Map<String, String>> lastYearCustomerCountFromDB = DBUtils.getDataFromStatement("select prod.product_name,\n" +
                "cust_prod.customer_count_last_year from customerengagementqa.products prod inner join \n" +
                "customer_products_aggregated cust_prod on(prod.product_id=cust_prod.product_id);");
        Map<String, String> lastYearCountFromDB = new HashMap();
        try {
            for (Map<String, String> temp : lastYearCustomerCountFromDB) {
                lastYearCountFromDB.put(temp.get("product_name"), temp.get("customer_count_last_year"));
            }
            LogManager.printInfoLog("The last year customer count from DB is: " + lastYearCountFromDB);
        }
        catch (Exception e) {
            e.printStackTrace();
            String logMessage = "Last Year Customer Data not fetched from DB ";
            LogManager.printExceptionLog(e, logMessage);
        }
        return lastYearCountFromDB;
    }


    public Map<String, String> fetchInactiveCustomerFromUI() {
        Map<String, String> inactiveCustomerKeyPairValue = null;
        inactiveCustomerKeyPairValue = new HashMap<>();
        try {
            for (WebElement product : product_Name) {
                String products = product.getText();
                WebElement inactiveCountElement = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']/following-sibling::div/div[2]/div[1]"));
                inactiveCustomerKeyPairValue.put(products, inactiveCountElement.getText());
            }
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
            String logMessage = "Inactive Customer Data not fetched from UI ";
            LogManager.printExceptionLog(e, logMessage);
        }
        LogManager.printInfoLog("The inactive customer count from UI is: " + inactiveCustomerKeyPairValue);
        return inactiveCustomerKeyPairValue;
    }

    public void validateInactiveCustomerCount() {
        Map<String, String> lastYearCount = fetchLastYearCustomerCountFromDB();
        Map<String, String> activeCountDB = fetchActiveCustomerCountFromDB();
        Map<String, String> inactiveCountDB = fetchInactiveCustomerFromUI();
        for (WebElement product : product_Name) {
            String products = product.getText();
            Integer inactiveCustomerCountDB = Integer.parseInt(activeCountDB.get(products).replaceAll(",", "")) - Integer.parseInt(lastYearCount.get(products).replaceAll(",", ""));
            LogManager.printInfoLog("the Difference from DB value is: " + inactiveCustomerCountDB);
            Integer inactiveCustomerCountUI = Integer.parseInt(inactiveCountDB.get(products).replaceAll(",", ""));
            LogManager.printInfoLog("the inactive value from UI is: " + inactiveCustomerCountDB);
            try{
            Assert.assertEquals(inactiveCustomerCountDB, inactiveCustomerCountUI, "The Inactive count failed for: " + products);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                String logMessage = "Active Customer Data not fetched from DB ";
                LogManager.printExceptionLog(e, logMessage);
            }
        }

        LogManager.printInfoLog("The Inactive customer count validation successful.");

    }


    public void validatePercentageCount() {
        Map<String, String> lastYearCountDB = fetchLastYearCustomerCountFromDB();
        Map<String, String> inactiveCountUI = fetchInactiveCustomerFromUI();
        for (WebElement product : product_Name) {
            String products = product.getText();
            double inactiveCount = Double.parseDouble(inactiveCountUI.get(products).replaceAll(",", ""));
            double lastYearCount = Double.parseDouble(lastYearCountDB.get(products).replaceAll(",", ""));
            double percentageCalculated = (inactiveCount / lastYearCount) * 100;
            DecimalFormat df = new DecimalFormat("#.#");
            double formattedPercentage = Double.parseDouble(df.format(percentageCalculated));
            LogManager.printInfoLog("The percentage Calculated is: " + formattedPercentage);
            //get percentage Values from UI
            String percentageStringFromUI = driver.findElement(By.xpath("//div[@title='" + products + "']/ancestor::div[@class='productItem_top__y_f_w']/div[2]/div[2]/span[1]")).getText();
            // String percentageChange = percentageStringFromUI.getText();
            String splitPercentageValue = percentageStringFromUI.split("\\%")[0];
            double percentageUI = Double.parseDouble(splitPercentageValue);
            LogManager.printInfoLog("The percentage change from UI is: " + percentageUI);
            try {
                Assert.assertEquals(formattedPercentage, percentageUI, "The percentage change is incorrect for: " + products);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                String logMessage = "Active Customer Data not fetched from DB ";
                LogManager.printExceptionLog(e, logMessage);
            }
        }
        LogManager.printInfoLog("The percentage change for customer count validation successful.");
    }

}











