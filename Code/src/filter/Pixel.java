package filter;

import java.awt.image.BufferedImage;

public class Pixel {
    private int[] data;
    private int[][] pixarr;
    private int height;
    private int width;

    Pixel(BufferedImage img){
        height = img.getHeight();
        width = img.getWidth();
        data = new int[height * width];
        pixarr = new int[height][width];
        img.getRGB(0, 0, width, height, data, 0, width);
        for(int i = 0; i < height; i++){
            System.arraycopy(data, i * width, pixarr[i], 0, width);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int[] getData() {
        return data;
    }

    public int getPixdata(int i, int j) {
        return pixarr[i][j];
    }

    public void setPixel(int i, int j, int value) {
        pixarr[i][j] = value;
    }
}
