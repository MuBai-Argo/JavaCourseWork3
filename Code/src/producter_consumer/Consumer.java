package producter_consumer;

import java.util.Random;

public class Consumer implements Runnable{
    private SynContainer container;
    private Product consume;
    Consumer(SynContainer SHAREONE){
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

    private synchronized String getStr(Product product, int bits, SynContainer container){
//        求输出语句
        String output1 = String.format("GET          " + product.getVal());
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
        int tm;
        Random rand = new Random();
        while(container.getTcount() <= 2000){
            synchronized (container) {
                tm = rand.nextInt(501);
                consume = container.pop();
                if (consume == null) {
                    continue;
                }
                int bits = bitsCount(consume.getVal());
                System.out.println(getStr(consume, bits, container));
            }
            try {
                Thread.sleep(tm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
