package sk.umb.cloud.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(value  = "/weather", produces = "application/json")
    public String getWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }
}
