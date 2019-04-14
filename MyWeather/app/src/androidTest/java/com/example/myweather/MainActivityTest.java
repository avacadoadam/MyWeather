package com.example.myweather;

import android.content.ComponentName;
import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.myweather.ArgoAPI.UnitsOfMeasurements;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;


@RunWith(AndroidJUnit4.class)
public class  MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> IntentsTestRule = new IntentsTestRule<>(MainActivity.class);


    /**
     * Navigates to settings then changes units to Imperial further checking if the UI implement the
     * settings in unit_field
     *
     * * NOTE uses Thread.sleep may raise false alarms
     */
    @Test
    public void changeSettingUnitsToImperialTest() {
        String imperial = UnitsOfMeasurements.Imperial.getUnitName();

        navigateToSettingsTest();

        onView(withText(getResourceString(R.string.unit_setting_option))).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));
        onView(withText(imperial)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.unit_field)).check(matches(withText(imperial)));
    }
    /**
     * Navigates to settings then changes units to Metric further checking if the UI implement the
     * settings in unit_field
     *
     * * NOTE uses Thread.sleep may raise false alarms
     */
    @Test
    public void changeSettingUnitsToMetricTest() {
        String Metric = UnitsOfMeasurements.Metric.getUnitName();

        navigateToSettingsTest();

        onView(withText(getResourceString(R.string.unit_setting_option))).perform(click());

        intended(hasComponent(SettingsActivity.class.getName()));
        onView(withText(Metric)).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.unit_field)).check(matches(withText(Metric)));
    }


    private String getResourceString(int id) {
        Context targetContext = getTargetContext();
        return targetContext.getResources().getString(id);
    }

    @Test
    public void ensureUnitIsDisplayingRealMeasurementType() {
        changeSettingUnitsToImperialTest();
        //todo


    }

    /**
     * Tests that the floatingButton refresh updates UI when called
     * * NOTE uses Thread.sleep may raise false alarms
     */
    @Test
    public void FloatingButtonRefreshUpdateUI(){
        onView(withId(R.id.floatingActionButton)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.pressure_value)).check(matches(not(withText(""))));
        onView(withId(R.id.temp_value)).check(matches(not(withText(""))));
        onView(withId(R.id.city_name_field)).check(matches(not(withText(""))));
        onView(withId(R.id.humidity_value)).check(matches(not(withText(""))));
        onView(withId(R.id.wind_speed_value)).check(matches(not(withText(""))));
    }
    @Test
    public void navigateToSettingsTest() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // opens settings
        onView(withText(getResourceString(R.string.action_settings))).perform(click());

        onView(withText(getResourceString(R.string.pref_header_units))).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), SettingsActivity.class)));
    }

    /**
     * Checks that Temperature field have formatted values properly based on Imperial
     */
    private void ensureTemperatureDataFormatsAreCorrectBasedOnImperial() {
        String Imperial = UnitsOfMeasurements.Imperial.getUnitName();

        onView(withId(R.id.unit_field))
                .check(matches(withText(containsString(Imperial))));
        //checking temp
        onView(withId(R.id.temp_value))
                .check(matches(withText(containsString("°F"))));

    }
    /**
     * Checks that wind field have formatted values properly based on Imperial
     */
    private void ensureWindDataFormatsAreCorrectBasedOnImperial() {
        //get unit name
        //if metric/imperial test textfeilds with regex
        String Imperial = UnitsOfMeasurements.Imperial.getUnitName();

        onView(withId(R.id.unit_field))
                .check(matches(withText(containsString(Imperial))));
        //checking temp
        onView(withId(R.id.wind_speed_value))
                .check(matches(withText(containsString("mph"))));

    }

    /**
     * Go to setting and changes unit to imperial then test that Wind and Temperature field are properly formatted
     *
     * * NOTE uses Thread.sleep may raise false alarms
     */
    @Test
    public void ensureImperialDataFormatIsCorrectAfterChangingInSettings(){
        changeSettingUnitsToImperialTest();
        ensureTemperatureDataFormatsAreCorrectBasedOnImperial();
        ensureWindDataFormatsAreCorrectBasedOnImperial();
    }
    /**
     * Go to setting and changes unit to Metric then test that Wind and Temperature field are properly formatted
     * * NOTE uses Thread.sleep may raise false alarms
     */
    @Test
    public void ensureMetricDataFormatIsCorrectAfterChangingInSettings(){
        changeSettingUnitsToMetricTest();
        ensureTemperatureDataFormatsAreCorrectBasedOnMetric();
        ensureWindDataFormatsAreCorrectBasedOnMetric();
    }
    /**
     * Checks that Temperature field have formatted values properly based on Imperial
     */
    private void ensureTemperatureDataFormatsAreCorrectBasedOnMetric() {
        String Metric = UnitsOfMeasurements.Metric.getUnitName();
        onView(withId(R.id.unit_field))
                .check(matches(withText( containsString(Metric))));
        //checking temp
        onView(withId(R.id.temp_value))
                .check(matches(withText(containsString("°C"))));

    }
    /**
     * Checks that wind field have formatted values properly based on Imperial
     */
    private void ensureWindDataFormatsAreCorrectBasedOnMetric() {
        String Metric = UnitsOfMeasurements.Metric.getUnitName();
        onView(withId(R.id.unit_field))
                .check(matches(withText(containsString(Metric))));

        onView(withId(R.id.wind_speed_value))
                .check(matches(withText(containsString("m/s"))));
    }

    //Todo implement theme test


}