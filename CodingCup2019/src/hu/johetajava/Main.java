package hu.johetajava;

import processing.core.PApplet;

import java.util.ArrayList;

public class Main {

    static final int colorMode = 1;
    public static Drawer drawer;
    public static Client client;
    public static boolean drawWorld = true;
    public static boolean printWorld = false;

    static boolean debug = false;
    static boolean saveFrame = false;

    public static boolean isFinale______________________ = true;

    /**
     * TODO EZEKET BEIKTATNI RENDESEN
     */
    private static boolean autoRun = true;
    private static int permanentObstacleMode = 6;
    private static boolean nextNextOtherCar = false;
    private static boolean fastTurn = false;
    static boolean declarationIfTheRoadIsClosed;

    static boolean drawOnlyOnce = false;
    static boolean infinite_running = true;
    public static boolean drawRandomChars = false;

    static int obstacleDirection = 0;
    static int obstacleSpeed = 4;

    public static final int infinity = 10000000;
    public static float framerate = 30;

    public static PApplet processing;
    public static World world;

    static ArrayList<Commands> commands = new ArrayList<>();
    static ArrayList<Commands> previousCommands = new ArrayList<>();
    static ArrayList<Integer> previousDistances = new ArrayList<>();
    private static boolean useProcessing = true;

    public static void main(String[] args) {
        world = new World();
        client = new Client();

        // Start processing
        if (useProcessing) {
            PApplet.main("hu.johetajava.Drawer", args);
        } else {
            drawer = new Drawer();
            while (true) {
                onTick();
            }
        }
    }

    public static void onTick() {
        //Drawer.log("=================");
        //Drawer.log("TICK: " + World.tick);

        if (!debug) {
            client.sendCommand(getNextCommand());
            if (useProcessing) {
                drawer.drawWorld();
                if (saveFrame) {
                    drawer.saveFrame("games/" + World.gameId + "/" + World.tick + ".jpg");
                }
            }
        }
    }

