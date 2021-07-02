package scPethospital;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;


public class Frame extends JFrame implements ActionListener {
    final private JButton Add, Search;
    final private JPanel backG, centerPanel, bottom;
    final private JTextArea petname, kind, brith, type, weight, ownername, ownerphone, time, Searchresult;
    PetClient client;
    Frame(){




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

        Searchresult = new JTextArea("此处显示查询结果");

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

        centerPanel.add(Searchresult);

        // 关联监听器
        Add.addActionListener(this);
        Search.addActionListener(this);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //操作结束，关闭数据库连接
                // closed();
                PetClient client = new PetClient();
                System.exit(0);//退出系统
            }
        });

    }




    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == Add){
            // 连接数据库，传入sql指令（INSERT）
            Pet pet = new Pet();
            pet.setName(petname.getText());
            pet.setBirth(brith.getText());
            pet.setKind(kind.getText());
            pet.setType(type.getText());
            pet.setOname(ownername.getText());
            pet.setOtelephone(ownerphone.getText());
            pet.setTime(time.getText());
            pet.setWeight(weight.getText());
            // 创建客户端
            client = new PetClient(pet);
            // 更新操作无需返回结果
            System.out.println("更新成功");

        }else if(e.getSource() == Search){
            // 查询语句
            String resultset = null;
            if(ownername.getText().equals("") && (!ownerphone.getText().equals(""))){
                client = new PetClient(ownerphone.getText());
                resultset = client.getResultset();
            }else if(ownerphone.getText().equals("") && (!ownername.getText().equals(""))){
                client = new PetClient(ownername.getText());
                // 获取result后通过setText方法将查询结果显示到文本域中
                resultset = client.getResultset();
            }
            System.out.println(resultset);
            // 假如获取的结果是Noting, 则弹出弹窗显示错误
            if(resultset.equals("Nothing")){
                JOptionPane.showMessageDialog(null, "请确认要查询的信息", "查无此记录",JOptionPane.ERROR_MESSAGE);
            }else{// 在Frame中增设一个文本域用来填写查询结果
                //System.out.println("在文本域中显示查询结果");
                Searchresult.setText(resultset);
            }


        }

    }

//    public void closed(){
//        try {
//            state.close();
//            connection.close();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//
//    }
    public static void main(String[] args) {
        // 创建GUI界面同时连接数据库
        Frame f = new Frame();



    }
}
