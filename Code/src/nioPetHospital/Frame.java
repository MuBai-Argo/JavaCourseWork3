package nioPetHospital;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Frame extends JFrame implements ActionListener, Runnable {
    private JButton Add, Search;
    private JPanel backG, centerPanel, bottom;
    private JTextArea petname, kind, brith, type, weight, ownername, ownerphone, time, Searchresult;
    private PetClient client;
    private boolean iferror = false;

    Frame() {

    }

    public void CreateGUI() {
        setSize(1000, 800);
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
                new PetClient();
                System.exit(0);//退出系统
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == Add) {
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

        } else if (e.getSource() == Search) {
            // 查询语句
            String resultset = null;

            if (ownername.getText().equals("") && (!ownerphone.getText().equals(""))) {
                client = new PetClient(ownerphone.getText());
                getResultset();
            } else if (ownerphone.getText().equals("") && (!ownername.getText().equals(""))) {
                client = new PetClient(ownername.getText());
                // 获取result后通过setText方法将查询结果显示到文本域中
                getResultset();
            }
            if(iferror){
                JOptionPane.showMessageDialog(null, "请确认要查询的信息", "查无此记录", JOptionPane.ERROR_MESSAGE);
                iferror = false;
            }
        }


    }

    @Override
    public void run() {
        CreateGUI();
    }

    public void getResultset() {
        Thread thread = new Thread(new Runnable() {
            String resultset = "";
            @Override
            public void run() {
                try {
                    System.out.println("线程开始运行");
                    if ((client.getSelector()).select() > 0) {
                        Iterator<SelectionKey> iterator = (client.getSelector()).selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            if (key.isReadable()) {
                                // 获取服务端
                                SocketChannel sc = (SocketChannel) key.channel();
                                int len = 0;
                                ByteBuffer buff = ByteBuffer.allocate(1024);
                                while ((len = sc.read(buff)) > 0) {
                                    System.out.println(buff.array().toString());
                                    buff.flip();
                                    resultset += new String(buff.array(), 0, len);
                                    buff.clear();
                                }
                                iterator.remove();
                                // 检验查询结果
                                System.out.println("检验查询结果" + resultset);
                                if (resultset != null) {
                                    if (resultset.equals("Nothing")) {
                                        iferror = true;
                                    } else {// 在Frame中增设一个文本域用来填写查询结果
                                        //System.out.println("在文本域中显示查询结果");
                                        Searchresult.setText(resultset);
                                        System.out.println("线程运行完成且resultset不为 null");
                                    }
                                }
                                System.out.println("线程运行完成");
                                resultset = "";
                            }
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        thread.start();
        while(thread.isAlive()){}
        System.out.println("数据查询完成");
    }


    public static void main(String[] args) {
        // 创建GUI界面同时连接数据库
        Thread threadFrame1 = new Thread(new Frame());
        Thread threadFrame2 = new Thread(new Frame());
        threadFrame1.start();
        threadFrame2.start();
    }
}
