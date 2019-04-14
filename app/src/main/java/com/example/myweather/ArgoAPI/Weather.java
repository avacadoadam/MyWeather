package com.example.myweather.ArgoAPI;


import android.graphics.Color;

/**
 * Stores Weather Information
 */
public class Weather {

    private String city;
    private double temperature; //main.temp
    private double humidity; //weather.main.humidity
    private double wind; //weather.wind.speed
    private double pressure; //weather.main.pressure
    private UnitsOfMeasurements unit;

    public Weather(String city, double temperature, double humidity, double wind, double pressure, UnitsOfMeasurements unit) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.pressure = pressure;
        this.unit = unit;

    }

    /**
     * Returns Color of wind in relation to its value on the Beaufort Scale
     * will convert to m/s always and determine whit that value
     * @return
     */
    public int getColorForWind() {
        double meterPerSecond;
        if(unit == UnitsOfMeasurements.Imperial){
            meterPerSecond = 2.2369/wind;
        }else meterPerSecond = wind;

        if (meterPerSecond < 1.5)
            return Color.parseColor("#488c56");
        else if (meterPerSecond < 3.3)
            return Color.parseColor("#442323");
        else if (meterPerSecond < 5.5)
            return Color.parseColor("#722d2d");
        else if (meterPerSecond < 8.0)
            return Color.parseColor("#842b2b");
        else if (meterPerSecond < 10.8)
            return Color.parseColor("#9b2828");
        else if (meterPerSecond < 13.9)
            return Color.parseColor("#9e1e1e");
        else if (meterPerSecond > 13.9)
            return Color.parseColor("#cc0e0e");
        return Color.BLACK;
    }

    /**
     * will convert to celsius
     * @return returns a color representing the value
     */
    public int getTempColor() {
        double celsius;
            if(unit == UnitsOfMeasurements.Imperial){
                celsius = (5.0/9.0)*(temperature - 32);
            }else celsius = temperature;
        if (celsius < 6)
            return Color.parseColor("#04705c");
        else if (celsius < 10)
            return Color.parseColor("#0e5f7f");
        else if (celsius < 18)
            return Color.parseColor("#818e30");
        else if (celsius < 25)
            return Color.parseColor("#8e7130");
        else if (celsius > 25)
            return Color.parseColor("#8c310b");

        return Color.BLACK;
    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return String.valueOf(temperature) + (UnitsOfMeasurements.Metric == unit ? " °C" :" °F");
    }

    public String getHumidity() {
        return String.valueOf(humidity) + " hpa";
    }

    public String getWind() {
        return String.valueOf(wind) + (UnitsOfMeasurements.Metric == unit ? " m/s" :" mph");
    }

    public String getPressure() {
        return String.valueOf(pressure) + " hpa";
    }

    public String getMeasurementType() {
        return unit.getUnitName();
    }
}
