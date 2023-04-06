package net.electrifai.library.utils;

import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Test;
import io.cucumber.java.*;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import net.electrifai.library.pom.HomePage;
import net.electrifai.library.pom.LoginPage;
import net.electrifai.library.pom.SegmentationPage;
import org.apache.commons.configuration.ConfigurationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterTest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.electrifai.library.pom.HomePage.segmentName;

public class Hooks extends AbstractTestNGCucumberTests
{
   public SegmentationPage segmentationPage;
   public LoginPage loginPage;
   public HomePage basePage;
    public Hooks(LoginPage loginPage, HomePage basepage, SegmentationPage segmentationPage)
    {
       this.loginPage=loginPage;
       this.basePage=basepage;
       this.segmentationPage=segmentationPage;
    }
 

    @Before(order=0)
    public void beforeScenarioStart(Scenario scenario) throws ConfigurationException, IOException {
        LogManager.scenario=scenario;

       System.out.println("-----------------Start of Scenario-----------------");

        String featureName=  scenario.getUri().toString();
        String currentFeature = featureName.split(".*/")[1];
        System.out.println(currentFeature);
        String scenarioName=scenario.getName();
        ArrayList<String> ab=new ArrayList<String>();


        if(scenarioName.contains(","))
        {
            scenario.log(scenarioName.split(",")[0]);
        }else
        {
            scenario.log(scenarioName);
        }

        try
        {
//            if(!a.contains(currentFeature))
//            {
          List<Test> a = ThreadLocalManager.getExtent().getReport().getTestList();
             for(Test b:a)
             {
                 ab.add(b.getFullName());
             }

               Boolean s=ab.contains(currentFeature);
               if(!s)
               {
                ThreadLocalManager.setFeature(ThreadLocalManager.getExtent().createTest(new GherkinKeyword("Feature"), currentFeature));
//                a.add(currentFeature);
//                ThreadLocalManager.setFeaturesName(a);

               }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
           ThreadLocalManager.setScenario(ThreadLocalManager.getFeature().createNode(new GherkinKeyword("Scenario"), scenarioName));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @After(order=0)
    public void afterScenarioFinish(Scenario scenario) throws ConfigurationException, FileNotFoundException {
        System.out.println("-----------------End of Scenario-----------------");

        String featureName = scenario.getUri().toString();
        String currentFeature = featureName.split(".*/")[1];
        System.out.println(currentFeature);
        String scenarioName = scenario.getName();
        ArrayList<String> ab = new ArrayList<String>();


        if (scenarioName.contains(",")) {
            scenario.log(scenarioName.split(",")[0]);
        } else {
            scenario.log(scenarioName);
        }

        String environment = PropertiesFile.getProperty("testEnvironment.properties").getString("environment");
        String testUrl = PropertiesFile.getProperty("testEnvironment.properties").getString("" + environment + ".url");
        System.out.println(testUrl);
      //  ThreadLocalManager.getDriver().get(testUrl);
        //  WebElement logintitle = ThreadLocalManager.getDriver().findElement(By.xpath("//div[@class='login-card-title']"));

        String Url = PropertiesFile.getProperty("testEnvironment.properties").getString("" + environment + ".url");
        System.out.println(Url);
        ThreadLocalManager.getDriver().get(Url);
        List<WebElement> loginAsNonLearner1 = ThreadLocalManager.getDriver().findElements(By.xpath("(//div[@class='common-login-button'])[2]"));
        if (loginAsNonLearner1.size()==1) {
         LogManager.printInfoLog("Already in common login page");
        } else {
        loginPage.logout();
        ThreadLocalManager.getDriver().manage().deleteAllCookies();
    }
    }


   @BeforeStep
    public void beforeStep(Scenario scenario) throws ClassNotFoundException
   {
       System.out.println("Executing before Step");
   }

   @AfterStep
    public void afterStep(Scenario scenario) throws IOException
   {
       System.out.println("Executing after step ");

       if(scenario.isFailed()) {
           // scenario.attach(Screenshot.getBase64ScreenShot(),"image/png;base64", "Screenshot");

           scenario.attach(Screenshot.getScreenShot(), "image/png", "Screenshot");
           ThreadLocalManager.getStep().fail(MediaEntityBuilder.createScreenCaptureFromBase64String(Screenshot.getBase64ScreenShot()).build());


       }else
       {
           scenario.attach(Screenshot.getScreenShot(),"image/png", "Screenshot");
           ThreadLocalManager.getStep().pass(MediaEntityBuilder.createScreenCaptureFromBase64String(Screenshot.getBase64ScreenShot()).build());

       }


   }
    /*@After("@smoke")
    public void afterScanario(Scenario scenario) throws IOException{
        segmentationPage.doGivenActionOnGivenSegment("delete",segmentName);
    }*/
}