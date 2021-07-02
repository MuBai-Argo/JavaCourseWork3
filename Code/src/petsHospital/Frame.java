package petsHospital;

import scPethospital.PetClient;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Frame extends JFrame implements ActionListener {
    private Connection connection;
    private Statement state;
    private JButton Add, Search;
    private JPanel backG, centerPanel, bottom;
    private JTextArea petname, kind, brith, type, weight, ownername, ownerphone, time, Searchresult;

    Frame(){
        connection = (new PetHospitalDatbase()).Usedatabase();
        // 获取Statement，向数据库发送指令的对象
        try {
            this.state = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        setSize(1000, 600);
        setLocation(0, 0);
        setTitle("PetHospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 设置布局
        backG = new JPanel(new BorderLayout(0, 0));
        bottom = new JPanel();
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setSize(10, 10);
        getContentPane().add(backG);
        backG.add(centerPanel, "Center");
        backG.add(bottom, "South");

        // 设置按键
        Add = new JButton("添加就诊记录");
        Search = new JButton("查询就诊记录");
        bottom.add(Add);
        bottom.add(Search);

        // 设置文本域
        JTextArea petnametext = new JTextArea("请输入宠物的名称");
        petnametext.setEditable(false);
        petname = new JTextArea();

        JTextArea kindtext = new JTextArea("请输入宠物的种类");
        kindtext.setEditable(false);
        kind = new JTextArea();

        JTextArea brithtext = new JTextArea("请输入宠物的生日");
        brithtext.setEditable(false);
        brith = new JTextArea();

        JTextArea typetext = new JTextArea("请输入宠物的品种");
        typetext.setEditable(false);
        type = new JTextArea();

        JTextArea weighttext = new JTextArea("请输入宠物的重量");
        weighttext.setEditable(false);
        weight = new JTextArea();

        JTextArea ownernametext = new JTextArea("请输入主人的名字");
        ownernametext.setEditable(false);
        ownername = new JTextArea();

        JTextArea ownerphonetext = new JTextArea("请输入主人的电话");
        ownerphonetext.setEditable(false);
        ownerphone = new JTextArea();

        JTextArea timetext = new JTextArea("请输入就诊时间");
        timetext.setEditable(false);
        time = new JTextArea();

        centerPanel.add(petnametext);
        centerPanel.add(petname);

        centerPanel.add(kindtext);
        centerPanel.add(kind);

        centerPanel.add(brithtext);
        centerPanel.add(brith);


        centerPanel.add(typetext);
        centerPanel.add(type);


        centerPanel.add(weighttext);
        centerPanel.add(weight);

        centerPanel.add(ownernametext);
        centerPanel.add(ownername);

        centerPanel.add(ownerphonetext);
        centerPanel.add(ownerphone);

        centerPanel.add(timetext);
        centerPanel.add(time);

        Searchresult = new JTextArea("此处显示查询结果");
        centerPanel.add(Searchresult);

        // 关联监听器
        Add.addActionListener(this);
        Search.addActionListener(this);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //操作结束，关闭数据库连接
                closed();
                System.exit(0);//退出系统
            }
        });

    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == Add){
            // 连接数据库，传入sql指令（INSERT）
            Pet pet1 = new Pet();
            pet1.setName(petname.getText());
            pet1.setBirth(brith.getText());
            pet1.setKind(kind.getText());
            pet1.setType(type.getText());
            pet1.setOname(ownername.getText());
            pet1.setOtelephone(ownerphone.getText());
            pet1.setTime(time.getText());
            pet1.setWeight(weight.getText());

            String SQL = String.format("Insert PET(petname, kind, weight, pettype, oname," +
                    "brith, otelephone, ctime)value(\"" + pet1.getName() + "\",\"" + pet1.getKind() + "\",\"" + pet1.getWeight() + "\",\"" + pet1.getType() + "\",\"" + pet1.getOname()
                     + "\",\"" + pet1.getBirth() + "\",\"" + pet1.getOtelephone() + "\",\"" + pet1.getTime()+"\");");
            try {
                System.out.println(SQL);
                int changedlines = state.executeUpdate(SQL);
                System.out.println("插入成功，一共" + changedlines + "行受影响");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else if(e.getSource() == Search){
            // 连接数据库，传入sql指令Select并显示查询结果
            // 若查询不到，则显示对话框，“查无此纪录”
            // 通过主人的姓名或者电话进行查询
            if(ownerphone.getText().equals("") && (!ownername.getText().equals(""))){
                // 通过姓名查询
                String SQL = String.format("SELECT * FROM PET WHERE oname = \"" + ownername.getText() + "\";");
                try {
                    String resultset = "";
                    System.out.println(SQL);
                    ResultSet resultSet = state.executeQuery(SQL);
                    if(!resultSet.isBeforeFirst()){
                        // 没有第一行，故返回的结果集为空
                        // 弹出窗口
                        JOptionPane.showMessageDialog(null, "请确认要查询的信息", "查无此记录",JOptionPane.ERROR_MESSAGE);
                    }else{
                        while(resultSet.next()){
                            resultset += String.format("宠物信息:\t名称 " + resultSet.getObject("petname") + " 种类 " + resultSet.getObject("kind")
                                    + " 生日 " + resultSet.getObject("brith") + " 品种 " + resultSet.getObject("pettype") + " 体重 " + resultSet.getObject("weight")
                                    + " 主人姓名 " + resultSet.getObject("oname") + " 主人电话 " + resultSet.getObject("otelephone") + " 就诊时间 " + resultSet.getObject("ctime")) + "\n";
                        }
                        Searchresult.setText(resultset);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }else if(ownername.getText().equals("") && (!ownerphone.getText().equals(""))){
                // 通过电话查询
                String SQL = String.format("SELECT * FROM PET WHERE otelephone = \"" + ownerphone.getText() + "\";");
                try {
                    String resultset = "";
                    System.out.println(SQL);
                    ResultSet resultSet = state.executeQuery(SQL);
                    if(!resultSet.isBeforeFirst()){
                        // 没有第一行，故返回的结果集为空
                        // 弹出窗口
                        JOptionPane.showMessageDialog(null, "请确认要查询的信息", "查无此记录",JOptionPane.ERROR_MESSAGE);
                    }else{
                    while(resultSet.next()){
                       resultset += String.format("宠物信息:\t名称 " + resultSet.getObject("petname") + " 种类 " + resultSet.getObject("kind")
                                + " 生日 " + resultSet.getObject("brith") + " 品种 " + resultSet.getObject("pettype") + " 体重 " + resultSet.getObject("weight")
                                + " 主人姓名 " + resultSet.getObject("oname") + " 主人电话 " + resultSet.getObject("otelephone") + " 就诊时间 " + resultSet.getObject("ctime")) + "\n";
                    }
                        Searchresult.setText(resultset);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }else{
                System.out.println("输入信息有误");
            }
        }
    }

    public void closed(){
        try {
            state.close();
            connection.close();
//            PetClient client = new PetClient();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
    public static void main(String[] args) {
        // 创建GUI界面同时连接数据库
        Frame f = new Frame();



    }
}
