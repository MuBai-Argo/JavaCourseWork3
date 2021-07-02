package nioPetHospital;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class PetServer {
    // 为了能让一个服务端能同时为多个客户端提供服务，应采用多线程机制，每个客户端的请求都由一个独立的
    // 线程进行处理(将accept方法接收到的socket存入线程池)。
    private boolean ifClose = false;
    private Connection connection;
    private Statement statement;
    // 和Selector一起用的时候，Channel必须处于非阻塞状态下
    private Selector selector;
    String Qresult = "";
    SocketChannel sc;


    public PetServer() {

    }

    public void handleAccept(SelectionKey key) {
        // 接收来自客户端的连接
        try {
            // 由选择器获取与选择器关联的ServerSocketChannel
            ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
            // 监听新接入的连接，若没有则返回null
            sc = ssChannel.accept();
            if (sc != null) {
                sc.configureBlocking(false);
                // register方法的第二个参数表示Selector在监听Channel时对什么感兴趣（Connect\Accept\Read\Write）
                // 服务端对读入客户端数据敏感，并将向客户端写入数据（写数据伴随读操作进行，故无需对写操作敏感）
                sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRead(SelectionKey key) {
        // 读通道传输信息 从客户端读入数据
        // NIO读数据步骤包括： 1. 获取缓冲区 2. 获取通道 3. 通道调用read方法将通道中数据存入缓冲区 4. 缓冲区数据字符串化（直接调用ByteBuffer的array方法或者通过字节数组进行读取）
        // 由选择器分配客户端通道
        int len = 0;
        String SQL = "";
        SocketChannel sc = (SocketChannel) key.channel();
        // ByteBuffer buffer = (ByteBuffer) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (((len = sc.read(buffer)) > 0)) {
                // 将读取位置调整到开始位置
                buffer.flip();
                // 读取缓冲区内容，获取从客户端传来的SQL语句
                SQL = new String(buffer.array(), 0, len);
                buffer.clear();
            }
            // 对获取的SQL语句进行验证
            System.out.println(SQL);
            // 获取SQL语句后，应当试图连接数据库
            // 首先应当获取对数据库的连接
            connection = (new PetDatabase()).Usedatabase();
            statement = connection.createStatement();
            // 根据不同的SQL语句类型进行SQL操作
            if (SQL.equals("CloseServer")) {
                // 关闭服务端
                ifClose = true;
            } else if (SQL.charAt(0) == 'I') {
                // 更新语句
                int effectlines = statement.executeUpdate(SQL);
                // 显示受影响的行数
                System.out.println("受影响行数为" + effectlines + "行");
            } else if (SQL.charAt(0) == 'S') {
                ResultSet resultSet = statement.executeQuery(SQL);
                // Qresult为查询结果的字符串形式

                if (!resultSet.isBeforeFirst()) {
                    // 没有第一行，故返回的结果集为空
                    System.out.println("查无此结果");
                    Qresult = "Nothing";
                    // 此处应向客户端写入Qresult
                    handleWrite(key, Qresult, sc);
                } else {
                    while (resultSet.next()) {
                        // 获取将结果集字符串化
                        Qresult += String.format("宠物信息:\t名称 " + resultSet.getObject("petname") + " 种类 " + resultSet.getObject("kind")
                                + " 生日 " + resultSet.getObject("brith") + " 品种 " + resultSet.getObject("pettype") + " 体重/斤 " + resultSet.getObject("weight")
                                + " 主人姓名 " + resultSet.getObject("oname") + " 主人电话 " + resultSet.getObject("otelephone") + " 就诊时间 " + resultSet.getObject("ctime") + "\n");

                    }
                    // 验证结果集
                    System.out.println(Qresult);
                    // 向客户端写入数据
                    // 将Qresult写入客户端
                    handleWrite(key, Qresult, sc);
                    System.out.println("客户端数据读取完成");
                }
                Qresult = "";
            }
        } catch (IOException | SQLException e) {
            // 若当前客户端离线 将当前通道从选择器中取消掉 并关闭通道
            try {
                System.out.println(sc.getRemoteAddress() + "离线");
                key.channel();
                sc.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    public void handleWrite(SelectionKey key, String Qresult, SocketChannel SChannel) {
        // 向客户端写入信息
        // 可以通过attach方法（或在注册时附着）将Buffer或者其他对象附着在SelectionKey上
        // 通过attachment方法可以进行取用
        // 但是我没明白为什么attach和attachment的具体用法，既然由通道可以直接传输数据为什么还要用关联的缓冲区呢？
        // 写数据的流程： 1.获取通道 2.获取缓冲区并与通道关联 3.利用put方法将字符串读入缓冲区 4.利用read方法将缓冲区数据读入通道
        // handleWrite方法的目的时将已经获得的SQL语句结果（"Nothing" or 查询结果）反馈回客户端
        try {
            ByteBuffer buffer = ByteBuffer.wrap(Qresult.getBytes());
            SChannel.write(buffer);

            System.out.println("客户端数据写回完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void selector() {
        selector = null;
        ServerSocketChannel ssc = null;
        try {
            // 创建选择器
            selector = Selector.open();
            // 创建服务端通道哦
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress((8899)));
            ssc.configureBlocking(false);
            // ServerSocketChannel对接受就绪行为敏感
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 一旦向Selector注册了一个或多个通道，就可以调用几个重载的select（）方法，从而返回所感兴趣的事件已经准备就绪的通道
                if (selector.select(3000) == 0) {
                    // select返回的int值表示多少通道已经就绪
                    // 通道就绪后，计科通过调用selector的selectedKeys = selectedKeys()来对已选择键集中的就绪通道进行访问
                    // 假如没有就绪的通道则跳过此次循环
                    System.out.println("Loading...");
                    if(ifClose){
                        break;
                    }
                    continue;
                }
                // 若由通道就绪则通过SelectionKey判断属于哪种操作
                // SelectionKey当像Selector注册Channel时，register方法会返回一个SelectionKey对象
                // 这个对象包括了interest集合、ready集合（已准备就绪的操作的集合）、Channel、Selector、附加的对象
                // 通过 int readySet = selectionKet.readyOps()可以会ready集合进行访问
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();   // 将返回的SelectionKey迭代器化，并逐一进行相应的操作
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // 检验Channel中已就绪的操作
                    if (key.isAcceptable()) {
                        // 接收来自客户端的通道
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        // 从客户端读入数据
                        handleRead(key);
                    }
                    // 执行完一个迭代对象后，将其移出迭代器
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 任务结束关闭通道和选择器
                if (ssc != null) {
                    ssc.close();
                }
                if (selector != null) {
                    selector.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        PetServer server = new PetServer();
        server.selector();
    }

}

