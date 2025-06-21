package org.lechuck.personal_app.DTO.WeatherDTO.Forecast;

import lombok.Getter;
import lombok.Setter;
import org.lechuck.personal_app.DTO.WeatherDTO.Condition;

@Setter
@Getter
public class DayDTO {
    private double maxtemp_c;
    private double maxtemp_f;
    private double mintemp_c;
    private double mintemp_f;
    private double avgtemp_c;
    private double avgtemp_f;
    private double maxwind_mph;
    private double maxwind_kph;
    private double totalprecip_mm;
    private double totalprecip_in;
    private double totalsnow_cm;
    private double avgvis_km;
    private int avghumidity;
    private int daily_will_it_rain;
    private int daily_chance_of_rain;
    private int daily_will_it_snow;
    private int daily_chance_of_snow;
    private Condition condition;
    private double uv;
//"maxtemp_c": 30.6,
//                    "maxtemp_f": 87.1,
//                    "mintemp_c": 24.1,
//                    "mintemp_f": 75.4,
//                    "avgtemp_c": 26.2,
//                    "avgtemp_f": 79.2,
//                    "maxwind_mph": 8.5,
//                    "maxwind_kph": 13.7,
//                    "totalprecip_mm": 12.14,
//                    "totalprecip_in": 0.48,
//                    "totalsnow_cm": 0.0,
//                    "avgvis_km": 9.9,
//                    "avgvis_miles": 6.0,
//                    "avghumidity": 83,
//                    "daily_will_it_rain": 1,
//                    "daily_chance_of_rain": 90,
//                    "daily_will_it_snow": 0,
//                    "daily_chance_of_snow": 0,
//                    "condition": {
//                        "text": "Moderate rain"
}
