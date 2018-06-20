import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Augmenter;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.sourceforge.tess4j.*;


public class getCaptcha {

        public static void getPrintScreen(String link) {
            System.setProperty("webdriver.gecko.driver", "/home/tamas/Desktop/captchaSolver/geckodriver");
            WebDriver driver = new FirefoxDriver();
            driver.get(link);
            String path;

            try {
                    WebDriver augmentedDriver = new Augmenter().augment(driver);
                    File source = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
                    path = "./" + "printscreen.png";
                    FileUtils.copyFile(source, new File(path));
                    cropImage(new File(path), 560, 240, 150, 50);
            }
                catch(IOException e) {
                    path = "Failed to capture screenshot: " + e.getMessage();
                    System.out.println(path);
                }
        }

    public static void cropImage(File filePath, int x, int y, int w, int h){

        try {
            BufferedImage originalImgage = ImageIO.read(filePath);
            BufferedImage subImgage = originalImgage.getSubimage(x, y, w, h);
            File outputfile = new File("./captcha.png");
            ImageIO.write(subImgage, "png", outputfile);
            System.out.println(getImgText("./captcha.png"));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getImgText(String imageLocation) {
        ITesseract instance = new Tesseract();
        try
        {
            String imgText = instance.doOCR(new File(imageLocation));
            return imgText;
        }
        catch (TesseractException e)
        {
            e.getMessage();
            return "Error while reading image";
        }
    }

        public static void main(String[] args) {
            getPrintScreen("https://sajtopub.nmhh.hu/sajto_kozzetetel/app/elerhetoseg.jsp?i=3861944&f=I");
        }
    }