    static Commands getNextCommand() {
        //Drawer.log("getNextCommand");
        int start = drawer.millis();
        if (World.car.nextCommand != Commands.X && World.tick > 0) {
            if (commands.size() > 0) {
                Commands command = commands.get(0);
                commands.remove(0);
                System.out.println(drawer.millis() - start);
                return command;
            }

            /*if (World.tick > 6 && autoRun) {
                boolean stopped = true;
                for (int i = 0; i < 5 && i < previousCommands.size(); i++) {
                    if (!(previousCommands.get(i) == Commands.DECELERATION || previousCommands.get(i) == Commands.NO_OP) || World.nextCar.speed > 0) {
                        stopped = false;
                    }
                }
                if (stopped) {
                    return Commands.CAR_INDEX_RIGHT;
                }
            }*/
/*            boolean stopped=false;
            for(Car otherCar:World.cars)*/


            World.resetChangingObstacles();
            //autó
            for (Car otherCar : World.cars) {
                if (otherCar.hp == 0) {
                    World.setObstacleByPosition(otherCar.position, true);
                }
                for (int i = 0; i <= otherCar.speed; i++) {
                    World.setObstacleByPosition(Entity.getNextEntityBySpeed(Entity.getNextEntity(otherCar, otherCar.nextCommand), Commands.NO_OP, i).position, false);
                }
            }

            //vonat
            for (Position position : Train.getTrainsWithOffset()) {
                World.setObstacleByPosition(position, false);
                //Drawer.log("offset position:" + position);
            }

            if ((!World.isRoad(Position.getPositionByDirection(Position.getPositionByDirection(World.nextCar.position, World.nextCar.direction, 2), Direction.turn(World.nextCar.direction, -1), 1)) &&
                    !World.isRoad(Position.getPositionByDirection(Position.getPositionByDirection(World.nextCar.position, World.nextCar.direction, 2), Direction.turn(World.nextCar.direction, 1), 1))) &&
                    (World.isRoad(Position.getPositionByDirection(Position.getPositionByDirection(World.nextCar.position, World.nextCar.direction, 1), Direction.turn(World.nextCar.direction, -1), 1)) &&
                            World.isRoad(Position.getPositionByDirection(Position.getPositionByDirection(World.nextCar.position, World.nextCar.direction, 1), Direction.turn(World.nextCar.direction, 1), 1))) &&
                    World.nextCar.speed > 0) {
                System.out.println(drawer.millis() - start);
                return Commands.DECELERATION;
            }

            //gyalogos
            for (Pedestrian pedestrian : World.pedestrians) {
                Position nextPosition = Entity.getNextPosition(pedestrian.nextCommand, pedestrian.position, pedestrian.direction, pedestrian.speed);
                for (int direction = 0; direction < 4; direction++) {
                    Position nextNextPosition = Entity.getNextPosition(Commands.NO_OP, nextPosition, direction, 1);
                    if (!nextNextPosition.equals(pedestrian.position) && World.isThere(Field.SIDEWALK, nextNextPosition)) {
                        World.setObstacleByPosition(nextNextPosition, false);
                    }
                }
            }


            Commands bestCommand = Commands.DECELERATION;
            boolean hasChanged = false;
            int minimalDistance = infinity;
            int distance;
            Entity target2 = getTarget();
            previousDistances.add(0, infinity);
            for (Commands command : Command.GOOD_COMMANDS) {
                if ((command != Commands.CAR_INDEX_LEFT && command != Commands.CAR_INDEX_RIGHT) || World.nextCar.speed <= 1) {
                    //Drawer.log("beléptünk " + command);
                    Entity nextNextCar = Entity.getNextEntity(World.nextCar, command);
                    distance = World.getDistance(nextNextCar, target2);
                    if (!Entity.willCollideAnyObstacles(World.nextCar, command) || distance == 0) {
                        for (Commands innerCommand : Command.GOOD_COMMANDS) {
                            if ((innerCommand != Commands.CAR_INDEX_LEFT && innerCommand != Commands.CAR_INDEX_RIGHT) || nextNextCar.speed <= 1) {
                                if (!Entity.willCollideAnyObstacles(nextNextCar, innerCommand) || distance == 0) {
                                    //Drawer.log("Nem ütközünk a " + command + " commanddal. Errefele a distance: " + distance);
                                    if (distance <= minimalDistance && distance != -1) {
                                        previousDistances.set(0, distance);
                                        minimalDistance = distance;
                                        bestCommand = command;
                                        hasChanged = true;
                                        //Drawer.log("Új legkisebb distance: " + minimalDistance);
                                    } else {
                                        //Drawer.log("Sajnos hosszabb lenne ez az út (" + distance + " !< " + minimalDistance + ")");
                                    }
                                    if (target2.speed == 1) {
                                        if (World.nextCar.position.equals(target2.position)) {
                                            //if(World.getDistance(World.nextCar, target2) == 1){
                                            commands = new ArrayList<>();
                                            commands.add(Commands.ACCELERATION);
                                            //commands.add(Commands.NO_OP);
                                            commands.add(Commands.DECELERATION);
                                            commands.add(Commands.CAR_INDEX_LEFT);
                                            commands.add(Commands.CAR_INDEX_LEFT);
                                            commands.add(Commands.ACCELERATION);
                                            //return Commands.DECELERATION;
                                            //Drawer.log("----------Return NO_OP");
                                            //return Commands.NO_OP;
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

            /*if (!hasChanged) {
                Drawer.log("!has changed " + World.tick);
            } else {
                Drawer.log("changed  " + World.tick);
            }*/
            /**
             if (World.tick > 5) {
             if (minimalDistance > previousDistances.get(0) + 1 && previousDistances.get(4) > 5 && declarationIfTheRoadIsClosed) {
             return Commands.DECELERATION;
             }
             }*/
            System.out.println(drawer.millis() - start);
            return bestCommand;
        }

        System.out.println(drawer.millis() - start);
        return Commands.NO_OP;
    }

    public static Entity getTarget() {
        Entity bestTarget = null;
        int minimalDistance = infinity;
        Position target2;
        boolean normal;
        for (Passenger passenger : World.passengers) {
            normal = false;
            for (int i = 0; i < 4; i++) {
                if (!normal) {
                    for (int direction = 0; direction < 4; direction++) {
                        if (!normal) {
                            if (World.car.hasPassenger()) {
                                target2 = Position.getPositionByDirection(World.car.passenger.destination, i, 1);
                            } else {
                                target2 = Position.getPositionByDirection(passenger.position, i, 1);
                            }
                            if (!World.isThereObstacle(target2, Direction.turn(i, direction), 0) && !World.isThere(Field.ZEBRA, target2)) {
                                normal = true;
                                int distance = World.getDistance(World.car, new Entity(target2, Direction.turn(i, direction), 0));
                                if (distance < minimalDistance && distance != -1) {
                                    minimalDistance = distance;
                                    bestTarget = new Entity(target2, Direction.turn(i, direction), 0);
                                    //return bestTarget;
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 4; i++) {
                if (!normal) {
                    for (int direction = 0; direction < 2; direction++) {
                        if (!normal) {
                            if (World.car.hasPassenger()) {
                                target2 = Position.getPositionByDirection(Position.getPositionByDirection(World.car.passenger.destination, i, 1), Direction.turn(i, 1), 1);
                            } else {
                                target2 = Position.getPositionByDirection(Position.getPositionByDirection(passenger.position, i, 1), Direction.turn(i, 1), 1);
                            }
                            if (!World.isThereObstacle(target2, Direction.turn(i, direction), 0) && !World.isThere(Field.ZEBRA, target2)) {
                                int distance = World.getDistance(World.car, new Entity(target2, Direction.turn(i, direction + 2), 1));
                                if (distance < minimalDistance && distance != -1) {
                                    minimalDistance = distance;
                                    bestTarget = new Entity(target2, Direction.turn(i, direction + 2), 1);
                                    //return bestTarget;
                                }
                            }
                        }
                    }
                }
            }

            if (bestTarget != null) {
                //println(minimalDistance);
                return bestTarget;
            }

        }
        return new Entity(new Position(3, 0), 0, 0);
    }
}