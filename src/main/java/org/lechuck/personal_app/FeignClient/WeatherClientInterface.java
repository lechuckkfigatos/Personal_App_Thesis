package org.lechuck.personal_app.FeignClient;

import org.lechuck.personal_app.DTO.WeatherDTO.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(
        name = "weatherapi",
        url = "${weatherapi.api.url}"
)
public interface WeatherClientInterface {
        @GetMapping("/current.json")
        WeatherResponse getWeather(
                @RequestParam("q") String location,
                @RequestParam("key") String apiKey
        );

        @GetMapping("/forecast.json")
        WeatherResponse getForecast(
                @RequestParam("q") String location,
                @RequestParam("key") String apiKey,
                @RequestParam("dt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                // Use LocalDate, ISO format yyyy-MM-dd is standard
                @RequestParam("hour") int hour
                );
}
