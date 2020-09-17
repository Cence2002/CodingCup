package hu.johetajava;

/*enum Directions {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE
}*/

class Direction {
    static final int NONE = -1;
    static final int RIGHT = 0;
    static final int UP = 1;
    static final int LEFT = 2;
    static final int DOWN = 3;

    /*public static Directions getDirectionBySign(char sign) {
        switch (sign) {
            case '>':
                return Directions.RIGHT;
            case '<':
                return Directions.LEFT;
            case 'v':
                return Directions.DOWN;
            case '^':
                return Directions.UP;
            default:
                return Directions.NONE;
        }
    }

    public static Directions getDirectionByName(String name) {
        try {
            return Directions.valueOf(name);
        } catch (Exception e) {
            return Directions.NONE;
        }
    }

    public static Directions getDirection(int index) {
        switch (index % 4) {
            case 0:
                return Directions.RIGHT;
            case 1:
                return Directions.UP;
            case 2:
                return Directions.LEFT;
            case 3:
                return Directions.DOWN;
            default:
                return null;
        }
    }

    public static int getIndex(Directions direction) {
        switch (direction) {
            case RIGHT:
                return RIGHT;
            case UP:
                return UP;
            case LEFT:
                return LEFT;
            case DOWN:
                return DOWN;
            default:
                return NONE;
        }
    }
*/
    public static int turn(int direction, int dir) {
        return (direction + dir + 4) % 4;
    }

    public static int getDirectionBySign(char sign) {
        switch (sign) {
            case '>':
                return RIGHT;
            case '<':
                return LEFT;
            case 'v':
                return DOWN;
            case '^':
                return UP;
            default:
                return NONE;
        }
    }

    public static String getDirectionName(int direction) {
        switch (direction) {
            case RIGHT:
                return "RIGHT";
            case UP:
                return "UP";
            case LEFT:
                return "LEFT";
            case DOWN:
                return "DOWN";
            default:
                return "NONE";
        }
    }

    public static int getDirectionByName(String name) {
        switch (name) {
            case "RIGHT":
                return RIGHT;
            case "UP":
                return UP;
            case "LEFT":
                return LEFT;
            case "DOWN":
                return DOWN;
            default:
                return NONE;
        }
    }
}