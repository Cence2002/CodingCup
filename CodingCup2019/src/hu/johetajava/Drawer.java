package hu.johetajava;

import processing.core.PApplet;
import processing.core.PFont;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import static hu.johetajava.Main.client;
import static hu.johetajava.Main.world;

public class Drawer extends PApplet {

    static int startTime = 0;


    public void settings() {
        Main.drawer = this;
        size(1279, 899);
    }

    public void setup() {
        if (surface != null) {
            surface.setTitle("ML 7ek - Ultimate Hack-driving cars | Coding Cup Tomorrow 2019");
        }
        if (Main.framerate < 1.0f) {
            frameRate(Main.framerate);
        }
        System.err.println("frameRate be van állítva!!");
    }

    public void draw() {
        Main.onTick();
    }

    public void keyPressed() {
        if (Main.debug || true) {
            int x;
            int y;
            switch (keyCode) {
                case LEFT:
                    client.sendCommand(Commands.CAR_INDEX_LEFT);
                    break;
                case RIGHT:
                    client.sendCommand(Commands.CAR_INDEX_RIGHT);
                    break;
                case UP:
                    client.sendCommand(Commands.ACCELERATION);
                    break;
                case DOWN:
                    client.sendCommand(Commands.DECELERATION);
                    break;
                case CONTROL:
                    client.sendCommand(Commands.NO_OP);
                    break;
                case ALT:
                    client.sendCommand(Commands.EMERGENCY_BRAKE);
                    break;
                case SHIFT:
                    client.sendCommand(Main.getNextCommand());
                    break;

                case 32:
                    Main.debug = !Main.debug;
                    break;

                case 81:
                    x = mouseX / 15;
                    y = mouseY / 15;
                    if (x >= 0 && x < 60 && y >= 0 && y < 60) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                World.permanentObstacles[x][y][i][j] = true;
                            }
                        }
                    }
                    break;
                case 87:
                    x = mouseX / 15;
                    y = mouseY / 15;
                    if (x >= 0 && x < 60 && y >= 0 && y < 60) {
                        World.setObstacleByPosition(x, y);
                    }
                    break;
                case 69:
                    x = mouseX / 15;
                    y = mouseY / 15;
                    if (x >= 0 && x < 60 && y >= 0 && y < 60) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                World.permanentObstacles[x][y][i][j] = false;
                            }
                        }
                    }
                    break;
                /*case 189:
                    Main.saveFrame = !Main.saveFrame;
                    println(Main.saveFrame);
                    break;*/

            }
            drawWorld();
            if (Main.saveFrame) {
                saveFrame("games/" + World.gameId + "/" + World.tick + ".jpg");
            }
            /*DELAY*/
        }
    }


    void drawWorld() {
        if (Main.printWorld) {
            log(getMessage());
        }
        //System.out.println(getMessage());
        if (Main.drawWorld) {
            switch (Main.colorMode) {
                case 1:
                    drawMap(color(0, 20, 100), color(0, 0, 0), color(20, 20, 160), color(20, 60, 255), color(20, 40, 180), color(0, 30, 130), color(50, 70, 255));

                    drawPhantomCars(color(0, 255, 0, 70), color(50, 200, 255, 80), 4);

                    drawOtherCars(color(200, 150, 50), color(50, 200, 255), 6);

                    drawNextOtherCars(color(200, 150, 50), color(50, 200, 255), 3);

                    drawCar(color(0, 255, 20), color(80, 150, 255), 6);

                    drawNextCar(color(0, 255, 20), color(0, 150, 255), 3);

                    drawPedestrians(color(0, 255, 0));

                    drawPassenger(color(0, 255, 0, 200), color(0, 255, 0, 200), color(60, 80, 255));

                    drawMessage(color(40, 60, 255), color(0));

                    youDied(color(40, 80, 255));

                    break;
                case 2:
                    drawMap(color(100, 0, 90), color(20, 60, 125), color(110, 30, 145), color(10, 190, 200), color(200, 0, 180), color(10, 25, 50), color(10, 190, 200));

                    drawPhantomCars(color(234, 0, 220, 30), color(10, 190, 200, 100), 4);

                    drawOtherCars(color(0, 255, 0, 150), color(100, 255, 255), 6);

                    drawCar(color(200, 0, 180), color(10, 190, 200), 6);

                    drawNextCar(color(200, 0, 180), color(10, 190, 200), 6);

                    drawPedestrians(color(200, 0, 180));

                    drawPassenger(color(0), color(0), color(10, 190, 200));

                    drawMessage(color(10, 209, 218), color(0));

                    youDied(color(200, 0, 180));

                    break;
                case 3:
                    drawMap(color(80, 80, 110), color(150, 140, 140), color(230, 50, 170), color(150, 50, 175), color(230, 50, 170), color(130, 50, 165), color(250, 50, 180));

                    drawPhantomCars(color(235, 0, 220, 30), color(10, 190, 200, 100), 4);

                    drawOtherCars(color(0, 255, 0, 150), color(100, 255, 255), 6);

                    drawCar(color(200, 0, 180), color(10, 190, 200), 6);

                    drawNextCar(color(200, 0, 180), color(10, 190, 200), 6);

                    drawPedestrians(color(230, 50, 170));

                    drawPassenger(color(230, 50, 170), color(230, 50, 170), color(150, 140, 140));

                    drawMessage(color(230, 50, 170), color(100, 100, 100));

                    youDied(color(200, 0, 180));

                    break;
                default:
                    drawMap(color(20, 100, 20), color(0), color(20, 130, 40), color(40, 240, 50), color(40, 180, 50), color(0, 100, 0), color(0, 250, 0));

                    drawPhantomCars(color(0, 255, 0, 20), color(50, 255, 255, 40), 4);

                    drawOtherCars(color(100, 255, 255), color(0, 255, 0, 150), 6);

                    drawCar(color(0, 255, 0, 150), color(100, 255, 255), 6);

                    drawNextCar(color(0, 255, 0, 150), color(100, 255, 255), 6);

                    drawPedestrians(color(0));

                    drawPassenger(color(0), color(0), color(50, 255, 50));

                    drawMessage(color(32, 194, 14), color(0));

                    youDied(color(0, 255, 0));
            }

            for (Car otherCar : World.cars) {
                for (int k = 0; k <= Entity.getNextSpeed(otherCar.nextCommand, otherCar.speed); k++) {
                    drawPoint(Entity.getNextPosition(Commands.NO_OP, Entity.getNextPosition(otherCar.nextCommand, otherCar.position, otherCar.direction, otherCar.speed), Entity.getNextDirection(otherCar.nextCommand, otherCar.direction), k));
                }
            }
        }
    }

    void drawMap(int background, int road, int zebra, int sidewalk, int grass, int buildingAndTreeFill, int buildingAndTreeStroke) {
        noStroke();
        if (!Main.drawOnlyOnce || World.tick == 1) {
            background(background);
            for (int i = 0; i < 60; i++) {
                for (int j = 0; j < 60; j++) {
                    switch (World.map[j][i]) {
                        case ROAD:
                            fill(road);
                            rect(15 * i, 15 * j, 14, 14);

                            break;
                        case CROSSING:
                            fill(road);
                            rect(15 * i, 15 * j, 14, 14);
                            stroke(buildingAndTreeStroke);
                            strokeWeight(1);
                            line(15 * i + 2, 15 * j + 2, 15 * i + 12, 15 * j + 12);
                            line(15 * i + 2, 15 * j + 12, 15 * i + 12, 15 * j + 2);
                            noStroke();

                            break;
                        case ZEBRA:
                            fill(zebra);
                            rect(15 * i, 15 * j, 14, 14);

                            break;
                        case SIDEWALK:
                            fill(sidewalk);
                            rect(15 * i, 15 * j, 14, 14);

                            break;
                        case GRASS:
                            fill(grass);
                            rect(15 * i, 15 * j, 14, 14);

                            break;
                        case RAIL:
                        case BUILDING:
                        case TREE:
                            fill(buildingAndTreeFill);
                            rect(15 * i, 15 * j, 14, 14);
                            stroke(buildingAndTreeStroke);
                            strokeWeight(1.5f);
                            line(15 * i + 2, 15 * j + 2, 15 * i + 12, 15 * j + 12);
                            line(15 * i + 2, 15 * j + 12, 15 * i + 12, 15 * j + 2);
                            noStroke();

                            break;
                        default:
                            break;
                    }
                    /*if (World.tick > 2 && Main.obstacleSpeed < 4 && World.isThereObstacle(new Position(i, j), Main.obstacleDirection, Main.obstacleSpeed)) {
                        drawPoint(new Position(i, j));
                    }*/

                    if (World.tick > 2 && World.isThereObstacle(new Position(i, j), World.car.direction, World.car.speed)) {
                        //drawPoint(new Position(i, j));
                    }
                    if (World.tick > 2 && World.isThereAnyObstacle(new Entity(new Position(i, j), world.nextCar.direction, world.nextCar.speed))) {
                        //fill(200, 20, 20);
                        //circle((float) (15 * i + 7.5), (float) (15 * j + 7.5), 5);
                        drawPoint(new Position(i, j));
                    }
                    /*if (World.isInCircle2(i, j, 15, 14, 4) || World.isInCircle2(i, j, 29, 28, 4) || World.isInCircle2(i, j, 49, 28, 4)) {
                        drawPoint(new Position(i, j));
                    }*/
                }
            }
            drawTrains();
            if (World.tick > 2) {
                Entity target2 = Main.getTarget();
                drawACar(target2.position, target2.direction, color(0, 255, 20, 200), color(100, 150, 255, 200), 6);
            }
        }
    }

    void drawTrainPart(int x, int y) {
        fill(255, 50, 50, 150);
        rect((float) (15 * x + 1), (float) (15 * y + 1), 12, 12);
    }

    void drawTrains() {
        ArrayList<Position> trainPieces = Train.getTrainsPieces();
        for (Position trainPiece : trainPieces) {
            drawTrainPart(trainPiece.x, trainPiece.y);
        }
    }

    void drawACar(Position pos, int dir, int fill, int head, float carWidth) {
        if (Main.drawOnlyOnce) {
            fill = color(red(fill), green(fill), blue(fill), alpha(fill) / 3);
            head = color(red(head), green(head), blue(head), alpha(head) / 3);
        }
        rectMode(CENTER);
        noStroke();
        translate((float) (15 * pos.x + 7.5), (float) (15 * pos.y + 7.5));
        rotate(-dir * HALF_PI);
        fill(fill);
        rect(1, 0, 10, carWidth);
        fill(head);
        rect(5, 0, 4, carWidth);
        rotate(dir * HALF_PI);
        translate(-(float) (15 * pos.x + 7.5), -(float) (15 * pos.y + 7.5));
        rectMode(CORNER);
    }

    void drawCar(int fill, int head, float carWidth) {
        drawACar(World.car.position, World.car.direction, fill, head, carWidth);
    }

    void drawNextCar(int fill, int head, float carWidth) {
        if (!Main.drawOnlyOnce) {
            drawACar(World.nextCar.position, World.nextCar.direction, fill, head, carWidth);
        }
    }

    void drawPhantomCars(int fill, int head, float carWidth) {
        if (!Main.drawOnlyOnce && false) {
            ArrayList<Entity> phantomCars = World.getGivenDistanceEntities(World.nextCar, 11);
            for (Entity phantomCar : phantomCars) {
                drawACar(phantomCar.position, phantomCar.direction, fill, head, carWidth);
            }
            println(phantomCars.size());
        }
    }

    void drawOtherCars(int fill, int head, float carWidth) {
        for (Car otherCar : World.cars) {
            if (otherCar.id != World.car.id) {
                drawACar(otherCar.position, otherCar.direction, fill, head, carWidth);
            }
        }
    }

    void drawNextOtherCars(int fill, int head, float carWidth) {
        for (Car otherCar : World.cars) {
            if (otherCar.id != World.car.id) {
                Entity nextCar = Entity.getNextEntity(otherCar, otherCar.nextCommand);
                drawACar(nextCar.position, nextCar.direction, fill, head, carWidth);
            }
        }
    }

    void drawPedestrians(int color) {
        if (Main.drawOnlyOnce) {
            color = color(red(color), green(color), blue(color), alpha(color) / 3);
        }
        fill(color);
        for (Pedestrian pedestrian : World.pedestrians) {
            circle((float) (15 * pedestrian.position.x + 7), (float) (15 * pedestrian.position.y + 7), 5);
        }
    }

    void drawPassenger(int position, int outerDestination, int innerDestination) {
        if (Main.drawOnlyOnce) {
            position = color(red(position), green(position), blue(position), alpha(position) / 3);
            outerDestination = color(red(outerDestination), green(outerDestination), blue(outerDestination), alpha(outerDestination) / 3);
        }
        for (int i = 0; i < World.passengers.size(); i++) {
            fill(position);
            circle((float) (15 * World.passengers.get(i).position.x + 7), (float) (15 * World.passengers.get(i).position.y + 7), 8);
            fill(outerDestination);
            circle((float) (15 * World.passengers.get(i).destination.x + 7), (float) (15 * World.passengers.get(i).destination.y + 7), 12);
            fill(innerDestination);
            circle((float) (15 * World.passengers.get(i).destination.x + 7), (float) (15 * World.passengers.get(i).destination.y + 7), 6);
        }
    }

    void drawMessage(int color, int backgroundColor) {
        fill(backgroundColor);
        rect(899, 0, 384, 899);
        // Draw text
        fill(color);
        textSize(16);
        int padding = 15;
        PFont hackerFont = createFont("Hack-Regular.ttf", 14);
        textFont(hackerFont);


        // Szöveg kijelzése
        text(getMessage(), 899 + padding, padding, 384 - padding * 2, 899 - padding * 2);  // Text wraps within text box

        // Mesage-ek kiírása
        if (World.messages.length > 0) {
            log("== MESSAGES (t=" + World.tick + ") ==");
            for (String message_ : World.messages) {
                error(message_);
            }
        }
    }

    String getMessage() {
        String message = "";
        message += "Tick = " + World.tick;
        message += "\nTime = " + (millis() - startTime) / 60000 + ":" + complete((millis() - startTime) / 1000 % 60);
        message += "\nframeRate = " + frameRate;
        message += "\n\nCAR:\n  hp = " + World.car.hp;
        message += "\n  maxSpeed = " + World.car.maxSpeed;
        message += "\n  passengerCount = " + World.car.transportedPassengerCount;
        message += "\n  position = (" + World.car.position.x + ", " + World.car.position.y + ")";
        message += "\n  direction = " + Direction.getDirectionName(World.car.direction);
        message += "\n  speed = " + World.car.speed;
        message += "\ncommand = " + World.car.nextCommand;

        message += "\n\ncars = " + World.cars.size();

        message += "\n\nmousePosition = (" + (mouseX / 15) + ", " + (mouseY / 15) + ")\n";

        message += "\n\ncommands = " + Main.commands.size();

        //message += "\n\nPassenger: " + (World.car.hasPassenger() ? World.car.passenger.toString() : "-");

        message += "\nhasPassenger = " + World.car.hasPassenger();
        /*message += "\nhasPassenger = " + target;        for (int i = 0; i < World.passengers.size(); i++) {
            message += "\n" + World.passengers.get(i).id;
        }*/

        if (Main.drawRandomChars) {
            message += "\n\nRandom Unicode karakterek:\n";
            byte[] array = new byte[(new Random()).nextInt() % 300 + 300];

            (new Random()).nextBytes(array);
            message += new String(array, Charset.forName("UTF-8"));
        }
    /*message += "\nCAR: " + World.car.toString();
    message += "\n\nPassengers:";
    ArrayList<Passenger> passengers = World.passengers;
    for (int i = 0, passengersSize = passengers.size(); i < passengersSize; i++) {
        Passenger passenger = passengers.get(i);
        message += "\n#" + i + "    pos: (" + passenger.position.x + ", " + passenger.position.y + ")";
        message += "\n      dest: (" + passenger.destination.x + ", " + passenger.destination.y + ")";
    }
    message += "\n\n(" + target.x + ", " + target.y + ")";*/

        return message;
    }

    String complete(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }

    void drawPoint(Position position) {
        drawPoint(position, color(200, 20, 20, 80));
    }

    void drawPoint(Position position, int color) {
        fill(color);
        circle((float) (15 * position.x + 7.5), (float) (15 * position.y + 7.5), 5);
    }

    void youDied(int color) {
        if (World.isDead) {
            background(0);
            textSize(200);
            fill(color);
            textAlign(CENTER, CENTER);
            text("YOU DIED!", 0, 0, 1280, 900);
            if (!Main.infinite_running) noLoop();
        }
    }


    static void error(String message) {
        System.err.println("ERROR: " + message + " [" + Main.drawer.millis() + "]");
    }

    static void log(String message) {
        System.out.println(message);
    }

    /*public int millis(){
        return (int)System.nanoTime() / 1000000;
    }*/
}