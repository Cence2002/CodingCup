package hu.johetajava;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static hu.johetajava.Main.drawer;
import static hu.johetajava.Main.infinite_running;

public class Client {
    private static final int GAMESERVER_PORT = 12323;
    static String TOKEN = "ELvHmVVgGrUgn1uwRaM1mYCTJ5VBLwGwEyw0SHDAmiCokQTvOBulyewN7H8HGneo0aX59pP";
    private static String GAME_SERVER_URI = "10.100.1.150";
    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    Client() {
        // Init client
        startClient();
        sendToken();
    }

    static void startClient() {
        try {
            Socket socket = new Socket(GAME_SERVER_URI, GAMESERVER_PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("\nnincs szerver (vagy internet)\n\nTODO:\n csatlakozni a hálózatra\n várni\n (s)írni a szervezőknek\n 5l@Lni az algoritmuson\n PUSHOLNI, HA VÉGEZTÉL");
            System.exit(0);
            //e.printStackTrace();
        }
    }

    static void sendMessage(String message) {
        //System.err.println("Message: " + message);
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readMessage() {
        try {
            StringBuilder sb = new StringBuilder();
            int brackets = 0;

            do {
                int readedChar = inputStream.read();

                char c = (char) readedChar;
                switch (c) {
                    case '{':
                        ++brackets;
                        break;
                    case '}':
                        --brackets;
                        break;
                }
                sb.append(c);
            } while (brackets > 0);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
    }

    /**
     * * Sends the token to the server then gets the first tick information
     */
    static void sendToken() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", TOKEN);
        if (Main.isFinale______________________) {
            jsonObject.put("finale", "420");
        }
        sendMessage(jsonObject.toString(0));

        try {
            // GET JSON DATA
            JSONObject response = new JSONObject(readMessage());
            eatJsonObject(response);
            System.out.println("Token elküldve.");
        } catch (JSONException e) {
            System.err.println("\nnincs szerver (vagy internet)\n\nTODO:\n csatlakozni a hálózatra\n várni\n (s)írni a szervezőknek\n 5l@Lni az algoritmuson\n PUSHOLNI, HA VÉGEZTÉL");
        }
    }

    /**
     * Gets the tick information from a JSONObject
     *
     * @param jsonObject JSON response
     */
    static void eatJsonObject(JSONObject jsonObject) {

        // Get request data
        try {
            World.carId = jsonObject.getJSONObject("request_id").getInt("car_id");
            World.tick = jsonObject.getJSONObject("request_id").getInt("tick");
            World.gameId = jsonObject.getJSONObject("request_id").getInt("game_id");
        } catch (JSONException e) {
            System.err.println("\nnincs szerver (vagy internet)\n\nTODO:\n csatlakozni a hálózatra\n várni\n (s)írni a szervezőknek\n 5l@Lni az algoritmuson\n PUSHOLNI, HA VÉGEZTÉL");
            //error("Sikertelen kommunkáció a szerverrel! Vagy elment a wifi, vagy valaki más is futtat...");
            System.exit(0);
        }
        // Get CAR data
        JSONArray jsonCars = jsonObject.getJSONArray("cars");

        //println(jsonCars.toString(4));
        World.cars.clear();
        for (int i = 0; i < jsonCars.length(); i++) {
            JSONObject jsonCar = jsonCars.getJSONObject(i);

            Commands next_command;
            if (jsonCar.has("command")) {
                next_command = Command.getNextCommandBySign(jsonCar.getString("command").charAt(0));
            } else {
                next_command = Commands.NO_OP;
            }
            World.cars.add(new Car(
                    jsonCar.getInt("id"),
                    new Position(jsonCar.getJSONObject("pos").getInt("x"), jsonCar.getJSONObject("pos").getInt("y")),
                    Direction.getDirectionByName(jsonCar.getString("direction")),
                    jsonCar.getInt("speed"), next_command,
                    jsonCar.getInt("life"),
                    jsonCar.getInt("transported")
            ));
        }

        for (int i = 0; i < World.cars.size(); i++) {
            if (World.cars.get(i).id == World.carId) {
                World.car = World.cars.get(i);

                // Create nextCar
                World.nextCar = new Car(
                        World.car.id,
                        Entity.getNextEntity(World.car, World.car.nextCommand),
                        Commands.NO_OP,
                        World.car.hp,
                        World.car.transportedPassengerCount
                );
                World.cars.remove(i);
                break;
            }
        }

        World.pedestrians.clear();

        // Get pedestrians:
        JSONArray jsonPedestrians = jsonObject.getJSONArray("pedestrians");
        for (int i = 0; i < jsonPedestrians.length(); i++) {
            JSONObject jsonPedestrian = jsonPedestrians.getJSONObject(i);

            Commands next_command = jsonPedestrian.has("next_command") ? Command.getNextCommandBySign(jsonPedestrian.getString("next_command").charAt(0)) : Commands.NO_OP;

            World.pedestrians.add(new Pedestrian(
                    jsonPedestrian.getInt("id"),
                    new Position(jsonPedestrian.getJSONObject("pos").getInt("x"), jsonPedestrian.getJSONObject("pos").getInt("y")),
                    Direction.getDirectionBySign(jsonPedestrian.getString("direction").charAt(0)),
                    jsonPedestrian.getInt("speed"),
                    next_command
            ));
        }


        // Get passengers:
        JSONArray jsonPassengers = jsonObject.getJSONArray("passengers");
        World.passengers.clear();
        for (int i = 0; i < jsonPassengers.length(); i++) {
            JSONObject jsonPassenger = jsonPassengers.getJSONObject(i);

            World.passengers.add(new Passenger(
                    jsonPassenger.getInt("id"),
                    new Position(
                            jsonPassenger.getJSONObject("pos").getInt("x"),
                            jsonPassenger.getJSONObject("pos").getInt("y")
                    ),
                    jsonPassenger.has("car_id") ? jsonPassenger.getInt("car_id") : -1,
                    new Position(
                            jsonPassenger.getJSONObject("dest_pos").getInt("x"),
                            jsonPassenger.getJSONObject("dest_pos").getInt("y")
                    )
            ));
            if (World.passengers.get(World.passengers.size() - 1).isCarried()) {
                World.passengers.remove(World.passengers.size() - 1);
            }
        }

        // Get messages
        JSONArray jsonMessages = jsonObject.getJSONArray("messages");
        ArrayList<String> messages = new ArrayList<>();
        for (int i = 0; i < jsonMessages.length(); i++) {
            messages.add(jsonMessages.getString(i));
        }
        World.messages = messages.toArray(new String[0]);


        // Additional calculations in the beginning of a tick
        World.car.setPassenger();
    }

    /**
     * Sends the command then fetches the new tick information from the server
     *
     * @param command Next command
     */
    void sendCommand(Commands command) {
        //println(command);
        Main.previousCommands.add(0, command);
        JSONObject requestObject = new JSONObject();
        requestObject.put("token", TOKEN);

        JSONObject responseId = new JSONObject();
        responseId.put("game_id", World.gameId);
        responseId.put("tick", World.tick);
        responseId.put("car_id", World.carId);

        requestObject.put("response_id", responseId);
        requestObject.put("command", command);

        // SEND JSON DATA
        sendMessage(requestObject.toString(0));

        // GET JSON DATA
        try {
            JSONObject response = new JSONObject(readMessage());
            //System.out.println(response.toString(4));
            eatJsonObject(response);
        } catch (JSONException e) {
            World.isDead = true;

            if (infinite_running) {
                startClient();
                sendToken();
                System.out.println("Token elküldve.");
                World.isDead = false;
                Drawer.startTime = drawer.millis();
                Main.obstacleDirection = 0;
                Main.obstacleSpeed = 4;
                Main.previousCommands.clear();
                Main.previousDistances.clear();
                Main.declarationIfTheRoadIsClosed = false;
            }

        }
    }

}
