package org.lechuck.personal_app.DTO.WeatherDTO.Forecast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class HourDTO {
    private double wind_kph;
    private int wind_degree;
    private String wind_dir;
    private double pressure_mb;
    private double pressure_in;
    private double precip_mm;
    private double precip_in;
    private int humidity;
    private int cloud;
    private double feelslike_c;
    private double windchill_c;
    private double heatindex_c;
    private double dewpoint_c;
    private double vis_km;
    private double uv;
    private double gust_kph;
}
