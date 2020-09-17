package hu.johetajava;

public class Car extends Entity {

    protected int hp = 100;
    protected int maxSpeed = 3;
    protected int transportedPassengerCount;
    protected Passenger passenger;

    Car(int id, Position position, int direction, int speed, Commands nextCommand, int hp, int transportedPassengerCount) {
        super(id, position, direction, speed, nextCommand); // Call the parent constructor
        this.hp = hp;
        setMaxSpeed();
        this.transportedPassengerCount = transportedPassengerCount;
    }

    Car(int id, Entity entity, Commands nextCommand, int hp, int transportedPassengerCount) {
        super(id, entity.position, entity.direction, entity.speed, nextCommand);
        this.hp = hp;
        setMaxSpeed();
        this.transportedPassengerCount = transportedPassengerCount;
    }

    @Override
    public String toString() {
        return "Car{" +
                "\n   hp=" + hp + "," +
                "\n   maxSpeed=" + maxSpeed +
                ",\n   transportedPassengerCount=" + transportedPassengerCount +
                ",\n   id=" + id +
                ",\n   position=" + position +
                ",\n   direction=" + direction +
                ",\n   speed=" + speed +
                ",\n   nextCommand=" + nextCommand +
                "\n}";
    }

    boolean hasPassenger() {
        return passenger != null;
    }

    void setPassenger() {
        for (Passenger passenger_ : World.passengers) {
            if (passenger_.carId == this.id) {
                this.passenger = passenger_;
                return;
            }
        }
        passenger = null;
    }

    void setMaxSpeed() {
        if (hp >= 60) {
            maxSpeed = 3;
        } else if (hp >= 25) {
            this.maxSpeed = 2;
        } else {
            maxSpeed = 1;
        }
    }
}
