package threadConLifeGame;

public class CellMap {
    private int[][] Map;
    private int height;
    private int width;

    public CellMap(int Height, int Width){
        Map = new int[Height][Width];
    }

    public void setMap(int i, int j, int value) {
        Map[i][j] = value;
    }

    public int getValue(int i, int j){
        return Map[i][j];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[][] getMap() {
        return Map;
    }


}
