package byow.Core;
import java.util.List;
import java.util.ArrayList;

public class room {
    private Position pos;
    private int width, height;
    private List<Position> floor, walls;

    public room(Position startPosition, int width, int height) {
        this.pos = startPosition;
        this.width = width;
        this.height = height;

        this.floor = new ArrayList<>();
        this.walls = new ArrayList<>();

        int rowEnd = getY() + getHeight();
        int colEnd = getX() + getWidth();               //存入walls和floor，具体去重放到engine里自己写
        for (int row = getY() - 1; row < rowEnd + 1; row++) {
            for (int col = getX() - 1; col < colEnd + 1; col++) {
                if (row == getY() - 1 || row == rowEnd || col == getX() - 1 || col == colEnd) {
                    walls.add(new Position(col, row));
                } else {
                    floor.add(new Position(col, row));
                }
            }
        }
    }

    public List<Position> getFloor() {
        return this.floor;
    }
    public List<Position> getWalls() {
        return this.walls;
    }
    public int getX() {
        return this.pos.getX();
    }
    public int getY() {
        return this.pos.getY();
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
}