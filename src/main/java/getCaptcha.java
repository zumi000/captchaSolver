
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Augmenter;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.List;

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
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current dir using System:" +currentDir);
            System.out.println("tess4j's answer:");
            System.out.println(getImgText("./captcha.png"));
            System.out.println("google's answer:");




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

    public static void detectText(String filePath) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.println("Error: %s\n" + res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    System.out.println("Text: %s\n" + annotation.getDescription());
                    System.out.println("Position : %s\n" + annotation.getBoundingPoly());
                }
            }
        }
    }


        public static void main(String[] args) throws Exception {
            System.out.println(getImgText("./captcha.png"));
            detectText("./captcha.png");
            //getPrintScreen("https://sajtopub.nmhh.hu/sajto_kozzetetel/app/elerhetoseg.jsp?i=3861944&f=I");
        }
    }

