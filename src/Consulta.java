
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Consulta {


    public Main buscarMoneda() {

        String direccion = "https://openexchangerates.org/api/latest.json?app_id=ab88e446171a4e30b6180209f79b50c9" ;
        String convert = "https://openexchangerates.org/api/convert/null/Required/Required?app_id=Required&prettyprint=false";


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                //.uri(URI.create(direccion))
                .uri(URI.create(direccion))
                .GET()
                .header("accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), Main.class);
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }


}
