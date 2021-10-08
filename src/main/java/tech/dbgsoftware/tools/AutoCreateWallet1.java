package tech.dbgsoftware.tools;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class AutoCreateWallet1 {



  private WebDriver driver;

  private Logger logger = Logger.getLogger(AutoCreateWallet1.class.getName());
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
      driver.get("https://solend.fi/dashboard");
      sleep(3);
      //LogUtils.info("切换到钱包导入界面");
      for (int i = 0; i < driver.getWindowHandles().size(); i++) {
        sleep(1);
        driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[i]));
        sleep(1);
        if (driver.getTitle().equals("Phantom Wallet")){
          break;
        }
      }
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
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      driver.get("chrome-extension://bfnaelmomeimhlpmgjnjophhpkkoljpa/popup.html");
      for (int i = 0; i < driver.getWindowHandles().size(); i++) {
        sleep(1);
        driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[i]));
        sleep(1);
        if (driver.getTitle().equals("Phantom Wallet")){
          break;
        }
      }
      clickTillShowByXPathArray(driver,"//*[@id=\"root\"]/div/section/div[2]/p[2]",0);
      sleep(2);
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
          while (!new AutoCreateWallet1().initEnv()) {
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
/*
    byte[] buff1;
    byte[] buff2;
    try
    {
      buff1=walletSedList.toString().getBytes();

      FileOutputStream out1=new FileOutputStream("d:\\newwalletSed.txt", true);
      out1.write(buff1,0,buff1.length);
      out1.close();
      buff2=walletAddressList.toString().getBytes();
      FileOutputStream out2=new FileOutputStream("d:\\newwalletAddress.txt", true);
      out2.write(buff2,0,buff2.length);
      out2.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    */
    byte[] buff1;
    byte[] buff2;
    try {
      FileOutputStream out1 =new FileOutputStream("d:\\newWalletSed1.txt", true);
      for (int i = 0; i < walletSedList.size(); i++) {
        buff1 = walletSedList.get(i).toString().getBytes();
        out1.write("\"".getBytes());
        out1.write(buff1,0,buff1.length);
        out1.write("\"".getBytes());
        out1.write(",".getBytes());
        out1.write("\r\n".getBytes());

      }
      out1.close();

      FileOutputStream out2=new FileOutputStream("d:\\newWalletAddress1.txt", true);
      for (int i = 0; i < walletAddressList.size(); i++) {
        buff2 = walletAddressList.get(i).toString().getBytes();
        out2.write("\"".getBytes());
        out2.write(buff2,0,buff2.length);
        out2.write("\"".getBytes());
        out2.write(",".getBytes());
        out2.write("\r\n".getBytes());

      }
      out2.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  }
