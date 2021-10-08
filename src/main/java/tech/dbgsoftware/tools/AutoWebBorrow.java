package tech.dbgsoftware.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AutoWebBorrow implements Runnable {

  private static final AtomicLong TIMES = new AtomicLong(0);

  private Logger logger = Logger.getLogger(AutoWebBorrow.class.getName());

  private String val;

  private String sed;

  private String pwd = "ABMISYOURDADDY!";

  private WebDriver driver;

  static ArrayList<String> walletSedList = new ArrayList<>(300);

  public AutoWebBorrow(String sed, String val) {
    this.sed = sed;
    this.val = val;
  }

  private static void sleep(int second) {
    try {
      Thread.sleep(1000L * second);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void clickTillShowByClass(WebDriver driver, String className) {
    int tryTimes = 0;
    while (driver.findElements(By.className(className)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        driver.quit();
        throw new RuntimeException("未找到元素");
      }
    }
    driver.findElement(By.className(className)).click();
  }

  public static void clickTillShowByClassArray (WebDriver driver, String className, int index) throws Exception{
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.className(className)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {

        System.out.println("未找到元素");

        throw new Exception();
      }
    }
    driver.findElements(By.className(className)).get(index).click();
  }

  public static void clickTillShowByXPathArray(WebDriver driver, String xPathName, int index) {
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.xpath(xPathName)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        //driver.quit();
        return;
      }
    }
    if (driver.findElements(By.xpath(xPathName)).size() != 0) {
      driver.findElements(By.xpath(xPathName)).get(index).click();
    }
  }

  public static boolean tillShowByClassArray(WebDriver driver, String className) {
    int tryTimes = 0;
    sleep(1);
    while (driver.findElements(By.className(className)).size() == 0) {
      sleep(1);
      tryTimes++;
      if (tryTimes > 10) {
        //driver.quit();
        //throw new RuntimeException("未找到元素");
        System.out.println("未找到元素");
      }
    }
    return true;
  }

  public static boolean isNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  public void refreshWeb() {
    driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
    sleep(3);
    driver.navigate().refresh();
    sleep(3);
    closeWindow();
    sleep(1);
    for (int i = 0; i < driver.getWindowHandles().size(); i++) {
      sleep(1);
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[i]));
      sleep(1);
      if (driver.getTitle().equals("Solend")){
        break;
      }
    }
    sleep(2);
    clickTillShowByXPathArray(driver,
        "//*[@id=\"root\"]/section/main/div/div/div[2]/div/div/button", 0);
    sleep(3);
    clickTillShowByXPathArray(driver,
        "/html/body/div[2]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/div[1]/div", 0);
  }

  public void closeWindow() {
    try {
      String winHandleBefore = driver.getWindowHandle();//关闭当前窗口前，获取当前窗口句柄
      Set<String> winHandles = driver.getWindowHandles();//使用set集合获取所有窗口句柄

      driver.close();//关闭窗口

      Iterator<String> it = winHandles.iterator();//创建迭代器，迭代winHandles里的句柄
      while (it.hasNext()) {//用it.hasNext()判断时候有下一个窗口,如果有就切换到下一个窗口
        String win = it.next();//获取集合中的元素
        if (!win.equals(winHandleBefore)) { //如果此窗口不是关闭前的窗口
          driver.switchTo().window(win);//切换到新窗口

          break;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();

    }

  }

  public AutoWebBorrow initEnv() {
    try {
      //配置瀏覽器環境，配置錢包插件
      System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
      ChromeOptions chromeOptions = new ChromeOptions();
      chromeOptions.addExtensions(new File("C:\\driver\\phantom.crx"));
      driver = new ChromeDriver(chromeOptions);

      logger.info("打开Opensea页面");
      driver.get("https://solend.fi/dashboard");
      sleep(1);
      logger.info("切换到钱包导入界面");
      //确保切换到钱包页面
      for (int i = 0; i < driver.getWindowHandles().size(); i++) {
        sleep(1);
        driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[i]));
        sleep(1);
        if (driver.getTitle().equals("Phantom Wallet")){
          break;
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

    } catch (Exception e) {
      try {
        System.out.println(sed);
        driver.quit();
      } catch (Exception ignored) {
      }
      sleep(1);
    }
    return this;
  }

  public void storeUsdt() throws Exception{
    //  把钱包里的usdt存入市场
    driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));//等会要把这条提到方法之外
    clickTillShowByClassArray(driver, "ant-table-row", 5);
    for (int i = 0; driver.findElements(By.className("BigInput_maxButton__2AXtI")).size() == 0;
        i++) {
      refreshWeb();
      clickTillShowByClassArray(driver, "ant-table-row", 5);
    }
  }

  public void clickMax() throws Exception{
    logger.info("USDT点击max");
    //USDT点击max
    clickTillShowByClassArray(driver, "BigInput_maxButton__2AXtI", 0);

  }

  public void clickSupply() throws Exception{
    logger.info("点击supply");
    //点击supply
    clickTillShowByClassArray(driver, "ConfirmButton_button__bbD_W", 0);
    sleep(3);
/*    for (int i = 0; driver.findElements(By.xpath("/html/body/div[2]/div/div[2]/div/div[2]/div/div/div[4]/button")).size() == 0; i++) {
      refreshWeb();
      storeUsdt();
      clickMax();
      clickTillShowByXPathArray(driver, "//*[@id=\"rc-tabs-0-panel-Supply\"]/div/button", 0);
    }*/
  }

  public void clickDone() {
    logger.info("点击DONE");
    //点击DONE
    sleep(1);
    try {
      clickTillShowByClassArray(driver, "TransactionTakeover_bigButton__12cBh", 0);
    } catch (Exception e) {
      refreshWeb();
      e.printStackTrace();
    }
    sleep(2);
/*    for (int i = 0; driver.findElements(By.xpath("//*[@id=\"root\"]/section/main/div/div/div[1]/div[2]/div[2]/div/div/div/div/div/div/table/tbody/tr[1]")).size() == 0; i++) {
      refreshWeb();
    }*/
  }

  public void borrowSol() throws Exception{
    logger.info("点击SOLANA");
    //点击SOLANA
    sleep(1);
    clickTillShowByClassArray(driver, "ant-table-row-level-0", 0);
/*    for (int i = 0; driver.findElements(By.xpath("/html/body/div[2]/div/div[2]/div/div[2]/div/div/div[1]/div[1]/div/div[2]")).size() == 0; i++) {
      refreshWeb();
      clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/section/main/div/div/div[1]/div[2]/div[2]/div/div/div/div/div/div/table/tbody/tr[1]", 0);
    }*/
  }

  public void clickBorrow() throws Exception{
    //点击borrow
    logger.info("点击borrow");
    sleep(1);
    clickTillShowByClassArray(driver, "ant-tabs-tab", 1);
/*    for (int i = 0; driver.findElements(By.xpath("//*[@id=\"rc-tabs-4-panel-Borrow\"]/div/div[1]/div/div[1]/div")).size() == 0; i++) {
      refreshWeb();
      borrowSol();
      clickTillShowByXPathArray(driver, "/html/body/div[3]/div/div[2]/div/div[2]/div/div/div[1]/div[1]/div/div[2]", 0);
    }*/
  }

  public void clickMax1() throws Exception{
    //点击max
    logger.info("点击max");
    sleep(1);
    clickTillShowByClassArray(driver, "BigInput_maxButton__2AXtI", 1);
    //clickTillShowByXPathArray(driver, "//*[@id=\"rc-tabs-0-panel-Borrow\"]/div/div[1]/div/div[1]/div/div", 0);
/*    for (int i = 0; Integer.parseInt(driver.findElements(By.xpath("//*[@id=\"rc-tabs-10-panel-Borrow\"]/div/div[1]/div/div[2]/span[1]")).get(0).getText()) < 0.5; i++) {
      refreshWeb();
      borrowSol();
      clickBorrow();
      clickTillShowByXPathArray(driver, "//*[@id=\"rc-tabs-4-panel-Borrow\"]/div/div[1]/div/div[1]/div", 0);
    }*/
  }

  public void clickBorrow1() throws Exception{
    //点击确定
    logger.info("点击确定");
    sleep(1);
    try {
      clickTillShowByClassArray(driver, "ant-btn-primary", 1);
    } catch (Exception e) {
      throw new Exception();

    }
    sleep(3);

    sleep(2);

  }

  public void clickDone2() {
    logger.info("点击Done");
    sleep(1);
    try {
      clickTillShowByClassArray(driver, "ant-result-content", 0);
    } catch (Exception e) {
      return;
    }
    sleep(3);
    try {
      if (driver.findElements(By.className("ant-result-content")).size() == 0) {
        refreshWeb();
      }
    } catch (Exception e) {

      refreshWeb();
      return;
    }
  }

  public void clickRepaySol() throws Exception{
    //点击SOLana还币按钮
    logger.info("点击SOLana还币按钮");
    sleep(2);

    clickTillShowByXPathArray(driver, "ant-table-row-level-0", 9);

    //如果因为BUG没借成，重新做借sol操作
    try {
      clickTillShowByXPathArray(driver,
          "//*[@id=\"root\"]/section/main/div/div/div[2]/div/div[2]/div[3]/div/div/div/div/div/table/tbody/tr[1]/td[1]",
          0);
    } catch (Exception e) {
      return;
    }
  }

  public void clickMax2() throws Exception{
    //点max
    logger.info("点击max");
    clickTillShowByClassArray(driver, "BigInput_iconContainer__1vcKQ", 0);
/*    for (int i = 0; Integer.parseInt(driver.findElements(By.xpath("//*[@id=\"rc-tabs-1-panel-Repay\"]/div/div[1]/div/div[2]/span[1]")).get(0).getText()) < 0.5; i++) {
      refreshWeb();
      clickRepaySol();
      clickTillShowByXPathArray(driver, "//*[@id=\"rc-tabs-3-panel-Repay\"]/div/div[1]/div/div[1]/div", 0);
    }*/
  }

  public void clickRepay() throws Exception{
    //点repay
    logger.info("点击Repay");
    try {
      Thread.sleep(500);
      driver.findElements(By.className("ant-btn-primary")).get(0).click();
    } catch (Exception ignored) {
      throw new Exception();
    }
    sleep(3);
  }

  public void clickDone3() throws Exception{
    logger.info("点击Done");
    sleep(1);
    try {
      clickTillShowByClassArray(driver, "TransactionTakeover_bigButton__12cBh", 0);

    } catch (Exception e) {
      return;
    }
    sleep(1);
    try {
      refreshWeb();
    } catch (Exception e) {
      throw new Exception();

    }
  }

  public void withDrawUsdt() throws Exception{
    //取usdt
    try {
      if (driver.findElements(By.className("ant-table-row-level-0")).size() == 0) {
        //页面中刷新按钮
        driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
      }
    } catch (Exception e) {
      logger.info("错误：点击取USDT按钮");
      //页面中刷新按钮
      driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
      return;
    }
    clickTillShowByClassArray(driver, "ant-table-row-level-0", 8);
  }

  public void clickMax3() throws Exception{
    //MAX
    logger.info("点击MAX");
    sleep(1);
    clickTillShowByClassArray(driver, "BigInput_maxButton__2AXtI", 0);

  }

  public void clickWithdraw() throws Exception {
    //确定
    Thread.sleep(500);

      clickTillShowByClassArray(driver,"ant-btn-primary",0);

    sleep(5);

      if (driver.findElements(By.className("TransactionTakeover_bigButton__12cBh")).size() == 0) {

        clickTillShowByClassArray(driver, "BigInput_maxButton__2AXtI", 0);
        Thread.sleep(500);
        clickTillShowByClassArray(driver,"ant-btn-primary",0);
      }

  }

  public void clickDone1() {
    //点击确定
    logger.info("DONE");
    sleep(2);
    try {
      clickTillShowByClassArray(driver, "TransactionTakeover_bigButton__12cBh", 0);
    } catch (Exception e) {
      return;
    }
    sleep(1);
    try {
      refreshWeb();
    } catch (Exception e) {
      refreshWeb();
      return;
    }
  }

  public Double coinNumber(WebDriver driver, String className1, String className2, int Number) {
    int tryTimes = 0;
    while (
        driver.findElements(By.className(className1)).get(8).findElements(By.className(className2))
            .size() == 0) {
      tryTimes++;
      if (tryTimes > 2) {
        //页面中刷新按钮
        driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
        sleep(1);
      }
      if (tryTimes > 10) {
        return 0.0;
      }
    }
    if (!isNumeric(
        driver.findElements(By.className(className1)).get(8).findElements(By.className(className2))
            .get(Number).getText())) {
      return Double.parseDouble(driver.findElements(By.className(className1)).get(8)
          .findElements(By.className(className2)).get(Number).getText().replace("USDT", " ")
          .trim());
    }
    Double number = Double.parseDouble(
        driver.findElements(By.className(className1)).get(8).findElements(By.className(className2))
            .get(Number).getText());
    return number;
  }

  public Double coinNumber2(WebDriver driver, String className1, int Number1, String className2,
      int Number2, String className3, int Number3)
      throws InterruptedException {
    int tryTimes = 0;
    Thread.sleep(500);
    driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
    Thread.sleep(500);
    //页面中刷新按钮
    driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
    Thread.sleep(500);
    while (driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).size() == 0) {
      tryTimes++;
      if (tryTimes > 2) {
        //页面中刷新按钮
        driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
        sleep(1);
      }
      if (tryTimes > 10) {
        return 0.0;
      }
    }
    if (!isNumeric(driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).get(Number2).findElements(By.className(className3))
        .get(Number3).getText())) {
      try {
        return Double.parseDouble(driver.findElements(By.className(className1)).get(Number1)
            .findElements(By.className(className2)).get(Number2)
            .findElements(By.className(className3)).get(Number3).getText().replace("SOL", " ")
            .trim());
      } catch (Exception e) {
        return 0.0;
      }
    }
    Double number = Double.parseDouble(driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).get(Number2).findElements(By.className(className3))
        .get(Number3).getText());
    return number;
  }

  public Double coinNumber3(WebDriver driver, String className1, int Number1, String className2,
      int Number2, String className3, int Number3)
      throws InterruptedException {
    int tryTimes = 0;
    Thread.sleep(500);
    driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
    Thread.sleep(500);
    //页面中刷新按钮
    driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
    Thread.sleep(500);
    while (driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).size() == 0) {
      tryTimes++;
      if (tryTimes > 2) {
        //页面中刷新按钮
        driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
        sleep(1);
      }
      if (tryTimes > 10) {
        return 0.0;
      }
    }
    if (!isNumeric(driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).get(Number2).findElements(By.className(className3))
        .get(Number3).getText())) {
      try {
        return Double.parseDouble(driver.findElements(By.className(className1)).get(Number1)
            .findElements(By.className(className2)).get(Number2)
            .findElements(By.className(className3)).get(Number3).getText().replace("USDT", " ")
            .trim());
      } catch (Exception e) {
        return 0.0;
      }
    }
    Double number = Double.parseDouble(driver.findElements(By.className(className1)).get(Number1)
        .findElements(By.className(className2)).get(Number2).findElements(By.className(className3))
        .get(Number3).getText());
    return number;
  }


  private void mine(String sed) {
    sleep(1);
//    LogUtils.info("切换到opensea界面");
    logger.info("切换到Solend");
    refreshWeb();
    sleep(3);
    driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[1]));
    clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/div/div[1]/div[2]/div[1]/span/input", 0);
    clickTillShowByXPathArray(driver, "//*[@id=\"root\"]/div/div[1]/div[2]/div[2]/div/button[2]",
        0);

    sleep(3);

    //存-借
    try {
      logger.info("存USDT");
      storeUsdt();
      sleep(1);
      clickMax();
      sleep(1);
      clickSupply();
      clickDone();
      sleep(2);
      refreshWeb();
      sleep(3);
      //查看存了多少USDT，没存进去重新存
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      while (coinNumber(driver, "ant-table-row-level-0", "Typography_body__rkN69", 1) < 0.1) {
        storeUsdt();
        sleep(1);
        clickMax();
        sleep(1);
        clickSupply();
        clickDone();
        sleep(2);
        //页面中刷新按钮
        try {
          driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
        } catch (Exception e) {
          System.out.println(sed);
          refreshWeb();
          return;
        }

        sleep(1);
      }
      logger.info("借SOL");
      borrowSol();
      clickBorrow();
      clickMax1();
      clickBorrow1();
      clickDone2();
      refreshWeb();
      //查看借了多少SOL，没借成重新借
      driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[0]));
      for (int i = 0; coinNumber2(driver, "ant-table-tbody", 2, "ant-table-cell", 1,
          "Typography_primary__r-t61", 0) <= 0.005; i++) {
        refreshWeb();
        borrowSol();
        clickBorrow();
        clickMax1();
        clickBorrow1();
        clickDone2();
        sleep(1);
        // 页面中刷新按钮
        driver.findElements(By.className("RefreshDataButton_glow__RY6Hu")).get(0).click();
        sleep(1);
        if (i == 10){
          System.out.println("借SOL出错"+sed);
          driver.quit();
        }
      }

    } catch (Exception e) {
      try {
        //driver.quit();
        //日志
        System.out.println(sed);
        e.printStackTrace();
        logger.info("出错");
      } catch (Exception ignored) {
      }

    }

  }

  @Override
  public void run() {
    mine(sed);
  }

  public static void main(String[] args) {
    String val = "0";
    List<String> sed = Arrays.asList(

        "minute banana tape nerve actress caught whale victory debate rain elevator kitten"

    );
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    Set<String> stringSet = new HashSet<>();
    sed.forEach((s) -> {
      try {
        if (!stringSet.contains(s)) {//查重，看有沒有重複
          stringSet.add(s);//把執行了的sed加入隊列
          executorService.execute(() -> new AutoWebBorrow(s, val).initEnv().run());
        }
      }catch (Exception e){
        walletSedList.add(s);
        System.out.println(s);
        e.printStackTrace();
      }
    });
    walletSedList.forEach(System.out::println);
  }
}




