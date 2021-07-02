package scPethospital;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PetClient{
    Socket socket;
    OutputStream os;
    InputStream cis;
    ByteArrayOutputStream cos;
    String resultset;

    PetClient(Pet pet) {// 更新
        // 构造器根据所给参数创建SQL语句
        // 分别为查询语句和更新语句创建不同的构造器
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            socket = new Socket(localHost, 8899);
            // 客户端向服务端传输数据
            os = socket.getOutputStream();
            String SQL = String.format("Insert PET(petname, kind, weight, pettype, oname," +
                    "brith, otelephone, ctime)value(\"" + pet.getName() + "\",\"" + pet.getKind() + "\",\"" + pet.getWeight() + "\",\"" + pet.getType() + "\",\"" + pet.getOname()
                    + "\",\"" + pet.getBirth() + "\",\"" + pet.getOtelephone() + "\",\"" + pet.getTime() + "\");");
            System.out.println(SQL);
            os.write(SQL.getBytes(StandardCharsets.UTF_8));
            System.out.println("插入成功");

            // 更新语句中客户端无需从服务端接收数据

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    PetClient(String str) {
        // 根据主人姓名or电话进行查询
        try {
            InetAddress localHost = null;
            String SQL;
            localHost = InetAddress.getByName("127.0.0.1");
            socket = new Socket(localHost, 8899);


// 客户端向服务端传输数据(最好把os的申请放在SQL语句创建完成之后)
            if (str.charAt(0) == '1') {
                // 假如传入的是电话
                SQL = String.format("SELECT * FROM PET WHERE otelephone = \"" + str + "\";");
            } else {
                // 假如传入的是姓名
//          现在的io流内容是 查询主人名为Zealda [83, 69, 76, 69, 67, 84, 32, 42, 32, 70, 82, 79, 77, 32, 80, 69, 84, 32, 87, 72, 69, 82, 69, 32, 111, 110, 97, 109, 101, 32, 61, 32, 34, 90, 101, 97, 108, 100, 97, 34, 59]
                SQL = String.format("SELECT * FROM PET WHERE oname = \"" + str + "\";");
            }
            System.out.println(SQL);
            os = socket.getOutputStream();
            os.write(SQL.getBytes());
            socket.shutdownOutput();




// 客户端从服务端接受数据(若查无结果则返回"Nothing")
            cis = socket.getInputStream();
            cos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = cis.read(buffer)) != -1){
                cos.write(buffer, 0, len);
            }
            System.out.println(cos.toString());
            // 获取查询结果
            resultset = cos.toString();
            // 通过getResultset函数在Frame中将查询结果存入GUI界面

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (cis != null) {
                    cis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                cos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PetClient(){
        CloseServer();
    }

    public String getResultset() {
        return resultset;
    }


    public void CloseServer(){
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            socket = new Socket(localHost, 8899);
            // 客户端向服务端传输数据
            os = socket.getOutputStream();
            os.write("CloseServer".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
