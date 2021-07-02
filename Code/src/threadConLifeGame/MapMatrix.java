package threadConLifeGame;

import java.io.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

// 题目要求：使用n个线程并行读取初始pgm文件，把图像分割为n份，分别计算出下一轮的细胞状态，然后重新整合为一个新一代细胞状态图

// 要求通过多个线程进行图像的读取，并且分别确认下一轮的细胞状态 这就要求各线程读取的行数据必须存在重合，且最高的一行必须读取导最低的一行
// 为了避免给线程最后出的结果不会互相矛盾，每个线程必须留出边缘不进行处理。
// 由于确定细胞死活用的是3x3的数据块，故我们应当取类似 height-2 -> unitlength + 1(height-2、unitlength + 1行只读取不处理)、unitlength -> 2*unitlengh + 1(2*unitlengh + 1只读取不处理)
// 大概是这么回事
// 最后讲处理后的数组按行进行拼接，作为新一代的细胞状态图
// 由于其他两个类需要的是完整的下一代细胞状态数组，故这两个类无需进行处理，只需讲MapMatrix进行多线程化即可。
// 故我们需要一个专门的类来进行读取图像并继承Runnable
// MapMayrix类我们姑且只用于启动线程和拼接下一代状态数组。


public class MapMatrix {
    private int[][] map;
    private int width;
    private int height;
    private  CellMap cellMap;
    String FileIN;
    int count;


    MapMatrix(String FileIn) throws FileNotFoundException {
        count = 0;
        InputStream input = null;
        DataInputStream data = null;
        Scanner scanner = null;
        FileIN = FileIn;
        // 初始化
        try{
            input = new FileInputStream(FileIn);
            data = new DataInputStream(input);
            String format = data.readLine();
            scanner = new Scanner(data.readLine());
            width = scanner.nextInt();
            height = scanner.nextInt();
            cellMap = new CellMap(height, width);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    int getWidth(){return width;}

    int getHeighth(){return height;}

    int getMapunit(int row, int col){
        return cellMap.getValue(row, col);
    }

    void firstMap(){
        // firstMap由于本身的cellMap未初始化，所以需要在线程内进行初始化和第一张图的填充
        // 由于firstMap的cellMap本身不存储信息所以需要利用FileIN在线程内读取信息
        LoadSquaresStart th1 = new LoadSquaresStart(1, FileIN, cellMap);
        LoadSquaresStart th2 = new LoadSquaresStart(2, FileIN, cellMap);
        LoadSquaresStart th3 = new LoadSquaresStart(3, FileIN, cellMap);
        LoadSquaresStart th4 = new LoadSquaresStart(4, FileIN, cellMap);
        Thread thread1 = new Thread(th1);
        Thread thread2 = new Thread(th2);
        Thread thread3 = new Thread(th3);
        Thread thread4 = new Thread(th4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        while(thread1.isAlive() && thread2.isAlive() && thread3.isAlive() && thread4.isAlive()){}
        map = cellMap.getMap();
        // 问题不出在第二次读取，出在第一次输入，处于未知原因 应该是id2读行的时候多读了一行（根据debug显示应当是在行5第一次出现细胞）
    }



    void nextMap(){
        // nextMap中图像的高度信息、宽度信息、以及当前的数组已经已经存储在了cellMap中无需再一次读取图像，故应重载Runnable对象的构造器
        // 传入参数应为：(id, cellMap) 将cellMap中的数组进行读取并迭代
        LoadSquaresContinue th1 = new LoadSquaresContinue(1, cellMap);
        LoadSquaresContinue th2 = new LoadSquaresContinue(2, cellMap);
        LoadSquaresContinue th3 = new LoadSquaresContinue(3, cellMap);
        LoadSquaresContinue th4 = new LoadSquaresContinue(4, cellMap);
        Thread thread1 = new Thread(th1);
        Thread thread2 = new Thread(th2);
        Thread thread3 = new Thread(th3);
        Thread thread4 = new Thread(th4);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        while(thread1.isAlive() && thread2.isAlive() && thread3.isAlive() && thread4.isAlive()){}
        map = cellMap.getMap();
//        // 统计map中细胞数量
        count = 0;
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                if(map[i][j] == 255)
                    count += 1;
            }
        }



//
//        System.out.println(count);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Timer t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println(count);
//                count = 0;
//            }
//        }, 20000);


    }

    public void setCount(int value){
        count = value;
    }

    public int getCount() {
        return count;
    }
}
