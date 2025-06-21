package org.lechuck.personal_app.DTO.WeatherDTO.Forecast;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forecast {

    @JsonProperty("forecastday")
    private List<ForecastDayDTO> forecastDay;

    public List<ForecastDayDTO> getForecastDay() {
        return forecastDay;
    }

    public void setForecastDay(List<ForecastDayDTO> forecastDay) {
        this.forecastDay = forecastDay;
    }
}
