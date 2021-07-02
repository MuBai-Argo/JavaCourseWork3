package filter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

public class MedianFilter{
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    MedianFilter(int sX, int sY, int eX, int eY){
        startX = sX;
        startY = sY;
        endX = eX;
        endY = eY;
    }

    public Pixel Imagload(String FileIN){
        try {
            BufferedImage img = ImageIO.read(new FileInputStream(FileIN));
            return new Pixel(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void remakePNG(Pixel PixelImg, String FileOUT){
        int[] data = new int[PixelImg.getHeight() * PixelImg.getWidth()];
        for (int i = 0; i < PixelImg.getHeight(); i++){
            for (int j = 0; j < PixelImg.getWidth(); j++){
                data[i * PixelImg.getWidth() + j] = PixelImg.getPixdata(i, j);
            }
        }
        BufferedImage bi = new BufferedImage(PixelImg.getWidth(), PixelImg.getHeight(), BufferedImage.TYPE_INT_BGR);
        bi.setRGB(0, 0, PixelImg.getWidth(), PixelImg.getHeight(), data, 0,  PixelImg.getWidth());
        try {
            ImageIO.write((RenderedImage) bi, "PNG", new File(FileOUT));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void filter() {
        String FileIN = "D:\\Study\\Study in MUC\\作业\\Java\\19010707 杨皓天 CourseWork3\\source\\ship.png";
        String FileOUT = "D:\\Study\\Study in MUC\\作业\\Java\\19010707 杨皓天 CourseWork3\\source\\new_ship.png";
        Pixel Pixelimag = Imagload(FileIN);

        // 应将这一步过滤算法多线程化， 为此设立一个类，FilterSquares
//        medianFilter(startY, endY, startX, endX, Pixelimag);
        Thread thread1 = new Thread(new FilterSquares(0, 0, 512, 128, Pixelimag));
        Thread thread2 = new Thread(new FilterSquares(0, 126, 512, 256, Pixelimag));
        Thread thread3 = new Thread(new FilterSquares(0, 254, 512, 384, Pixelimag));
        Thread thread4 = new Thread(new FilterSquares(0, 382, 512, 512, Pixelimag));
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        // 通过while确保线程将图片中内容处理完以后才开始执行图片的输出
        System.out.println("Loading...");
        while(thread1.isAlive() || thread2.isAlive() || thread3.isAlive() ||thread4.isAlive()){ }
        System.out.println("Excute Compeleted");
        remakePNG(Pixelimag, FileOUT);
    }


    public static void main(String[] args) {
        // 要将图像处理算法多线程化，需要的是将meidianfiter这一步进行多线程而不能将图像输出多线程化
        MedianFilter median = new MedianFilter(0, 0, 512, 512);
        median.filter();
    }
}
