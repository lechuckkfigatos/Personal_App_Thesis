package org.lechuck.personal_app.DTO.WeatherDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.lechuck.personal_app.DTO.WeatherDTO.Forecast.Forecast;


@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private Location location;
    private Current current;
    private Forecast forecast;

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }
}