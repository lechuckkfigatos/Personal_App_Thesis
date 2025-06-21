package org.lechuck.personal_app.DTO.WeatherDTO.Forecast;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ForecastDayDTO {
    private String date;
    private long date_epoch;
    private DayDTO day;
    private AstroDTO astro;
    private List<HourDTO> hour;
}
