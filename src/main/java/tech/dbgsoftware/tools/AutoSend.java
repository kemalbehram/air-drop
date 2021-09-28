package tech.dbgsoftware.tools;


import java.io.File;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AutoSend {

  private Logger logger = Logger.getLogger(AutoWeb.class.getName());

  private WebDriver driver;

  //發幣的賬號種子
  private String sed = "";
  //錢包密碼
  private String pwd = "";

  //發送的USDT數量
  private static String usdtNumber = "10";
  //發送的SOL數量
  private static String solNumber = "0.03";

  static ArrayList<String> walletSedList = new ArrayList<String>(300);


  private static void sleep(int second) {
    try {
      Thread.sleep(1500 * second);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void clickTillShowByXPathArray(WebDriver driver, String xPathName, int index)
      throws Exception {
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.xpath(xPathName)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        //driver.quit();
        throw new Exception();
      }
    }
    if (driver.findElements(By.xpath(xPathName)).size() != 0) {
      driver.findElements(By.xpath(xPathName)).get(index).click();
    }
    return;
  }

  public static void clickTillShowByClassArray(WebDriver driver, String className, int index) throws Exception{
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.className(className)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        //driver.quit();
        throw new Exception();
      }
    }
    driver.findElements(By.className(className)).get(index).click();
  }

  public static void sendUsdt(WebDriver driver, String address) throws Exception {

      //转账
      sleep(2);
      clickTillShowByClassArray(driver,"sc-hKFxyN",0);
      //点击USDT
      sleep(2);
      clickTillShowByClassArray(driver,"sc-dsXzNU",0);
      //填写地址
      sleep(1);
      driver.findElements(By.className("sc-iwajpm")).get(0)
          .sendKeys(address);
      //driver.findElements(By.className("ens-input__wrapper__input")).get(0).sendKeys(address);
      //填写发送额 10U
      sleep(1);
      driver.findElements(By.className("sc-iemWCZ")).get(0).sendKeys(usdtNumber);
      //下一步
      sleep(1);
      clickTillShowByClassArray(driver, "gDKYmt", 0);
      //确认
      sleep(2);
      clickTillShowByClassArray(driver, "gDKYmt", 0);

  }

  public static void sendSol(WebDriver driver, String address) throws Exception {
    //转账
    sleep(1);
    clickTillShowByClassArray(driver,"sc-hKFxyN",0);
    //点击SOL
    sleep(1);
    clickTillShowByClassArray(driver,"sc-dsXzNU",1);
    //填写地址
    sleep(1);
    //填写发送额 0.03SOL
    sleep(1);
    try {
      driver.findElements(By.className("sc-iwajpm")).get(0).sendKeys(address);
      driver.findElements(By.className("sc-iemWCZ")).get(0).sendKeys(solNumber);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //下一步
    sleep(1);
    clickTillShowByClassArray(driver, "gDKYmt", 0);
    //确认
    sleep(1);
    clickTillShowByClassArray(driver, "gDKYmt", 0);
  }


  private WebDriver initEnv() {
    try {
      //配置瀏覽器環境，配置錢包插件
      System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
      ChromeOptions chromeOptions = new ChromeOptions();
      chromeOptions.addExtensions(new File("C:\\driver\\phantom.crx"));
      driver = new ChromeDriver(chromeOptions);


      sleep(4);
      logger.info("切换到钱包导入界面");
      String title = driver.getTitle();
      if (!title.equals("Phantom Wallet")) {
        for (int i = 0; i < driver.getWindowHandles().size(); i++) {
          driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[i]));
          title = driver.getTitle();
          if (title.equals("Phantom Wallet")) {
            break;
          }
        }
      }

      sleep(1);
      logger.info("开始使用");
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div/div/section/button[2]", 0);
      sleep(1);
      logger.info("輸入助記詞");
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/section/div/textarea"))
          .sendKeys(sed);
      sleep(1);
      logger.info("确定");
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div[2]/div/form/button", 0);
      sleep(1);
      logger.info("输入密码");
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/section/input"))
          .sendKeys(pwd);
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/section/div[1]/input"))
          .sendKeys(pwd);
      sleep(1);
      clickTillShowByXPathArray(driver,
          "//*[@id=\"root\"]/main/div[2]/div/form/section/div[2]/span/input", 0);
      sleep(1);
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div[2]/div/form/button", 0);
      sleep(1);
      logger.info("继续");
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div[2]/div/button", 0);
      sleep(1);
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div[2]/div/button", 0);
      //打开钱包控制界面
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      driver.get("chrome-extension://bfnaelmomeimhlpmgjnjophhpkkoljpa/popup.html");

      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));


      sleep(5);
      return driver;
    } catch (Exception e) {

    }
    return null;

  }

  public static void main(String[] args)  {
    try {
      WebDriver driver = null;
      try {
        driver = new AutoSend().initEnv();
      } catch (Exception e) {
        driver.quit();
        e.printStackTrace();
      }
      if (driver == null) {
        throw new Exception();
      }
      List<String> address = Arrays.asList(
          "",
          "",
          ""
      );

      //转账
      WebDriver finalDriver = driver;
      address.forEach((s) -> {
        try {
          //发送USDT
          sendUsdt(finalDriver, s);
          sleep(12);
          //判断USDT发送成功没
          String text = null;
          for (int i = 0; i < 20; i++) {
            if (finalDriver.findElements(By.className("csoHSi")).get(0)
                .findElements(By.className("sc-jrsJWt")).get(0).getText() == null) {
              sleep(1);
            } else {
              break;
            }
            if (i == 19) {
              try {
                throw new Exception();
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
            text = finalDriver.findElements(By.className("csoHSi")).get(0)
                .findElements(By.className("sc-jrsJWt")).get(0).getText();

            if (!text.equals("Sent 10 USDT")) {
              finalDriver.switchTo()
                  .window(String.valueOf(finalDriver.getWindowHandles().toArray()[0]));
              finalDriver.quit();
            }



            finalDriver.switchTo()
                .window(String.valueOf(finalDriver.getWindowHandles().toArray()[0]));
            clickTillShowByXPathArray(finalDriver, "//*[@id=\"root\"]/div/div[2]/div[2]/a[1]", 0);

          //发送SOL


            sendSol(finalDriver, s);


          //判断SOL发送成功没
          sleep(15);
          String text1 = null;

          for (int i = 0; i < 30; i++) {
            if (finalDriver.findElements(By.className("csoHSi")).get(0)
                .findElements(By.className("sc-jrsJWt")).get(0).getText() == null) {
              sleep(1);
            } else {
              break;
            }
            if (i >=29) {
              try {
                throw new Exception();
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }

          text1 = finalDriver.findElements(By.className("csoHSi")).get(0)
              .findElements(By.className("sc-jrsJWt")).get(0).getText();

          if (!text1.equals("Sent 0.03 SOL")) {
            finalDriver.quit();
          }




          String title = finalDriver.getTitle();
          if (!title.equals("Phantom Wallet")) {
            for (int i = 0; i < finalDriver.getWindowHandles().size(); i++) {
              finalDriver.switchTo().window(String.valueOf(finalDriver.getWindowHandles().toArray()[i]));
              title = finalDriver.getTitle();
              if (title.equals("Phantom Wallet")) {
                break;
              }
            }
          }
          clickTillShowByClassArray(finalDriver,"gEDWsL",0);
        } catch (Exception e) {
          walletSedList.add(s);
          System.out.println(s);
          finalDriver.quit();
          e.printStackTrace();
        }
      });
    } catch (Exception e){
      e.printStackTrace();
    }
    walletSedList.forEach(System.out::println);

  }



}

