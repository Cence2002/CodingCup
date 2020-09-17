package hu.johetajava;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class World {

    public static int gameId;
    public static int tick = 0;
    public static int carId;
    public static int width;
    public static int height;

    public static boolean isDead = false;
    public static String[] messages = new String[0];

    public static Car car;
    public static Car nextCar;

    public static ArrayList<Car> cars;
    public static ArrayList<Passenger> passengers;
    public static ArrayList<Pedestrian> pedestrians;

    public static Field[][] map;

    public static boolean[][][][] permanentObstacles;
    public static boolean[][][][] changingObstacles = new boolean[60][60][4][4];

    World() {
        cars = new ArrayList<>();
        passengers = new ArrayList<>();
        pedestrians = new ArrayList<>();

        loadMap();
        setPermanentObstacles(6);

        //calculateDistances();
    }

    public static void loadMap() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("map.txt"));

            String line;

            ArrayList<Field[]> fieldLines = new ArrayList<>();

            for (int i = 0; (line = reader.readLine()) != null; i++) {
                char[] charA = line.toCharArray();
                Field[] fieldLine = new Field[charA.length];
                for (int j = 0; j < charA.length; j++) {
                    switch (charA[j]) {
                        case 'S':
                            fieldLine[j] = Field.ROAD;
                            break;
                        case 'Z':
                            fieldLine[j] = Field.ZEBRA;
                            break;
                        case 'P':
                            fieldLine[j] = Field.SIDEWALK;
                            break;
                        case 'G':
                            fieldLine[j] = Field.GRASS;
                            break;
                        case 'B':
                            fieldLine[j] = Field.BUILDING;
                            break;
                        case 'T':
                            fieldLine[j] = Field.TREE;
                            break;
                        case 'R':
                            fieldLine[j] = Field.RAIL;
                            break;
                        case 'C':
                            fieldLine[j] = Field.RAIL;
                            break;
                        case 'X':
                            fieldLine[j] = Field.CROSSING;
                            break;
                    }
                }
                fieldLines.add(fieldLine);
            }

            map = fieldLines.toArray(new Field[0][]);

            height = map.length;
            width = map[0].length;

            Drawer.log("MAP betöltve");
        } catch (FileNotFoundException e) {
            Drawer.error("Map file reading error.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Debug:
        for (Field[] line: map) {
            System.out.println();
            for(Field field : line){
                System.out.print(field.name() + "\t");
            }
        }
        */
    }

    public static void resetChangingObstacles() {
        changingObstacles = new boolean[60][60][4][4];
    }

    public static void setObstacleByPosition(int x, int y) {
        if (!(isRoad(new Position(x, y)))) {
            setObstacleByPosition(x, y, true);
        } else {
            for (int dir = 0; dir < 4; dir++) {
                for (int speed = 0; speed < 4; speed++) {
                    permanentObstacles[x][y][dir][speed] = (
                            //left side
                            !isRoad(Position.getPositionByDirection2(x, y, Direction.turn(dir, 1), 1)) &&
                                    isRoad(Position.getPositionByDirection2(x, y, Direction.turn(dir, -1), 1)) &&
                                    speed != 0 &&
                                    !isInAnyCircle(x, y)) ||
                            //right side (in roundabout)
                            (isRoad(Position.getPositionByDirection2(x, y, Direction.turn(dir, 1), 1)) &&
                                    !isRoad(Position.getPositionByDirection2(x, y, Direction.turn(dir, -1), 1)) &&
                                    speed != 0 &&
                                    isInAnyCircle2(x, y)) ||
                                            /*  (isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, -1), 1)) &&
                                                    isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, 1), 1)) &&
                                                    (l == 2 || l == 3)) ||*/
                            //too fast in crossing
                            (isRoad(Position.getPositionByDirection(Position.getPositionByDirection2(x, y, dir, 1), Direction.turn(dir, -1), 1)) &&
                                    isRoad(Position.getPositionByDirection(Position.getPositionByDirection2(x, y, dir, 1), Direction.turn(dir, 1), 1)) && speed > 1)
                            // vonat
                            || (isThereAtNeighbour(Field.CROSSING, new Position(x, y)) && speed > 1)
                            || (isThere(Field.CROSSING, new Position(x, y)) && speed > 1);

                }
            }
        }

        //road corrections
        permanentObstacles[35][16][1][1] = false;
        permanentObstacles[50][13][3][1] = false;
        permanentObstacles[51][10][2][1] = false;

        permanentObstacles[33][15][0][1] = false;
        permanentObstacles[33][15][1][1] = false;
        permanentObstacles[33][15][2][1] = false;

        permanentObstacles[35][15][3][0] = false;
        permanentObstacles[35][15][3][1] = false;
        permanentObstacles[35][15][3][2] = false;

        permanentObstacles[33][18][1][0] = false;
        permanentObstacles[33][18][1][1] = false;
        permanentObstacles[33][17][0][0] = false;
        permanentObstacles[33][17][0][1] = false;

        permanentObstacles[31][15][3][0] = false;
        permanentObstacles[31][15][3][1] = false;

        //sidewalk gap
        permanentObstacles[9][31][1][1] = false;
        permanentObstacles[9][31][3][1] = false;
        permanentObstacles[41][24][1][1] = false;
        permanentObstacles[41][24][3][1] = false;

        permanentObstacles[9][31][1][0] = false;
        permanentObstacles[9][31][3][0] = false;
        permanentObstacles[41][24][1][0] = false;
        permanentObstacles[41][24][3][0] = false;
    }

    public static void setPermanentObstacles(int mode) {
        permanentObstacles = new boolean[60][60][4][4];
        switch (mode) {
            case 6:
                for (int x = 0; x < 60; x++) {
                    for (int y = 0; y < 60; y++) {
                        setObstacleByPosition(x, y);
                    }
                }

                break;
            case 5:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        if (!(isThere(Field.ROAD, i, j) || isThere(Field.ZEBRA, i, j))) {
                            setObstacleByPosition(new Position(i, j), true);
                        } else {
                            for (int k = 0; k < 4; k++) {
                                for (int l = 0; l < 4; l++) {
                                    permanentObstacles[i][j][k][l] = (
                                            isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, -1), 1)) &&
                                                    !isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, 1), 1)) &&
                                                    l != 0 &&
                                                    !isInAnyCircle(i, j)) ||
                                            (!isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, -1), 1)) &&
                                                    isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, 1), 1)) &&
                                                    l != 0 &&
                                                    isInAnyCircle2(i, j)) ||

                                            /*(isRoad(Position.getPositionByDirection(Position.getPositionByDirection2(i, j, k, 1), Direction.turn(k, 1), 1)) &&
                                                    isRoad(Position.getPositionByDirection(Position.getPositionByDirection2(i, j, k, 1), Direction.turn(k, -1), 1)) &&
                                                    (l == 2 || l == 3)) ||*/
                                            (isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, 1), 1)) &&
                                                    isRoad(Position.getPositionByDirection2(i, j, Direction.turn(k, -1), 1)) &&
                                                    (l == 2 || l == 3));
                                }
                            }
                        }
                    }
                }

                permanentObstacles[33][17][0][1] = false;
                permanentObstacles[35][16][1][1] = false;
                permanentObstacles[50][13][3][1] = false;
                permanentObstacles[51][10][2][1] = false;

                break;
            case 4:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        for (int k = 0; k < 4; k++) {
                            for (int l = 0; l < 4; l++) {
                                permanentObstacles[i][j][k][l] = (!(isRoad(new Position(i, j)))) || (
                                        isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&           //left side on normal road
                                                !isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                l != 0 &&
                                                !isInAnyCircle(i, j)) ||
                                        (!isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&         //rigte side in circles
                                                isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                l != 0 &&
                                                isInAnyCircle2(i, j)) ||
                                        (isRoad(Entity.getNextPosition(Commands.NO_OP, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1), Direction.turn(k, 1), 1)) &&       //too fast in cross
                                                isRoad(Entity.getNextPosition(Commands.NO_OP, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1), Direction.turn(k, -1), 1)) &&
                                                (l == 2 || l == 3));
                                if (isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1)) &&                                 //1 thick sidewlak
                                        isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 2), 1)) &&
                                        isThere(Field.SIDEWALK, new Position(i, j)) &&
                                        l == 1) {
                                    permanentObstacles[i][j][k][l] = false;
                                }
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        if (!(isThere(Field.ROAD, i, j) || isThere(Field.ZEBRA, i, j))) {
                            setObstacleByPosition(new Position(i, j), true);
                        } else {
                            for (int k = 0; k < 4; k++) {
                                for (int l = 0; l < 4; l++) {
                                    permanentObstacles[i][j][k][l] = (
                                            isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&
                                                    !isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                    l != 0 &&
                                                    !isInAnyCircle(i, j)) ||
                                            (!isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&
                                                    isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                    l != 0 &&
                                                    isInAnyCircle2(i, j)) ||
                                            (isRoad(Entity.getNextPosition(Commands.NO_OP, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1), Direction.turn(k, 1), 1)) &&
                                                    isRoad(Entity.getNextPosition(Commands.NO_OP, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1), Direction.turn(k, -1), 1)) &&
                                                    (l == 2 || l == 3)) ||
                                            (isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                    isRoad(Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&
                                                    (l == 2 || l == 3));
                                }
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        if (!(isThere(Field.ROAD, i, j) || isThere(Field.ZEBRA, i, j))) {
                            setObstacleByPosition(new Position(i, j), true);
                        } else {
                            for (int k = 0; k < 4; k++) {
                                for (int l = 0; l < 4; l++) {
                                    permanentObstacles[i][j][k][l] = (((isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) ||
                                            isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1))) && (
                                            !isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                    !isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1))) &&
                                            (isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1)) ||
                                                    isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1))) &&
                                            l != 0) && !isInCircle(i, j, 15, 14, 4) && !isInCircle(i, j, 29, 28, 4) && !isInCircle(i, j, 49, 28, 4)) ||
                                            (((isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) ||
                                                    isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1))) && (
                                                    !isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&
                                                            !isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1))) &&
                                                    (isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1)) ||
                                                            isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1))) &&
                                                    l != 0) && (isInCircle2(i, j, 15, 14, 4) || isInCircle2(i, j, 29, 28, 4) || isInCircle2(i, j, 49, 28, 4)));
                                }
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        for (int k = 0; k < 4; k++) {
                            for (int l = 0; l < 4; l++) {
                                Position position = new Position(i, j);
                                permanentObstacles[i][j][k][l] = !(
                                        isThere(Field.ROAD, position) || isThere(Field.ZEBRA, position)) || //normal obstacle
                                        (((isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) ||
                                                isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1))) && (
                                                !isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) &&
                                                        !isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1))) &&
                                                (isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1)) ||
                                                        isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1))) &&
                                                l != 0) && !isInCircle(i, j, 15, 14, 4) && !isInCircle(i, j, 29, 28, 4) && !isInCircle(i, j, 49, 28, 4)) ||
                                        (((isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1)) ||
                                                isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, 1), 1))) && (
                                                !isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1)) &&
                                                        !isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), Direction.turn(k, -1), 1))) &&
                                                (isThere(Field.ZEBRA, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1)) ||
                                                        isThere(Field.ROAD, Entity.getNextPosition(Commands.NO_OP, new Position(i, j), k, 1))) &&
                                                l != 0) && (isInCircle(i, j, 15, 14, 4) || isInCircle(i, j, 29, 28, 4) || isInCircle(i, j, 49, 28, 4)));
                            }
                        }
                    }
                }
                break;
            default:
                for (int i = 0; i < 60; i++) {
                    for (int j = 0; j < 60; j++) {
                        for (int k = 0; k < 4; k++) {
                            for (int l = 0; l < 4; l++) {
                                Position position = new Position(i, j);
                                permanentObstacles[i][j][k][l] = !(isThere(Field.ROAD, position) || isThere(Field.ZEBRA, position));
                            }
                        }
                    }
                }
                break;
        }
    }

    public static void setObstacleByPosition(Position position, boolean permanent) {
        if (position.x >= 0 && position.x < 60 && position.y >= 0 && position.y < 60) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (permanent) {
                        permanentObstacles[position.x][position.y][i][j] = true;
                    } else {
                        changingObstacles[position.x][position.y][i][j] = true;
                    }
                }
            }
        }
    }

    public static void setObstacleByPosition(int x, int y, boolean permanent) {
        setObstacleByPosition(new Position(x, y), permanent);
    }

    public static boolean isThereObstacle(Position position, int direction, int speed) {
        return permanentObstacles[position.x][position.y][direction][speed];
    }

    public static boolean isThereObstacle(Entity entity) {
        return permanentObstacles[entity.position.x][entity.position.y][entity.direction][entity.speed];
    }

    public static boolean isThereAnyObstacle(Entity entity) {
        return permanentObstacles[entity.position.x][entity.position.y][entity.direction][entity.speed] || changingObstacles[entity.position.x][entity.position.y][entity.direction][entity.speed];
    }

    public static void setObstacle(Position position, int direction, int speed) {
        permanentObstacles[position.x][position.y][direction][speed] = true;
    }

    public static boolean isInCircle(int x, int y, int circleX, int circleY, int r) {
        return (x >= circleX && x < circleX + r && (y == circleY || y == circleY + r - 1)) || (y >= circleY && y < circleY + r && (x == circleX || x == circleX + r - 1));
    }

    public static boolean isInCircle2(int x, int y, int circleX, int circleY, int r) {
        return (x > circleX && x < circleX + r - 1 && (y == circleY || y == circleY + r - 1)) || (y > circleY && y < circleY + r - 1 && (x == circleX || x == circleX + r - 1));
    }

    public static boolean isInAnyCircle(int x, int y) {
        return isInCircle(x, y, 15, 14, 4) || isInCircle(x, y, 29, 28, 4) || isInCircle(x, y, 49, 28, 4);
    }

    public static boolean isInAnyCircle2(int x, int y) {
        return isInCircle2(x, y, 15, 14, 4) || isInCircle2(x, y, 29, 28, 4) || isInCircle2(x, y, 49, 28, 4);
    }

    public static ArrayList<Entity> getGivenDistanceEntities(Entity startEntity, int step) {
        Drawer.log("getGivenDistanceEntities");
        int[][][][] distanceMatrix = new int[60][60][4][4];
        distanceMatrix[startEntity.position.x][startEntity.position.y][startEntity.direction][startEntity.speed] = 1;
        ArrayList<Entity> knownDistanceEntities = new ArrayList<>();
        knownDistanceEntities.add(startEntity);
        if (step == 0) {
            return knownDistanceEntities;
        }
        int d = 2;
        boolean allChanged = false;
        while (!allChanged) {
            allChanged = true;
            ArrayList<Entity> newKnownDistanceEntities = new ArrayList<>();
            for (Entity entity : knownDistanceEntities) {
                Entity nextEntity;
                for (Commands command : Command.GOOD_COMMANDS) {
                    if ((command != Commands.CAR_INDEX_LEFT && command != Commands.CAR_INDEX_RIGHT)
                            || (entity.speed == 0 || entity.speed == 1)) {
                        nextEntity = Entity.getNextEntity(entity, command);
                        if (!Entity.willCollide(entity, command)) {
                            if (distanceMatrix[nextEntity.position.x][nextEntity.position.y][nextEntity.direction][nextEntity.speed] == 0) {
                                allChanged = false;
                                distanceMatrix[nextEntity.position.x][nextEntity.position.y][nextEntity.direction][nextEntity.speed] = d;
                                newKnownDistanceEntities.add(nextEntity);
                            }
                        }
                    }
                }
            }
            knownDistanceEntities.clear();
            knownDistanceEntities.addAll(newKnownDistanceEntities);
            if (d - 1 == step) {
                return newKnownDistanceEntities;
            }
            d++;
        }
        return new ArrayList<>();
    }

    public static int getDistance(Entity startEntity, Entity targetEntity) {
        //Drawer.log("getDistance");
        int[][][][] distanceMatrix = new int[60][60][4][4];
        distanceMatrix[startEntity.position.x][startEntity.position.y][startEntity.direction][startEntity.speed] = 1;
        ArrayList<Entity> knownDistanceEntities = new ArrayList<>();
        knownDistanceEntities.add(startEntity);
        int d = 1;
        if (startEntity.position.equals(targetEntity.position) && startEntity.direction == targetEntity.direction && startEntity.speed == targetEntity.speed) {
            return 0;
        }
        boolean allChanged = false;
        while (!allChanged) {
            allChanged = true;
            ArrayList<Entity> newKnownDistanceEntities = new ArrayList<>();
            for (Entity entity : knownDistanceEntities) {
                Entity nextEntity;
                for (Commands command : Command.GOOD_COMMANDS) {
                    if ((command != Commands.CAR_INDEX_LEFT && command != Commands.CAR_INDEX_RIGHT)
                            || (entity.speed == 0 || entity.speed == 1)) {
                        nextEntity = Entity.getNextEntity(entity, command);

                        if (!Entity.willCollide(entity, command)) {
                            if (distanceMatrix[nextEntity.position.x][nextEntity.position.y][nextEntity.direction][nextEntity.speed] == 0) {
                                allChanged = false;
                                distanceMatrix[nextEntity.position.x][nextEntity.position.y][nextEntity.direction][nextEntity.speed] = d;
                                newKnownDistanceEntities.add(nextEntity);
                                if (nextEntity.position.equals(targetEntity.position) && (nextEntity.direction == targetEntity.direction) && (nextEntity.speed == targetEntity.speed)) {
                                    return d;
                                }
                            }
                        }
                    }
                }
            }
            knownDistanceEntities.clear();
            knownDistanceEntities.addAll(newKnownDistanceEntities);
            d++;
        }
        //System.err.print("Unreachable endPosition");
        return -1;
    }

    public static boolean isThere(Field field, Position position) {
        return getField(position).equals(field);
    }

    public static boolean isRoad(Position position) {
        return isThere(Field.ROAD, position) || isThere(Field.ZEBRA, position) || isThere(Field.CROSSING, position);
    }

    public static boolean isThere(Field field, int x, int y) {
        return getField(new Position(x, y)).equals(field);

    }

    public static boolean isThereAtNeighbour(Field field, Position position) {
        for (int dir = 0; dir < 4; dir++) {
            if (isThere(field, Position.getPositionByDirection(position, dir, 1))) return true;
        }
        return false;
    }

    private static Field getField(Position position) {
        if (position.x >= 0 && position.y >= 0) {
            return map[position.y][position.x];
        }
        return Field.RAIL;
    }
}
