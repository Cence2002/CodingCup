package hu.johetajava;

public class Passenger extends Entity {
    private static final int MAX_SPEED = 1;
    protected Position destination;
    /**
     * The passenger's car id
     */
    protected int carId;


    public Passenger(int id, Position position, int carId, Position destination) {
        super(id, position, -1, 0, Commands.NO_OP);
        this.destination = destination;
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "destination=" + destination +
                ", carId=" + carId +
                ", id=" + id +
                ", position=" + position +
                '}';
    }

    public boolean isCarried() {
        for (Car car : World.cars) {
            if (car.id == carId) {
                return true;
            }
        }
        return carId != World.carId && !World.isThere(Field.SIDEWALK, position);
    }
}
