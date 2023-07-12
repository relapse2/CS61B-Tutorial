package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class test {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            TERenderer ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);
            Engine engine = new Engine();
            TETile[][] world= engine.interactWithInputString(args[1]);
            ter.renderFrame(world);
            // DO NOT CHANGE THESE LINES YET ;)
        } else if (args.length == 2 && args[0].equals("-p")) { System.out.println("Coming soon."); }
        // DO NOT CHANGE THESE LINES YET ;)
    }
}
