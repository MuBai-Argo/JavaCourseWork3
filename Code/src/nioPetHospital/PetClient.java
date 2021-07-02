package nioPetHospital;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

// 当前的问题在于运行的顺序
// 查询问题要求客户端先向服务端传入数据，并等待直到服务端将反馈写还给客户端，然后再开始接收
// 当前客户端会在传入数据后立即开始试图接收数据, 这导致反馈无法被传回数据库，反而是客户端将过早的切断连接
// 目前的想法是将传输查询结果反馈回客户端创建一个独立的通道，并在查询的末尾进行调用
// 否决上述方法，应当创建一个线程专门监听服务端发来的消息


public class PetClient {
    private String resultset;
    private String SQL;
    private SocketChannel socketChannel;
    private Selector selector;

    // 客户端的主要操作是: 1.向服务端写入数据 2.从服务端接受反馈并体现在GUI上
    PetClient(Pet pet) {// 更新
        // 构造器根据所给参数创建SQL语句
        // 分别为查询语句和更新语句创建不同的构造器
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel = null;
        try {
            selector = Selector.open();
            // 创建客户端通道
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);


            // 客户端向服务端写入数据
            SQL = String.format("Insert PET(petname, kind, weight, pettype, oname," +
                    "brith, otelephone, ctime)value(\"" + pet.getName() + "\",\"" + pet.getKind() + "\",\"" + pet.getWeight() + "\",\"" + pet.getType() + "\",\"" + pet.getOname()
                    + "\",\"" + pet.getBirth() + "\",\"" + pet.getOtelephone() + "\",\"" + pet.getTime() + "\");");
            // 检验SQL语句
            System.out.println("检验SQL语句" + SQL);
            // 将SQL语句写入通道
            buffer.put(SQL.getBytes());
            try {

                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();


            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("插入成功");

            // 更新语句中客户端无需从服务端接收数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PetClient(){
        CloseServer();
    }


    public void CloseServer(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel = null;
        try {
            selector = Selector.open();
            // 创建客户端通道
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);


            // 客户端向服务端写入数据
            SQL = String.format("CloseServer");
            // 检验SQL语句
            System.out.println("检验SQL语句" + SQL);
            // 将SQL语句写入通道
            buffer.put(SQL.getBytes());
            try {

                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
        e.printStackTrace();
    }
    }

    PetClient(String str) {
        // 根据主人姓名or电话进行查询
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel = null;
        try {
            selector = Selector.open();
            // 创建客户端通道
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            // 客户端向服务端写入数据
            if (str.charAt(0) == '1') {
                // 假如传入的是电话
                SQL = String.format("SELECT * FROM PET WHERE otelephone = \"" + str + "\";");
            } else {
                // 假如传入的是姓名
                SQL = String.format("SELECT * FROM PET WHERE oname = \"" + str + "\";");
            }
            // 检验SQL语句
            System.out.println("检验SQL语句" + SQL);
            // 将SQL语句写入通道
            buffer.put(SQL.getBytes());


            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();


            // 写入后还需接收来自服务端的反馈(读数据)



        } catch (IOException e) {
            e.printStackTrace();
        }
        // 客户端应在读取从服务端返回的消息后再关闭。
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public Selector getSelector() {
        return selector;
    }


}

