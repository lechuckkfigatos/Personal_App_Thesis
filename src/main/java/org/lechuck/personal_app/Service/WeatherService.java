package org.lechuck.personal_app.Service;

import org.lechuck.personal_app.DTO.WeatherDTO.WeatherResponse;
import org.lechuck.personal_app.FeignClient.WeatherClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
;import java.time.LocalDate;

@Service
//@Configuration
public class WeatherService {

    private final WeatherClientInterface weatherClientInterface;
//   @Bean
    @Value("${weatherapi.api.key}")
    private String apiKey;

    public WeatherService(WeatherClientInterface weatherClientInterface){
        this.weatherClientInterface = weatherClientInterface;
    }

    public WeatherResponse getWeatherByLocation(String location) {
            return weatherClientInterface.getWeather(location, apiKey);
        } 

    public WeatherResponse getForecast(String location, LocalDate date, int hour) {
        return weatherClientInterface.getForecast(location, apiKey, date, hour);
    }

}
