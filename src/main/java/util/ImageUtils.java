package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static int  calcAvrRGB(BufferedImage image, int startX, int startY, int n) {
        int totalPixels = n * n;
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int rgb;
        for (int y = startY; y < startY + n; y++) {
            for (int x = startX; x < startX + n; x++) {
                if (x < width && y < height){
                     rgb = image.getRGB(x, y);


                }
                else if( x< width && y>=height){
                    rgb = image.getRGB(x, height-1);
                }else if(x>=width && y<height){
                    rgb = image.getRGB(width-1,y);

                }else{
                    rgb = image.getRGB(width-1,height-1);
                }

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                totalRed += red;
                totalGreen += green;
                totalBlue += blue;

            }
        }

        int averageRed = totalRed / totalPixels;
        int averageGreen = totalGreen / totalPixels;
        int averageBlue = totalBlue / totalPixels;


        return  (averageRed << 16) | (averageGreen << 8) | averageBlue;

    }
    public static void savePixelatedImage(BufferedImage imageToSave) {
        try {
            File output = new File("src/main/resources/result.jpg");
            ImageIO.write(imageToSave, "jpg", output);
            System.out.println("Pixelated image saved to: " + output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
