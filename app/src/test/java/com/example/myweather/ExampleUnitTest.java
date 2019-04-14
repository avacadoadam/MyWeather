package com.example.myweather;

import com.android.volley.toolbox.StringRequest;
import com.example.myweather.ArgoAPI.Argo;
import com.example.myweather.ArgoAPI.ArgoCallback;
import com.example.myweather.ArgoAPI.UnitsOfMeasurements;

import org.junit.Test;
import org.mockito.Mock;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    Argo argo = new Argo(UnitsOfMeasurements.Metric);

    String testResponse = "{\"coord\":{\"lon\":32.12,\"lat\":12.21},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"02d\"}],\"base\":\"stations\",\"main\":{\"temp\":301.464,\"pressure\":1009.36,\"humidity\":59,\"temp_min\":301.464,\"temp_max\":301.464,\"sea_level\":1009.36,\"grnd_level\":965.4},\"wind\":{\"speed\":3.51,\"deg\":154.504},\"clouds\":{\"all\":8},\"dt\":1555132139,\"sys\":{\"message\":0.0032,\"country\":\"SD\",\"sunrise\":1555126843,\"sunset\":1555171390},\"id\":370510,\"name\":\"Marabba\",\"cod\":200}";


    @Test
    public void ensureUnitIsEncodedIntoURLTest(){
        argo.setUnit(UnitsOfMeasurements.Imperial);
        StringRequest url = argo.getCityWeather("Dublin",callback);
        url.getUrl().contains(UnitsOfMeasurements.Imperial.getUnitName());
    }

    @Mock
    ArgoCallback callback;

    @Test
    public void OutputStringURLIsValidOnCityNameTest(){
        StringRequest url = argo.getCityWeather("Dublin",callback);
        try {
            new URL(url.getUrl()).toURI();
            assert true;
        } catch (URISyntaxException | MalformedURLException e) {
            assert false;
        }
    }

    @Test
    public void OutputStringURLIsValidOnCoordsTest(){
        StringRequest url = argo.getCoordWeather(12.21,32.12,callback);
        try {
            new URL(url.getUrl()).toURI();
            assert true;
        } catch (URISyntaxException | MalformedURLException e) {
            assert false;
        }
    }





}