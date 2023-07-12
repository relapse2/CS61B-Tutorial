package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    private Random rand;
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private List<room> rooms;
    private String playerMovement, seedString="";
    private TETile[][] finalWorldFrame;

    public Engine() {
        rand = null;
        rooms = new ArrayList<>();
        playerMovement = "";
        seedString = "";
        finalWorldFrame = new TETile[WIDTH][HEIGHT];
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // seed the random generator
        int index = -1;
        do {
            index++;
            this.seedString += input.charAt(index);
        } while (input.charAt(index) != 's');


        Long seed = Long.parseLong(this.seedString.substring(1, this.seedString.length() - 1));
        rand = new Random(seed);

        // initialize tiles
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                finalWorldFrame[x][y] = Tileset.NOTHING;
            }
        }
        // generate rooms
        int numRooms = 6 + rand.nextInt(6);
        for (int i = 0; i < numRooms; i++) {
            room r = generateRoom();
            rooms.add(r);
            drawRoom(r);
        }
        //generate hallways
        union uf = new union(rooms.size());
        for (int i = 0; i < 50; i++) {
            int a = rand.nextInt(rooms.size());
            int b = rand.nextInt(rooms.size());
            room roomA = rooms.get(a);
            room roomB = rooms.get(b);
            if (!uf.isConnected(a, b)) {
                uf.connect(a, b);
                connect(roomA, roomB);
            }
        }

        for (int i = 0; i < numRooms; i++) {
            for (int j = 0; j < numRooms; j++) {
                if (!uf.isConnected(i, j)) {
                    uf.connect(i, j);
                    connect(rooms.get(i), rooms.get(j));
                }
            }
        }


        return finalWorldFrame;
    }
    //任意位置
    public Position randomPosition() {
        int x = 1 + rand.nextInt(WIDTH - 13);
        int y = 1 + rand.nextInt(HEIGHT - 13);
        return new Position(x, y);
    }
    //生成任意room
    public room generateRoom() {
        Position startPosition = randomPosition();
        int width = 6 + rand.nextInt(6);
        int height = 6 + rand.nextInt(6);
        room newRoom = new room(startPosition, width, height);
        for (room r : rooms) {
            if (overlaps(r, newRoom)) {
                return generateRoom();
            }
        }
        return newRoom;
    }
    public boolean overlaps(room a, room b) {
        for (Position p1 : b.getFloor()) {
            for (Position p2 : a.getFloor()) {
                if (Math.abs(p1.getX() - p2.getX()) <= 3 && Math.abs(p1.getY() - p2.getY()) <= 3) {
                    return true;
                }
            }
        }
        return false;
    }
    public void drawRoom(room r) {
        for (Position p : r.getFloor()) {
            finalWorldFrame[p.getX()][p.getY()] = Tileset.FLOOR;
        }
        for (Position p : r.getWalls()) {
            finalWorldFrame[p.getX()][p.getY()] = Tileset.WALL;
        }
    }
    //连接房间
    public void connect(room a,room b) {
        List<Position> spanA = a.getFloor();
        List<Position> spanB = b.getFloor();

        Position pointA = spanA.get(rand.nextInt(spanA.size()));
        Position pointB = spanB.get(rand.nextInt(spanB.size()));

        Position start = Position.compareX(pointA, pointB) < 0 ? pointA : pointB;  //进行比较看哪个开始
        Position end = start == pointA ? pointB : pointA;

        for (int col = start.getX() - 1; col < end.getX() + 2; col++) {
            for (int row = start.getY() - 1; row < start.getY() + 2; row++) {
                if (row == start.getY() && col >= start.getX() && col <= end.getX()) {
                    finalWorldFrame[col][row] = Tileset.FLOOR;
                } else if (finalWorldFrame[col][row] != Tileset.FLOOR ) {
                    finalWorldFrame[col][row] = Tileset.WALL;
                }
            }
        }
        Position corner = new Position(end.getX(), start.getY());
        start = Position.compareY(corner, end) < 0 ? corner : end;
        end = start == corner ? end : corner;

        for (int row = start.getY() - 1; row < end.getY() + 2; row++) {
            for (int col = start.getX() - 1; col < end.getX() + 2; col++) {
                if (col == start.getX() && row >= start.getY() && row <= end.getY()) {
                    finalWorldFrame[col][row] = Tileset.FLOOR;
                } else if (finalWorldFrame[col][row] != Tileset.FLOOR) {
                    finalWorldFrame[col][row] = Tileset.WALL;
                }
            }
        }
    }

}
