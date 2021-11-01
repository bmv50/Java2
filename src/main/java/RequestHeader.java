import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class RequestHeader {

    static OkHttpClient client = new OkHttpClient();
    static ObjectMapper objectMapper = new ObjectMapper();


    public static String detectCity(String cityName) throws IOException {

        HttpUrl detectCityUrl = new HttpUrl.Builder()
                .scheme("http")
                .host(BaseParams.HOST)
                .addPathSegment("locations")
                .addPathSegment("v1")
                .addPathSegment("cities")
                .addPathSegment("search")
                .addQueryParameter("apikey", BaseParams.API_KEY)
                .addQueryParameter("q", cityName)
                .build();

        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(detectCityUrl)
                .build();

        Response response = client.newCall(request).execute();

        String json = response.body().string();

        if(!response.isSuccessful()) {
            throw new IOException("Запрос не выполнен" + "с кодом " + response.code() + "с телом " + response.body().string());
        }

        if(objectMapper.readTree(json).size() >0) {
            String city = objectMapper.readTree(json).get(0).at("/LocalizedName").asText();
            String country = objectMapper.readTree(json).get(0).at("/Country/LocalizedName").asText();
            System.out.println(country + " " + city);
        }
        else
        {
            throw new IOException("Сервер не вернул города");
        }
        return objectMapper.readTree(json).get(0).at("/Key").asText();
    }

    public static String detectWeather(String cityCode) throws IOException {

        HttpUrl detectWeatherUrl = new HttpUrl.Builder()
                .scheme("http")
                .host(BaseParams.HOST)
                .addPathSegment("forecasts")
                .addPathSegment("v1")
                .addPathSegment("daily")
                .addPathSegment("5day")
                .addPathSegment(cityCode)
                .addQueryParameter("apikey", BaseParams.API_KEY)
                .addQueryParameter("metric", "true")
                .build();

        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(detectWeatherUrl)
                .build();

        Response response = client.newCall(request).execute();

        if(!response.isSuccessful()) {
            throw new IOException("Запрос не выполнен успешно" + "с кодом " + response.code() + "с телом " + response.body().string());
        }

        return response.body().string();
    }
}
