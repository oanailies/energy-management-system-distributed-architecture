package Receiver;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDatabaseConnection {
  /*  public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/consumer_db";
        String username = "postgres";
        String password = "oana2002";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Conexiune reușită la baza de date!");
        } catch (Exception e) {
            System.out.println("Eroare la conectarea la baza de date: " + e.getMessage());
        }
    }*/

   /* public static void main(String[] args) {
        String jsonMessage = "{\"measurement_value\":236.3126361,\"device_id\":1,\"timestamp\":1735910626004}";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DeviceMeasurementDTO dto = objectMapper.readValue(jsonMessage, DeviceMeasurementDTO.class);
            System.out.println(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
