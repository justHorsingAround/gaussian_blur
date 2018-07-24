import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;


public class Main {
    private static double gaussianBlur(double sigma, int mu, int y){
        double result = 1/(sigma * Math.sqrt(2 * Math.PI))* Math.exp(-0.5* (((y - mu) / sigma) * ((y - mu) / sigma)));
        return result;
    }

    private static void save(BufferedImage newImg){
        try {
            File out = new File("./result.png");
            ImageIO.write(newImg, "png", out);
        }
        catch (IOException e){
            System.out.println("failed to save");
        }
    }

    private static BufferedImage loopThrough(BufferedImage img, BufferedImage newImg){
        for(int y = 0; y < img.getHeight(); y++){
            for( int x = 0; x < img.getWidth(); x++){
                newImg.setRGB(x, y, img.getRGB(x, y));
            }
        }
        return newImg;
    }

    private static double blurRadius() {
        Scanner scan = new Scanner(System.in);


        System.out.println("Enter sigma value: ");
        String input = scan.next().trim();
        double sig = Double.parseDouble(input);


        return sig;
    }

    private static BufferedImage neighbours(BufferedImage newImg, int x, int y, double sigma){
        double blurWidth = 3 * sigma;
        double sumA = 0;
        double sumR = 0;
        double sumG = 0;
        double sumB = 0;
        double counter = 0;

        for (double xx = x - blurWidth; xx < x + blurWidth; xx++) {
            if (xx >= 0 && xx < newImg.getWidth()){
                for (double yy = y - blurWidth; yy < y + blurWidth; yy++) {
                    if (yy >= 0 && yy < newImg.getHeight()) {
                        int ixx = (int)Math.round(xx);
                        int iyy = (int)Math.round(yy);
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
                    }
                    else {
                        continue;
                    }
                }
            }
            else {
                continue;
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

    private static String pathToRawImage(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the path of your raw file:");
        return scan.nextLine();
    }

    public static void main(String[] args){
        Date begin = new Date();
        System.out.println(begin.toString());
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(pathToRawImage()));
        }
        catch (IOException e){
            System.out.println("image not found");
            System.exit(2);

        }

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newImg = loopThrough(img, newImg);
        double sigma = blurRadius();
        System.out.println("processing...");
        for (int x = 0; x < newImg.getWidth(); x++){
            for (int y = 0; y < newImg.getHeight(); y++){
                newImg = neighbours(newImg, x, y, sigma);
            }
        }
        save(newImg);

        Date end = new Date();
        System.out.println(end.toString());

        System.out.println("done");

    }
}
