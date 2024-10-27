package sk.umb.cloud.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GEO_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/v1/forecast";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Cacheable(value = "geocoding", key = "#city", unless = "#result == null")
    public GeocodeResponse getGeocode(String city) {
        logger.info("Fetching geocode for city: {}", city);
        UriComponentsBuilder geoUriBuilder = UriComponentsBuilder.fromHttpUrl(GEO_BASE_URL)
                .queryParam("name", city);

        GeocodeResponse response = restTemplate.getForObject(geoUriBuilder.toUriString(), GeocodeResponse.class);
        if (response != null && !response.getResults().isEmpty()) {
            logger.info("Geocode retrieved: {} (Lat: {}, Lon: {})", city, response.getResults().get(0).getLatitude(), response.getResults().get(0).getLongitude());
        }
        return response;
    }

    @Cacheable(value = "weather", key = "#city", unless = "#result == null")
    public String getWeather(String city) {
        // Step 1: Get coordinates for the city
        logger.info("Fetching weather data for city: {}", city);
        GeocodeResponse geocodeResponse = getGeocode(city);

        if (geocodeResponse == null || geocodeResponse.getResults().isEmpty()) {
            return "City not found";
        }

        double lat = geocodeResponse.getResults().get(0).getLatitude();
        double lon = geocodeResponse.getResults().get(0).getLongitude();

        // Step 2: Fetch weather data for the coordinates
        UriComponentsBuilder weatherUriBuilder = UriComponentsBuilder.fromHttpUrl(WEATHER_BASE_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("hourly", "temperature_2m");

        logger.info("Fetching weather data for coordinates: Lat: {}, Lon: {}", lat, lon);
        String weatherData = restTemplate.getForObject(weatherUriBuilder.toUriString(), String.class);

        return weatherData;
    }
}
