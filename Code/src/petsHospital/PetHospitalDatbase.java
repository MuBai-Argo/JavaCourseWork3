package petsHospital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PetHospitalDatbase {

    PetHospitalDatbase(){}

    public Connection Usedatabase(){
        try {
            // 加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            String Url = "jdbc:mysql://localhost:3306/PetHospital?useUnicode = true&characterEncoding=utf-8&useSSL=true";
            String username = "root";
            String password = "Nicht.vergessen";
            // 获取连接数据库对象
            Connection connection = DriverManager.getConnection(Url, username, password);

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



}
