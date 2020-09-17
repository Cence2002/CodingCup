package hu.johetajava;

import processing.core.PApplet;

public class Entity {
    /**
     * ID
     * unique identifier
     * It's -1 if something has no id
     */
    protected int id;

    /**
     * The current position of the entity
     */
    protected Position position;

    /**
     * The absolute direction of the entity's movement
     */
    protected int direction;

    /**
     * The current speed of the entity
     */
    protected int speed;

    /**
     * The expected command of the entity
     */
    protected Commands nextCommand;

    Entity(int id, Position position, int direction, int speed, Commands nextCommand) {
        this.id = id;
        this.position = position;
        this.direction = direction;
        this.speed = speed;
        this.nextCommand = nextCommand;
    }

    Entity(Position position, int direction, int speed) {
        this.position = position;
        this.direction = direction;
        this.speed = speed;
    }

    public static Entity getNextEntity(Entity start, Commands command) {
        Position nextPos = getNextPosition(command, start.position, start.direction, start.speed);
        int nextDir = getNextDirection(command, start.direction);
        int nextSpeed = getNextSpeed(command, start.speed);
        return new Entity(nextPos, nextDir, nextSpeed);
    }

    public static Entity getNextEntityBySpeed(Entity start, Commands command, int speed) {
        Position nextPosition = getNextPosition(command, start.position, start.direction, speed);
        int nextDirection;
        if (speed == getNextSpeed(command, start.speed)) {
            nextDirection = getNextDirection(command, start.direction);
        } else {
            nextDirection = start.direction;
        }
        int nextSpeed = getNextSpeed(command, start.speed);
        return new Entity(nextPosition, nextDirection, nextSpeed);
    }

    public static boolean willCollideAnyObstacles(Entity entity, Commands command) {
        /*int speed = entity.speed;
        if(command == Commands.ACCELERATION){
            speed++;
            //Drawer.log("gyorított");
        }
        else if(command == Commands.DECELERATION){
            speed--;
        }
        for (int i = 0; i <= speed; i++) {
            if (World.isThereAnyObstacle(getNextEntityBySpeed(entity, Commands.NO_OP, i))) {
                //Drawer.log("Na most ütköznénk. entity: " + entity.toString() + " az i most " + i+".");
                return true;
            }
        }
        return false;*/
        for (int i = 0; i <= entity.speed; i++) {
            if (World.isThereAnyObstacle(getNextEntityBySpeed(entity, command, i))) {
                return true;
            }
        }
        return false;
    }


    public static boolean willCollide(Entity entity, Commands command) {
        for (int i = 0; i <= entity.speed; i++) {
            if (World.isThereObstacle(getNextEntityBySpeed(entity, command, i))) {
                return true;
            }
        }
        return false;
    }

    public static int getNextSpeed(Commands command, int speed) {
        int nextSpeed = speed;

        if (speed > 1 && (command == Commands.CAR_INDEX_LEFT || command == Commands.CAR_INDEX_RIGHT)) {
            nextSpeed--;
        }
        if (command == Commands.ACCELERATION) {
            if (nextSpeed < 3) {
                nextSpeed++;
            }
        }
        if (command == Commands.DECELERATION) {
            if (nextSpeed > 0) {
                nextSpeed--;
            }
        }
        return PApplet.min(nextSpeed, World.car.maxSpeed);
    }

    public static int getNextDirection(Commands command, int direction) {
        if (command == Commands.CAR_INDEX_LEFT) {
            return Direction.turn(direction, 1);
        } else if (command == Commands.CAR_INDEX_RIGHT) {
            return Direction.turn(direction, -1);
        } else {
            return direction;
        }
    }

    public static Position getNextPosition(Commands command, Position position, int direction, int speed) {
        int nextSpeed = getNextSpeed(command, speed);
        return Position.getPositionByDirection(position, direction, nextSpeed);
    }

    /**
     * Get the position that the entity would stand if it moves in  a direction
     *
     * @param position
     * @param direction
     * @param speed     The current speed of the
     * @return The position after the movement
     */

}
