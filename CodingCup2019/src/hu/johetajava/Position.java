package hu.johetajava;


public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        Position pos2 = (Position) obj;
        return this.x == pos2.x && this.y == pos2.y;
    }

    public static Position getPositionByDirection(Position oldPosition, int direction, int speed) {
        Position newPos = oldPosition.clone();
        return getPositionByDirection2(newPos.x, newPos.y, direction, speed);
    }

    public static Position getPositionByDirection2(int x, int y, int direction, int speed) {
        Position newPos = new Position(x, y);
        switch (direction) {
            case 1:
                newPos.y -= speed;
                break;
            case 3:
                newPos.y += speed;
                break;
            case 2:
                newPos.x -= speed;
                break;
            case 0:
                newPos.x += speed;
                break;
        }
        return checkTeleportPosition(newPos);
    }

    public static Position checkTeleportPosition(Position oldPosition) {
        return new Position((oldPosition.x + 60) % 60, (oldPosition.y + 60) % 60);
    }

    public Position clone() {
        return new Position(x, y);
    }
}
