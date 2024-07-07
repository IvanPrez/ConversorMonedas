import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Consulta consulta = new Consulta();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();

        HistorialConv history = new HistorialConv();

        String menu = """
                ** Escoge una opcion*
                
                1- Dólar a Peso Argentino
                2- Peso Argentino a Dólar
                3- Dólar a Real brasileño
                4- Real brasileño a  Dólar
                5- Dólar a Peso Colombiano
                6- Peso Colombiano a Dólar
                7- Dólar a Peso Mexicano
                8- Peso Mexicano a  Dólar
                9- Historial de conversiones
                0- Salir
                
                Elija una de las opciones 
                """;
        System.out.println("¡Bienvenido!;]");

        Scanner teclado = new Scanner(System.in);

        while (true) {
            System.out.println(menu);
            int opcion = 0;

            try {
                opcion = teclado.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Opción no válida. Por favor, ingrese un número.");
                teclado.next(); // Limpiar el buffer del escáner
                continue;
            }

            if (opcion == 0) {
                System.out.println("¡Gracias, Hasta pronto!");
                break;
            }

            if (opcion == 9) {
                history.printHistory();
                continue;
            }

            if (opcion < 0 || opcion > 9) {
                System.out.println("Opción no válida");
                continue;
            }

            double cantidad = 0;
            System.out.println("Cantidad a convertir: ");
            try {
                cantidad = teclado.nextDouble();
                if (cantidad > 9999999) {
                    System.out.println("Solo cifras de 7 digitos.");
                    continue;
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                teclado.next(); // Limpiar el buffer del escáner
                continue;
            }

            String base = "";
            String target = "";

            switch (opcion) {
                case 1:
                    base = "USD";
                    target = "ARS";
                    break;
                case 2:
                    base = "ARS";
                    target = "USD";
                    break;
                case 3:
                    base = "USD";
                    target = "BRL";
                    break;
                case 4:
                    base = "BRL";
                    target = "USD";
                    break;
                case 5:
                    base = "USD";
                    target = "COP";
                    break;
                case 6:
                    base = "COP";
                    target = "USD";
                    break;
                case 7:
                    base = "USD";
                    target = "MXN";
                    break;
                case 8:
                    base = "MXN";
                    target = "USD";
                    break;
            }

            String direccion = "https://v6.exchangerate-api.com/v6/e51d09a4daf36db05cb00191/pair/"
                    + base + "/" + target + "/" + cantidad;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(direccion))
                        .build();
                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();

                // Verifica si la respuesta es un objeto JSON
                if (json.trim().startsWith("{")) {
                    Respuesta respuesta = gson.fromJson(json, Respuesta.class);
                    double conversionResult = respuesta.getConversionResult();

                    // Redondear el resultado a dos decimales
                    BigDecimal roundedResult = new BigDecimal(conversionResult).setScale(2, RoundingMode.HALF_UP);
                    System.out.println( "$" + cantidad + " " + base + " equivalen a $" + roundedResult + " " + target);

                    // Agregar el registro al historial
                    history.addRecord(cantidad, base, target, roundedResult.doubleValue());

                    // Guardar historial en un archivo
                    history.saveHistoryToFile("historial.txt");
                } else {
                    System.out.println("Respuesta inesperada: ");
                }

            } catch (JsonSyntaxException e) {
                System.out.println("Error de sintaxis JSON: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Solo números");
            } catch (IOException | InterruptedException e) {
                System.out.println("Error en la conexión. Por favor, intente de nuevo más tarde.");
            }
        }
    }
}
