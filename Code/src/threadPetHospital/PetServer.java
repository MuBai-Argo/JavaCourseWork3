package threadPetHospital;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetServer implements Runnable{
    // 为了能让一个服务端能同时为多个客户端提供服务，应采用多线程机制，每个客户端的请求都由一个独立的
    // 线程进行处理(将accept方法接收到的socket存入线程池)。
    private Socket socket;
    private int clientNo;
    private boolean ifClose = false;
    private InputStream is;
    private ByteArrayOutputStream os;
    private OutputStream sos;
    private Connection connection;
    private Statement statement;

    public PetServer(Socket socket, int clientNo){
        this.socket = socket;
        this.clientNo = clientNo;
    }

    @Override
    public void run(){
        try {
            int len = 0;
            String SQL;
            connection = (new PetDatabase()).Usedatabase();
            // 获取Statement，向数据库发送指令的对象
            this.statement = connection.createStatement();
            // 从socket中获取客户端的输入流
            is = socket.getInputStream();
            System.out.println("获取到来自客户端的输入流");
            // 任务1
            byte[] buffer = new byte[1024];

// 就是这个地方有问题 len = 41 os为空
            os = new ByteArrayOutputStream();
            System.out.println("检查InputStream的内容"+is.toString());
            while((len = is.read(buffer)) != -1){
                // 通过缓冲区将输入流中的数据读入输出流中
                // buffer中当前内容为 [83, 69, 76, 69, 67, 84, 32, 42, 32, 70, 82, 79, 77, 32, 80, 69, 84, 32, 87, 72, 69, 82, 69, 32, 111, 110, 97, 109, 101, 32, 61, 32, 34, 90, 101, 97, 108, 100, 97, 34, 59]
                // 当前buffer中内容与client中的os中的字节流内容一致， 说明在tcp传输过程中内容没有丢失。即，问题处在os对buffer的读取上
                String msgfromclient = new String(buffer, 0, len);
                System.out.println("检查缓冲区内当前的内容" + msgfromclient);
                os.write(buffer, 0, len);
            }
            System.out.println(os.toString());

// 以上为接受客户端的数据
// 接下来将向客户端反馈结果
            // 当前os中是从client中传入的sql语句
            // 显示sql语句

            SQL = os.toString();
            // 验证sql语句无误后，将其传入statement
            // 2
            // 如何区分SQL语句的更新和查询？
            // 取SQL语句的第一个字符，若为I则是更新语句，若为S则为查询语句。
            if(SQL.equals("CloseServer")){
                // 关闭服务端
                ifClose = true;
            }else if(SQL.charAt(0) == 'I'){
                // 更新语句
                int effectlines = statement.executeUpdate(SQL);
                // 显示受影响的行数
                System.out.println(effectlines);
            }else if(SQL.charAt(0) == 'S'){
                // 为查询语句
                ResultSet resultSet = statement.executeQuery(SQL);
                // 输出流由客户端socket获取
                sos = socket.getOutputStream();
                if(!resultSet.isBeforeFirst()){
                    // 没有第一行，故返回的结果集为空
                    System.out.println("查无此结果");// os是用来接收从client传入server的数据的输出流，此处应当将os改成sos
                    sos.write("Nothing".getBytes(StandardCharsets.UTF_8));
                }
                while(resultSet.next()){
                    // 应直接向client传递result结果集
                    String Qresult = String.format("宠物信息:\t名称 " + resultSet.getObject("petname") + " 种类 " + resultSet.getObject("kind")
                            + " 生日 " + resultSet.getObject("brith") + " 品种 " + resultSet.getObject("pettype") + " 体重/斤 " + resultSet.getObject("weight")
                            + " 主人姓名 " + resultSet.getObject("oname") + " 主人电话 " + resultSet.getObject("otelephone") + " 就诊时间 " + resultSet.getObject("ctime"));
                    System.out.println(Qresult);
                    // 向client传递结果集(输入流sis和输出流sos)
                    sos.write(Qresult.getBytes());   // 暂时只能做到逐条传递// os是用来接收从client传入server的数据的输出流，此处应当将os改成sos
                    sos.write("\n".getBytes());      // 若有多条返回结果用换行符进行分行显示
                }
                //sos.write("收到查询需求，显示查询结果".getBytes(StandardCharsets.UTF_8));
                System.out.println("收到查询需求，显示查询结果");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }finally{
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os !=null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (sos !=null)
                    sos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    public static void main(String[] args) {
        ServerSocket ss = null;
        int clientNo = 1;
        try {
            ss = new ServerSocket(8899);
            // 创建线程池
            ExecutorService executor = Executors.newCachedThreadPool();
            while(true){
                Socket socket = ss.accept();
                executor.execute(new PetServer(socket, clientNo));
                clientNo++;
                // 暂时还没想到怎么让ServerSocket在多线程的情况下随GUI的关闭而停止循环
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (ss != null) {
                    ss.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}

