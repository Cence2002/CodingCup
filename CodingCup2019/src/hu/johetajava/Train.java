package hu.johetajava;

import java.util.ArrayList;

public class Train {
    public static ArrayList<Position> getTrainsPieces(int tick) {
        ArrayList<Position> pieces = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pieces.add(new Position(i + (World.tick % 50) * 3 - 14, 5));
            pieces.add(new Position(59 - (i + (World.tick % 50) * 3 - 14), 54));
            pieces.add(new Position(54, i + ((World.tick - 25) % 50) * 3 - 14));
            pieces.add(new Position(5, 59 - (i + ((World.tick - 25) % 50) * 3 - 14)));
        }


        return pieces;
    }

    public static ArrayList<Position> getTrainsWithOffset() {
        return getTrainsWithOffset(World.tick);
    }

    public static ArrayList<Position> getTrainsWithOffset(int tick) {
        ArrayList<Position> pieces = new ArrayList<>();
        for (int i = 3; i < 21; i++) {
            pieces.add(new Position(i + (tick % 50) * 3 - 14, 5));
            pieces.add(new Position(59 - (i + (tick % 50) * 3 - 14), 54));
            pieces.add(new Position(54, i + ((tick - 25) % 50) * 3 - 14));
            pieces.add(new Position(5, 59 - (i + ((tick - 25) % 50) * 3 - 14)));
        }
        return pieces;
    }

    public static ArrayList<Position> getTrainsPieces() {
        return getTrainsPieces(World.tick);
    }

}
