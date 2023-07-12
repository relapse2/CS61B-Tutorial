package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with some tiles.
     */

    public static void fillboard(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * pick a random tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }
    private static class position{      //需要static来可以有构造方法
        int x;
        int y;
        public position(int x,int y){
            this.x = x;
            this.y = y;
        }
        public position shift(int dx,int dy){
            return new position(this.x+dx,this.y+dy);
        }

    }

    public static void draw_hexcol(position p,TETile[][] tiles,int size,int num){
        if (num < 1){
            return;
        }
        draw_hexagon(p,size,tiles,randomTile());
        if(num > 1){
            position bottom_pos = p.shift(0,-2*size);
            draw_hexcol(bottom_pos,tiles,size,num-1);
        }
    }


    /**
     * build certain hexagons with random tile

     */
    public static void draw_hexagon(position p,int size,TETile[][] tiles,TETile tile){       //画六边形，理解如何实现主要在于在某个特定area内完成，寻找规律
        if (size < 2){
            return;
        }
        draw_hex_helper(p,size-1,size,tiles,tile);
    }
    public static void draw_hex_helper(position p,int b,int t,TETile[][] tiles,TETile tile){           //六边形的helper func
        position new_p = p.shift(b,0);
        draw_raw(new_p,tiles,tile,t);
        if(b>0){
            position next_p = p.shift(0,-1);
            draw_hex_helper(next_p,b-1,t+2,tiles,tile);
        }
        new_p = p.shift(b,-(2*b+1));
        draw_raw(new_p,tiles,tile,t);
    }

    public static void draw_raw(position p,TETile[][] tiles,TETile tile,int length){        //简化方法，分工模块化
        for(int dx = 0;dx < length; dx++){
            tiles[p.x+dx][p.y] = tile;
        }
    }

    /**
     draw hexagon world
     */
    public static void draw_world(TETile[][] tiles,int size,int tessize){
        position p = new position(0,35);
        fillboard(tiles);
        draw_hexcol(p,tiles,size,tessize);
        for(int i = 1;i<tessize;i++){
            p = p.shift(2*size-1,size);
            draw_hexcol(p,tiles,size,tessize+i);
        }
        for(int i = tessize-2;i>=0;i--){
            p = p.shift(2*size-1,-size);
            draw_hexcol(p,tiles,size,tessize+i);
        }
    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        draw_world(randomTiles,3,3);

        ter.renderFrame(randomTiles);
    }
}
