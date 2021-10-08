package tech.dbgsoftware.tools;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AutoSendSol1 {

  private Logger logger = Logger.getLogger(AutoWeb.class.getName());

  private WebDriver driver;

  //發幣的賬號種子
  private String sed = "render valid universe panic until industry load join network hood client match";
  //錢包密碼
  private String pwd = "ABMISYOURDADDY!";

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
    clickTillShowByClassArray(driver,"sc-dsXzNU",0);
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
  //切换浏览器页面到指定页面
  public static boolean switchToWindow(WebDriver driver, String windowTitle){
    boolean flag = false;
    try {
      String currentHandle = driver.getWindowHandle();
      Set<String> handles = driver.getWindowHandles();
      for (String s : handles) {
        if (s.equals(currentHandle))
          continue;
        else {
          driver.switchTo().window(s);
          if (driver.getTitle().contains(windowTitle)) {
            flag = true;
            System.out.println("Switch to window: "
                    + windowTitle + " successfully!");
            break;
          } else
            continue;
        }
      }
    } catch (NoSuchWindowException e) {
      System.out.println("Window: " + windowTitle
              + " cound not found!");
      flag = false;
    }
    return flag;
  }


  private WebDriver initEnv() {
    try {
      //配置瀏覽器環境，配置錢包插件
      System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
      ChromeOptions chromeOptions = new ChromeOptions();
      chromeOptions.addExtensions(new File("C:\\driver\\phantom.crx"));
      driver = new ChromeDriver(chromeOptions);
      //初始化一个空白页并记录他的windowHandle
      driver.get("chrome://newtab");
      String blankWindowHandle = driver.getWindowHandle();

      sleep(4);
      logger.info("切换到钱包导入界面");

      switchToWindow(driver, "Phantom Wallet");

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
      driver.switchTo().window(blankWindowHandle);
      driver.get("chrome-extension://bfnaelmomeimhlpmgjnjophhpkkoljpa/popup.html");
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      sleep(4);
      return driver;
    } catch (Exception e) {
      System.out.println("加载失败");
      driver.quit();
    }
    return null;

  }

  public static void main(String[] args)  {
    try {
      WebDriver driver = null;
      try {
        driver = new AutoSendSol1().initEnv();
      } catch (Exception e) {
        assert driver != null;
        driver.quit();
        e.printStackTrace();
      }
      if (driver == null) {
        throw new Exception();
      }
      List<String> address = Arrays.asList(
              "Eib9Qhv5rSx6HPWiUoWmG5w7nKJQuQKC6J3iXhaMRBDE",
              "2VA7WyUqajzDmKmuuRm5nhLYXy1abEwBNuvBiUXCc1qd"
      );
      sleep(10);
      //转账
      WebDriver finalDriver = driver;
      for (int j = 0; j < address.size(); j++) {
        String s = address.get(j);
        //取地址前四位
        String substring = s.substring(0, 4);
        try {
          //发送SOL
            sendSol(finalDriver, s);
          //判断SOL发送成功没
          sleep(15);
          String text1 = null;
          for (int i = 0; i < 30; i++) {
            //查看发送的地址是否正确
            if (finalDriver.findElements(By.className("csoHSi")).get(0)
                    .findElements(By.className("dPPazS")).get(0).getText().contains(substring)) {
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
            throw new Exception();
          }
          switchToWindow(finalDriver, "Phantom Wallet");
          sleep(1);
          clickTillShowByClassArray(finalDriver,"gEDWsL",0);
        } catch (Exception e) {
          walletSedList.add(s);
          System.out.println(s);
          finalDriver.quit();
          e.printStackTrace();
        }
      }
    } catch (Exception e){
      e.printStackTrace();
    }
    walletSedList.forEach(System.out::println);

    byte[] buff;
    try
    {

      FileOutputStream out=new FileOutputStream("d:\\bugWallet.txt", true);
      for (int i = 0; i < walletSedList.size(); i++) {
        buff = walletSedList.get(i).toString().getBytes();
        out.write("\"".getBytes());
        out.write(buff,0,buff.length);
        out.write("\"".getBytes());
        //out.write(",".getBytes());
        out.write("\r\n".getBytes());

      }
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}


