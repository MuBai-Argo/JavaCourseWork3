package filter;

import java.util.Arrays;

public class FilterSquares implements Runnable{
    private int StartX;
    private int StartY;
    private int EndX;
    private int EndY;
    private Pixel PixImage;

    public FilterSquares(int sx, int sy, int ex, int ey, Pixel Piximg){
        StartX = sx;
        StartY = sy;
        EndX = ex;
        EndY = ey;
        PixImage = Piximg;
    }

    public void medianFilter() {
        int[][] delta = new int[3 * 3][2];
        // delta表示每个3*3像素块中每个像素距离中心的位置
        synchronized(this){
            for (int i = 0; i < 9; i++) {
                int row = i / 3;
                int col = i % 3;
                delta[i][0] = row - 1;
                delta[i][1] = col - 1;
        }
        for (int i = StartY + 1; i < EndY - 1; i++) {//row
            for (int j = StartX + 1; j < EndX - 1; j++) {//col
                // 对图像中每一个像素进行遍历（以遍历中的像素作为3*3的中值）
                int[] pix_3_3 = new int[9];
                // 当前遍历到的像素中心为PixData.getPixdata(i*width,j)
                for (int k = 0; k < pix_3_3.length; k++) {
                    // 填充3*3像素块
                    pix_3_3[k] = PixImage.getPixdata(i + delta[k][0], j + delta[k][1]);
                }
                //对3*3像素块进行排序,取中值赋予当前像素
                Arrays.sort(pix_3_3);
                int average = pix_3_3[4];
                PixImage.setPixel(i, j, average);

            }
            }
        }
    }
    @Override
    public void run() {
            medianFilter();
        }
    }

