package ch.epfl.sdp;

import org.junit.Test;

import java.util.Date;
import java.util.Map;

import ch.epfl.sdp.db.DatabaseObjectBuilder;
import ch.epfl.sdp.db.DatabaseObjectBuilderRegistry;
import ch.epfl.sdp.weather.Weather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class WeatherDatabaseBuilderTest {

    private String weatherData = "{\"lat\":50.04,\"lon\":10.48,\"timezone\":\"Europe/Berlin\",\"current\":{\"dt\":1589308917,\"sunrise\":1589254552,\"sunset\":1589309586,\"temp\":8.67,\"feels_like\":4.58,\"pressure\":1011,\"humidity\":6,\"dew_point\":-24.37,\"uvi\":6.28,\"clouds\":98,\"wind_speed\":0.45,\"wind_deg\":45,\"wind_gust\":0.89,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}]},\"daily\":[{\"dt\":1589281200,\"sunrise\":1589254552,\"sunset\":1589309586,\"temp\":{\"day\":8.67,\"min\":4.04,\"max\":8.67,\"night\":4.04,\"eve\":8.67,\"morn\":8.67},\"feels_like\":{\"day\":4.03,\"night\":0.95,\"eve\":4.03,\"morn\":4.03},\"pressure\":1011,\"humidity\":6,\"dew_point\":-24.37,\"wind_speed\":1.23,\"wind_deg\":238,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":98,\"uvi\":6.28},{\"dt\":1589367600,\"sunrise\":1589340865,\"sunset\":1589396074,\"temp\":{\"day\":15.55,\"min\":6.62,\"max\":16.22,\"night\":7.26,\"eve\":11.54,\"morn\":6.62},\"feels_like\":{\"day\":12.6,\"night\":4.62,\"eve\":9.62,\"morn\":4},\"pressure\":1011,\"humidity\":40,\"dew_point\":2.18,\"wind_speed\":1.83,\"wind_deg\":59,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":66,\"uvi\":6},{\"dt\":1589454000,\"sunrise\":1589427180,\"sunset\":1589482560,\"temp\":{\"day\":10.43,\"min\":6.72,\"max\":14.21,\"night\":7.97,\"eve\":11.5,\"morn\":6.72},\"feels_like\":{\"day\":7.41,\"night\":2.79,\"eve\":7.73,\"morn\":3.07},\"pressure\":1014,\"humidity\":79,\"dew_point\":7.04,\"wind_speed\":3.3,\"wind_deg\":75,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":99,\"rain\":2.2,\"uvi\":6.24},{\"dt\":1589540400,\"sunrise\":1589513497,\"sunset\":1589569046,\"temp\":{\"day\":14.72,\"min\":3.54,\"max\":14.72,\"night\":3.54,\"eve\":10.89,\"morn\":8.19},\"feels_like\":{\"day\":9.59,\"night\":0.85,\"eve\":8.36,\"morn\":2.39},\"pressure\":1015,\"humidity\":52,\"dew_point\":5.18,\"wind_speed\":5.71,\"wind_deg\":18,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":35,\"uvi\":6.58},{\"dt\":1589626800,\"sunrise\":1589599816,\"sunset\":1589655531,\"temp\":{\"day\":15.24,\"min\":5.32,\"max\":15.8,\"night\":5.32,\"eve\":12.41,\"morn\":7.17},\"feels_like\":{\"day\":12.34,\"night\":2.77,\"eve\":10.15,\"morn\":4.59},\"pressure\":1023,\"humidity\":45,\"dew_point\":3.47,\"wind_speed\":2.09,\"wind_deg\":328,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":0,\"uvi\":6.3},{\"dt\":1589713200,\"sunrise\":1589686137,\"sunset\":1589742014,\"temp\":{\"day\":18.1,\"min\":7.31,\"max\":18.37,\"night\":7.31,\"eve\":14.2,\"morn\":9},\"feels_like\":{\"day\":15.83,\"night\":4.66,\"eve\":11.88,\"morn\":6.86},\"pressure\":1025,\"humidity\":43,\"dew_point\":5.59,\"wind_speed\":1.73,\"wind_deg\":344,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":6,\"uvi\":6.6},{\"dt\":1589799600,\"sunrise\":1589772460,\"sunset\":1589828497,\"temp\":{\"day\":21.1,\"min\":9.49,\"max\":21.1,\"night\":9.49,\"eve\":17.04,\"morn\":11.56},\"feels_like\":{\"day\":18.8,\"night\":7.96,\"eve\":15.58,\"morn\":9.49},\"pressure\":1021,\"humidity\":44,\"dew_point\":8.7,\"wind_speed\":2.75,\"wind_deg\":344,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":0,\"uvi\":6.97},{\"dt\":1589886000,\"sunrise\":1589858785,\"sunset\":1589914979,\"temp\":{\"day\":18.98,\"min\":6.56,\"max\":18.98,\"night\":6.56,\"eve\":14.63,\"morn\":13.07},\"feels_like\":{\"day\":15.65,\"night\":4.01,\"eve\":12.03,\"morn\":11.74},\"pressure\":1023,\"humidity\":52,\"dew_point\":9.1,\"wind_speed\":4.41,\"wind_deg\":340,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":18,\"uvi\":6.84}]}";
    private Weather mockWeather = new Weather(weatherData);
    private final long MILLIS_IN_SEC = 1000;
    private final long SECS_IN_HOUR = 3600;

    @Test
    public void WeatherDatabaseBuilder_CheckSymmetry() {


        long mockDateInMillis = (mockWeather.getResponseTimestamp() + (50 * SECS_IN_HOUR)) * MILLIS_IN_SEC;
        Date mockDate = new Date(mockDateInMillis);
        int closestDay = mockWeather.getClosestDay(mockDate);
        double delta = 10e-5;

        Map<String, Object> data = DatabaseObjectBuilderRegistry.getBuilder(Weather.class).serializeToMap(mockWeather);

        Weather resultWeather = DatabaseObjectBuilderRegistry.getBuilder(Weather.class).buildFromMap(data);

        assertEquals(mockWeather.getTemp(closestDay), resultWeather.getTemp(closestDay), delta);
        assertEquals(mockWeather.getFeelsLikeTemp(closestDay), resultWeather.getFeelsLikeTemp(closestDay), delta);
        assertEquals(mockWeather.getWeather(closestDay), resultWeather.getWeather(closestDay));

        assertEquals(mockWeather.isForecastAvailable(mockDate), resultWeather.isForecastAvailable(mockDate));
        assertEquals(mockWeather.getClosestDay(mockDate), resultWeather.getClosestDay(mockDate));
        assertEquals(mockWeather.getString(), resultWeather.getString());
    }

    @Test
    public void WeatherDatabaseBuilder_HasNoLocation() {
        DatabaseObjectBuilder<Weather> builder = DatabaseObjectBuilderRegistry.getBuilder(Weather.class);
        assertFalse(builder.hasLocation());
        assertNull(builder.getLocation(mockWeather));
    }

}
