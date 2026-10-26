public class WeatherData {
    private String city;
    private double temperature;
    private int humidity;
    private String condition;
    private double windSpeed;

    public WeatherData(String city, double temperature,
                       int humidity, String condition,
                       double windSpeed) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.windSpeed = windSpeed;
    }

    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getCondition() {
        return condition;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
}