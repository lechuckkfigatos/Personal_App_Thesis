package org.lechuck.personal_app.Controller;

import org.lechuck.personal_app.DTO.WeatherDTO.WeatherResponse;
import org.lechuck.personal_app.Service.WeatherService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(value = "/current", produces = "application/json")
    public ResponseEntity<?> getWeather(
            @RequestParam String location
    ){
        WeatherResponse currentWeather = weatherService.getWeatherByLocation(location);
        return ResponseEntity.ok(currentWeather);
    }

    @GetMapping(value = "/forecast", produces = "application/json")
    public ResponseEntity<?> getForecast(
            @RequestParam String location,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, // Expect date as yyyy-MM-dd
            @RequestParam("hour") int hour // Expect hour as an integer
    ) {
        WeatherResponse weatherForecast =  weatherService.getForecast(location, date, hour);
        if (weatherForecast == null) {
            return ResponseEntity.badRequest().body("Invalid date or hour");
        }
        return ResponseEntity.ok(weatherForecast);
    }
}