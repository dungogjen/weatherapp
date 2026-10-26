import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class WeatherService {

    private final String apiKey = "9c9761f97fffdd377d4a84f36531168c";

    public WeatherData getWeather(String city) throws Exception {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Please enter a city name."
            );
        }

        String encodedCity = URLEncoder.encode(
            city.trim(), StandardCharsets.UTF_8
        );

        String urlString =
            "https://api.openweathermap.org/data/2.5/weather?q="
            + encodedCity
            + "&appid=" + apiKey
            + "&units=metric";

        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int status = connection.getResponseCode();

            InputStream stream = status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream();

            StringBuilder response = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject json = new JSONObject(response.toString());

            if (status != 200) {
                String message = json.optString(
                    "message", "Request failed."
                );
                throw new Exception(message);
            }

            String cityName = json.getString("name");
            double temperature = json.getJSONObject("main")
                                      .getDouble("temp");
            int humidity = json.getJSONObject("main")
                               .getInt("humidity");
            String condition = json.getJSONArray("weather")
                                   .getJSONObject(0)
                                   .getString("description");
            double windSpeed = json.getJSONObject("wind")
                                   .optDouble("speed", 0.0);

            return new WeatherData(
                cityName, temperature, humidity,
                condition, windSpeed
            );

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}