package threadConLifeGame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class Frame extends JFrame implements ActionListener {
    private MapMatrix map;
    private int distence;
    private int lx, ly;
    private int unitW;
    private int unitH;
    private JButton Start, Continue, Exit, Pause;
    private JPanel backG, centerPanel, bottom;
    private boolean ifContinue;

    Frame(MapMatrix Nmap){
        map = Nmap;
        ly = map.getHeighth();
        lx = map.getWidth();
        if(lx < 64){
            distence = 5;
        }
        else if(lx < 128){
            distence = 10;
        }
        else if(lx < 512){
            distence = 20;
        }
        else{
            distence = 20;
        }

        if(lx < 512) {
            unitH = 512 / Nmap.getHeighth();
            unitW = 512 / Nmap.getWidth();
        }
        else{
            unitH = 1024 / Nmap.getHeighth();
            unitW = 1024 / Nmap.getWidth();
        }

        setSize((lx + distence / 5) * unitW, (ly + distence)  * unitH);

        setLocation(0, 0);
        setTitle("Life_game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        backG = new JPanel(new BorderLayout());
        bottom = new JPanel();
        centerPanel = new GraphicDraws(map, lx, ly);
        //centerPanel = new GraphicDraws(map, lx, ly);

        setContentPane(backG);
        //backG.add(centerPanel, "Center");
        backG.add(bottom, "South");

        Continue = new JButton("Continue");
        Exit = new JButton("Exit");
        Start = new JButton("Start");
        Pause = new JButton("Pause");
        bottom.add(Start);
        bottom.add(Continue);
        bottom.add(Pause);
        bottom.add(Exit);
        setVisible(true);

        Continue.addActionListener(this);
        Exit.addActionListener(this);
        Start.addActionListener(this);
        Pause.addActionListener(this);
        timer.start();
    }

    Timer timer = new Timer(2000, this);


    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == Start){
            backG.add(centerPanel, "Center");
            setVisible(true);
        }
        else if(e.getSource() == Continue){
            // ????????????Continue??????????????????????????????????????????????????????
//            change();
//            newMap();
            ifContinue = true;
        }
        else if(e.getSource() == Exit){
            System.out.println("????????????");
            dispose();
            System.exit(0);
        }else if(e.getSource() == Pause){
            ifContinue = false;
        }
        if(ifContinue){
            change();
            newMap();
        }

//        // ??????map???????????????

//      ????????????Continue???????????????????????????????????????????????????GUI???????????????????????????2????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        System.out.println(map.getCount());
        timer.start();
//        java.util.Timer t = new java.util.Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println(count);
//                count = 0;
//            }
//        }, 2000);


    }


    private void change() {
        map.nextMap();
    }

    private void newMap() {
        repaint();
    }


    public static void main(String[] args) throws FileNotFoundException {
        MapMatrix map = new MapMatrix("source\\16x16.pgm");
        map.firstMap();
        Frame f = new Frame(map);
    }
}

