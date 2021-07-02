package producter_consumer;

import java.util.ArrayList;

public class SynContainer {     // 缓冲区
    // count 用于为缓冲区中已存在的数据进行计数
    private int Tcount;
    private int count;
    private int wri_index;
    private int red_index;
    private int MAXSIZE;
    private ArrayList<Product > container;

    public SynContainer(){
        MAXSIZE = 5;
        Tcount = 0;
        count = 0;
        container = new ArrayList<>(MAXSIZE);

        for(int i = 0; i < 5; i ++){
            container.add(new Product(0));
        }
    }

    public int getcount(){return count;}

    public int getTcount(){return Tcount;}


    public int getMAXSIZE(){return MAXSIZE;}

    public ArrayList<Product> getContainer(){return container;}

    public int getwriteIndex(){return wri_index;}

    public int getreadIndex(){return red_index;}

    public synchronized void push(Product product){
        if(count < MAXSIZE){
            // 若缓冲区未满，则可压入新的product(并更改写入位置)
//            System.out.println(Thread.currentThread().getName());
            container.set(wri_index, product);
            Tcount ++;
            count ++;
            if(wri_index == 4){
                wri_index = 0;
            }else{
                wri_index ++;
            }
        }else{
            // 缓冲区满，加入行为等待，等待消费者消费
            try {
                this.wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        // 通知消费者消费
        this.notifyAll();
    }

    public synchronized Product pop(){
//        System.out.println(Thread.currentThread().getName());
        Product product;
        if(count != 0){
            // 若缓冲区含有product,则可进行消费
            // 根据题目要求，不将container中的product排出，而是改变更改消费位置（而不更改写入位置）Tcount是累计生产数，count是未消费生产数，故只减少count
            product = container.get(red_index);
            count --;
            if(red_index == 4){
                red_index = 0;
            }else{
                red_index ++;
                //System.out.println(red_index);
            }
        }else{
            // 缓冲区空，加入行为等待，等待生产者生产
            try {
                this.wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            product = null;
        }
        // 通知生产者生产
        this.notifyAll();
        return product;
    }

}
