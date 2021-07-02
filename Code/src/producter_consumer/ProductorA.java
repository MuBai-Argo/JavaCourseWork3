package producter_consumer;

import java.util.Random;


public class ProductorA implements Runnable{
    private SynContainer container;

    public ProductorA(SynContainer SHAREONE){
        container = SHAREONE;
    }

    private synchronized int bitsCount(int i){
        if(i == 1000){
            return 1;
        }else if(i >= 100){
            return 2;
        }else if(i >= 10){
            return 3;
        }else{
            return 4;
        }
    }

    private synchronized String getStr(Product product, int i, int bits, SynContainer container){
//        求输出语句
        String output1 = String.format("Put          " + i);
        String output2 = String.format(
                "    &{[" + container.getContainer().get(0).getVal() + " " +
                        container.getContainer().get(1).getVal() + " " +
                        container.getContainer().get(2).getVal() + " " +
                        container.getContainer().get(3).getVal() + " " +
                        container.getContainer().get(4).getVal() + "] " +
                        container.getMAXSIZE() +
                        " " + container.getreadIndex() + " " + container.getwriteIndex());
        StringBuffer spaces = new StringBuffer(" ");
        for(int bit = 0; bit < bits; bit++){
            spaces.append(" ");
        }
        String output3 = new String(spaces);
        String output = String.format(output1 + output3 + output2);

        return output;
    }

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName());
        int tm;
        int bits;
        Random rand = new Random();
        // 要求每个0 ~ 500ms写入一个数, 故在0~500间取随机数来调用sleep
        // bits用于求位数，对齐用
        for(int i = 1000; i > 0; i--){
            synchronized (container) {
                bits = bitsCount(i);
                tm = rand.nextInt(501);     // 随机取阻塞时间
                Product product = new Product(i);
                int temp = container.getwriteIndex();
                container.push(product);
                if(temp != container.getwriteIndex()) {
                    System.out.println(getStr(product, i, bits, container));
                }else{
                    i++;
                }
            }
            try {
//                sleep应在锁外调用
                Thread.sleep(tm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
