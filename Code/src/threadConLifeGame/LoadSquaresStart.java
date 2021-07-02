package threadConLifeGame;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class LoadSquaresStart implements Runnable{
    // 我们首先应该确认一下整个图像数组高度，并基于此进行线程的分段（在构造器中赋予每段读取的高度）
    // 我们姑且先按照最简单的16x16像素图像进行处理，假设我们只采用4个线程进行处理，段长为6行（已确定段长和线程数关系为 段长=(height-4)/(线程数-2)）
    // 起始位置与段长在构造器中进行初始化
    private CellMap Map;
    private int Start;
    private int Length;
    private int id;
    private int Width;
    private int[][] map;
    private int[][] neighbor;
    private int[][] nextGeneration;
    private int height;
    private int generation;
    String FileIN;

    LoadSquaresStart(int id, String FileIn, CellMap Map){     // id表示线程的编号，用于确定Start (Start的算法： if id == 1 -> Start = height-1 else Start = (id-1)*(Length-2)-1)
        // 初始化第一个数组
        this.id = id;
        this.Map = Map;
        FileIN = FileIn;
    }

    // 读取除边缘以外的像素点的邻居信息
    public void getNeighbors(){
        // map的最上一行以及最低一行不进行处理
        for(int i = 1; i < Length - 1; i++){
            for(int j = 0; j < Width; j++){
                neighbor[i - 1][j] = getNeighborUnit(i, j);
            }
        }
    }


    int getNeighborUnit(int row, int col){
        int count = 0;
        for(int i = row - 1; i <= row + 1; i++){
            for(int j = col - 1; j <= col + 1; j++){
                if(i >= 0 && j >= 0 && i < Length && j < Width)
                    if(map[i][j] == 255){
                        count++;
                    }
            }
        }
        if(map[row][col] == 255)
            count -= 1;
        if(col == 0){
            for(int j = row - 1; j <= row + 1; j++){
                if(j >= 0 && j < Length){
                    if(map[j][Width - 1] == 255){
                        count ++;
                    }
                }
            }
        }else if(col == Width - 1){
            for(int j = row - 1; j <= row + 1; j++){
                if(j >= 0 && j < Length){
                    if(map[j][0] == 255){
                        count ++;
                    }
                }
            }
        }
        return count;
    }

    // 根据已获得的邻居分布对局部的数组进行迭代操作
    void NextGeneration(){
        //根据对应的neighbor count进行存活判定
        for (int i = 0; i < Length-2; i++) {
            for (int j = 0; j < Width; j++){
                if(neighbor[i][j] < 2 || neighbor[i][j] >= 4)
                    nextGeneration[i][j] = 0;
                else if(neighbor[i][j] != 2) {
                    nextGeneration[i][j] = 255;
                }else if(map[i + 1][j] == 255 && neighbor[i][j] == 2){
                    nextGeneration[i][j] = 255;
                }else{
                    nextGeneration[i][j] = 0;
                }
            }
        }
        // 检验局部迭代 确认是迭代算法出了问题还是拼接时出错(注。已确认邻居分布无错)

    }


    @Override
    public void run() {
        // 进入线程内
        {
            synchronized (this) {
                Width = 0;
                FileInputStream input = null;
                DataInputStream data = null;
                Scanner scanner;
                try {
                    input = new FileInputStream(FileIN);
                    data = new DataInputStream(input);
                    // 读出PGM图像规格
                    String format = data.readLine();
                    scanner = new Scanner(data.readLine());
                    // 读取图像宽度和高度
                    Width = scanner.nextInt();
                    height = scanner.nextInt();
                    Map.setHeight(height);
                    Map.setWidth(Width);
                    // 由height计算Length ->暂定线程数为4
                    Length = (height - 4) / 2;
                    // 由Length和id计算Start
                    if (id == 1) {
                        Start = height - 1;
                    } else {
                        Start = (Length - 2) * (id - 1) - 1;
                    }
                    map = new int[Length][Width];
                    neighbor = new int[Length - 2][Width]; // 不计算边缘的neighbor
                    nextGeneration = new int[Length - 2][Width]; // 根据neighbor进行处理后的细胞存活状态
                    data.readLine();
                    if (Start != height - 1 || (Start + Length) < (height - 2)) {
                        // 即不读取上下边缘
                        for (int i = 0; i < height; i++) {      // 将对应位置的像素信息读入map数组中
                            for (int j = 0; j < Width; j++) {
                                if (i >= Start && i < Start + Length)
                                    map[i - Start][j] = data.read();
                                else
                                    data.read();
                            }
                        }
                    } else if (Start == height - 1) {
                        // 读取上边缘
                        for (int i = 0; i < height; i++) {      // 将对应位置的像素信息读入map数组中
                            for (int j = 0; j < Width; j++) {
                                if (i < Length - 1)
                                    map[i + 1][j] = data.read();
                                else if (i == height - 1)
                                    map[0][j] = data.read();
                                else
                                    data.read();
                            }
                        }
                    } else {
                        // 读取下边缘
                        for (int i = 0; i < height; i++) {      // 将对应位置的像素信息读入map数组中
                            for (int j = 0; j < Width; j++) {
                                if (i >= Start) {
                                    map[i - Start][j] = data.read();
                                }
                                else if (i == 0) {
                                    map[Length - 1][j] = data.read();
                                }else {
                                    data.read();
                                }
                            }
                        }
                    }


                    // 经检验->初始化的数组是正确的，可读入初版的像素图
                    // 目前的错误出在迭代上
                    // 因与单线程版本进行对比读取
                    // 注意，由于线程读取的特殊性，我们只需要贯通左右的壁垒，而不需对上下的界限进行处理
                    // 将map截取为nextGeneration
                    for(int i = 1; i < Length - 1; i++){
                        for(int j = 0; j < Width; j++){
                            nextGeneration[i-1][j] = map[i][j];
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 这一部分都有问题，由于跨上下栏的缘故，不能将map直接用于为CellMap进行赋值，而应该截取为nextGeneration并将各线程的数组进行拼接
            // 假如是初始化用的构造器，则无需迭代，直接取除上下两行以外的部分存入nextGeneration数组，迭代构造器则通过NextGeneration方法对nextGeneration数组进行获取。
            int Schange;
            //        // 当前已获取一个迭代后的数组，对原数组进行覆盖
            if(Start == height-1){
                Schange = 0;
            }else{
                Schange = Start + 1;  // 不录入用于计算邻居的上下两行
            }
            for(int i = 0; i < Length - 2; i++){
                for(int j = 0; j < Width; j++){
                    Map.setMap(i+Schange, j, nextGeneration[i][j]);
                }
            }
        }
    }
}
