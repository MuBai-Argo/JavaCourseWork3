package threadConLifeGame;

import javax.swing.*;
import java.awt.*;

public class GraphicDraws extends JPanel {
    private MapMatrix map;
    int unitW;
    int unitH;
    int lx, ly;
    GraphicDraws(MapMatrix Nmap, int x, int y){
        map = Nmap;
        lx = x;
        ly = y;
        if(lx < 512) {
            unitH = 512 / Nmap.getHeighth();
            unitW = 512 / Nmap.getWidth();
        }
        else{
            unitH = 1024 / Nmap.getHeighth();
            unitW = 1024 / Nmap.getWidth();
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        map.setCount(0);
        for (int row = 0; row < map.getHeighth(); row++){
            for (int col = 0; col < map.getWidth(); col++) {
                if (map.getMapunit(row, col) == 255) {
                    map.setCount(map.getCount() + 1);
                    g.fillRect(col * unitW, row * unitH, unitW, unitH);
                }
                else {
                    g.drawRect(col * unitW, row * unitH, unitW, unitH);
                }
            }
        }
    }

}
