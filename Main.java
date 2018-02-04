import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static Double gaussianBlur(double sigma, int mu, int y){
        double result = 1/(sigma * Math.sqrt(2 * Math.PI))* Math.exp(-0.5* (((y - mu) / sigma) * ((y - mu) / sigma)));
        return result;
    }

    public static void save(BufferedImage newImg){
        try {
            File out = new File("../result.png");
            ImageIO.write(newImg, "png", out);
        }
        catch (IOException e){
            System.out.println("failed to save");
        }
    }

    public static BufferedImage loopThrough(BufferedImage img, BufferedImage newImg){
        for(int y = 0; y < img.getHeight(); y++){
            for( int x = 0; x < img.getWidth(); x++){
                newImg.setRGB(x, y, img.getRGB(x, y));
            }
        }
        return newImg;
    }

    public static Double blurRadius() {
        Scanner scan = new Scanner(System.in);
        Double sig = null;
        try {
            System.out.println("Enter sigma value: ");
            sig = scan.nextDouble();
        }
        catch (Exception e) {
            System.out.println("not a number");
            System.exit(2);
        }
        return sig;
    }

    public static BufferedImage neighbours(BufferedImage newImg, int x, int y, double sigma){
        Double blurWidth = 3 * sigma;
        double sumA = 0;
        double sumR = 0;
        double sumG = 0;
        double sumB = 0;
        double counter = 0;
        if (x - blurWidth >= 0 && x + blurWidth < newImg.getWidth()){
            for (Double xx = x - blurWidth; xx < blurWidth; xx++) {
                if (y - blurWidth >= 0 && y + blurWidth < newImg.getHeight()) {
                    for (Double yy = y - blurWidth; yy < blurWidth; yy++) {
                        int ixx = xx.intValue();
                        int iyy = yy.intValue();
                        double gaussX = gaussianBlur(sigma, x, ixx);
                        double gaussY = gaussianBlur(sigma, y, iyy);
                        int alpha = (newImg.getRGB(ixx, iyy) >> 24) & 0xff;
                        int red = (newImg.getRGB(ixx, iyy) >> 16) & 0xff;
                        int green = (newImg.getRGB(ixx, iyy) >> 8) & 0xff;
                        int blue = (newImg.getRGB(ixx, iyy) >> 0) & 0xff;
                        sumA += alpha * gaussX * gaussY;
                        sumR += red * gaussX * gaussY;
                        sumG += green * gaussX * gaussY;
                        sumB += blue * gaussX * gaussY;
                        counter += gaussX * gaussY;
                        System.out.println("red: " + sumR);
                        System.out.println("gaussianX: " + gaussX);
                        System.out.println("counter: " + counter);
                    }
                }
            }
        }
        sumA /= counter;
        sumR /= counter;
        sumG /= counter;
        sumB /= counter;
        int color = ((int)sumA << 24) | ((int)sumR << 16) | ((int)sumG << 8) | ((int)sumB << 0);
        newImg.setRGB(x, y, color);

        return newImg;
    }

    public static void main(String[] args){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("../test_picture.png"));
        }
        catch (IOException e){
            System.out.println("image not found");
            e.getStackTrace();
        }

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newImg = loopThrough(img, newImg);
        Double sigma = blurRadius();
        System.out.println("processing...");
        for (int x = 0; x < newImg.getWidth(); x++){
            for (int y = 0; y < newImg.getHeight(); y++){
                newImg = neighbours(newImg, x, y, sigma);
            }
        }
        save(newImg);


        System.out.println("done");

    }
}
