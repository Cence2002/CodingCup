package hu.johetajava;

public class Pedestrian extends Entity {

    public Pedestrian(int id, Position position, int direction, int speed, Commands nextCommand) {
        super(id, position, direction, speed, nextCommand);
    }

    @Override
    public String toString() {
        return "Pedestrian{" +
                "id=" + id +
                ", position=" + position +
                ", direction=" + direction +
                ", speed=" + speed +
                ", nextCommand=" + nextCommand +
                '}';
    }
}
