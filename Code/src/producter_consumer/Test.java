package producter_consumer;

public class Test {
    public static void main(String[] args) {
        SynContainer syncontainer = new SynContainer();
        Thread ThreadA = new Thread(new ProductorA(syncontainer));
        Thread ThreadB = new Thread(new ProductorB(syncontainer));
        Thread ThreadC = new Thread(new Consumer(syncontainer));
        ThreadA.start();
        System.out.println("A");
        ThreadB.start();
        System.out.println("B");
        ThreadC.start();
        System.out.println("C");
    }
}
