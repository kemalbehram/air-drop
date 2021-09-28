package tech.dbgsoftware.tools;


import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class AutoCreateWallet {



  private WebDriver driver;

  private Logger logger = Logger.getLogger(AutoCreateWallet.class.getName());
  String sed = null;

  static ArrayList<InnerWallet> walletList = new ArrayList<InnerWallet>(300){};

  static ArrayList<String> walletSedList = new ArrayList<String>(300);

  static ArrayList<String> walletAddressList = new ArrayList<String>(300);


  InnerWallet innerWallet = null;

  private static void sleep(int second) {
    try {
      Thread.sleep(1500 * second);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  //从剪切板获取文本
  public String getFromClipboard(){
    Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      try {
        return (String)transferable.getTransferData(DataFlavor.stringFlavor);
      }catch (Exception e){
        return " ";
      }
    }
    return " ";
  }

  public static void clickTillShowByXPathArray(WebDriver driver, String xPathName, int index) {
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.xpath(xPathName)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        driver.quit();
        return;
      }
    }
    if (driver.findElements(By.xpath(xPathName)).size() != 0) {
      driver.findElements(By.xpath(xPathName)).get(index).click();
    }
  }




  private boolean initEnv() {
    try {
      System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
      ChromeOptions chromeOptions = new ChromeOptions();
      chromeOptions.addExtensions(new File("C:\\driver\\phantom.crx"));
      driver = new ChromeDriver(chromeOptions);
      //LogUtils.info("打开挖矿页面");
      driver.get("https://solend.fi/dashboard");
      sleep(5);
      //LogUtils.info("切换到钱包导入界面");
      driver.navigate().refresh();
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[1]));
      //LogUtils.info("开始使用");
      //创建钱包
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/main/div/div/section/button[1]",0);
      sleep(2);
      //获取助记词
      try {
        sed = driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/section/div/textarea")).getText();
      }catch (Exception e){
        return true;
      }

      //点击确定
      clickTillShowByXPathArray(driver,"//*[@id=\"root\"]/main/div[2]/div/form/button",0);
      sleep(1);
      //输入账号密码，勾选，保存
      driver.findElement(By.name("password.first")).sendKeys("ABMISYOURDADDY!");
      driver.findElement(By.name("password.confirm")).sendKeys("ABMISYOURDADDY!");
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/section/div[2]/span/input")).click();
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/form/button")).click();
      //继续
      Thread.sleep(500);
      driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/button")).click();
      //结束
      Thread.sleep(500);
      try {
        driver.findElement(By.xpath("//*[@id=\"root\"]/main/div[2]/div/button")).click();
      }catch (Exception e){

      }

      //打开SOlend
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      clickTillShowByXPathArray(driver,"//*[@id=\"root\"]/section/main/div/div/div[2]/div/div/button",0);
      sleep(2);
      clickTillShowByXPathArray(driver,"/html/body/div[2]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[1]/div",0);
      Thread.sleep(2000);
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[1]));
      clickTillShowByXPathArray(driver,"//*[@id=\"root\"]/div/section/div[2]/p[2]",0);
      Thread.sleep(1000);
      String address = getFromClipboard();
      innerWallet = new InnerWallet(sed, address);
      walletList.add(innerWallet);
      System.out.println(innerWallet.toString());


      driver.quit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      driver.quit();
      return false;
    }
  }
  public static void main(String[] args) {
    int taskNum = 2;
    CountDownLatch countDownLatch = new CountDownLatch(taskNum);
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    for (int i = 0; i < taskNum; i++) {
      executorService.execute(() -> {
        try {
          while (!new AutoCreateWallet().initEnv()) {
            sleep(1);
          }
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    try {
      if (countDownLatch.await(1, TimeUnit.HOURS)) {
        walletList.forEach((s) -> {
          walletSedList.add(s.getSed());
          walletAddressList.add(s.getAddress());
        });
      }
      walletSedList.forEach((s) -> System.out.println("\""+s+"\""+","));
      walletAddressList.forEach((s) -> System.out.println("\""+s+"\""+","));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}