package com.example.lifeline;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.lifeline.ui.dashboard.DashboardFragment;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
//    @Test
//    public void testUpdateCaloriesBasic() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        DashboardFragment.button = new Button(appContext);
//        DashboardFragment.calTextView = new TextView(appContext);
//        DashboardFragment.ageInYears = 40;
//        DashboardFragment.ppw = 0;
//        assertEquals(DashboardFragment.updateCalories(), "You will need to consume 1980 calories a day in order to reach this goal.");
//    }
//    @Test
//    public void testUpdateCaloriesLow() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        DashboardFragment.button = new Button(appContext);
//        DashboardFragment.calTextView = new TextView(appContext);
//        DashboardFragment.ageInYears = 94;
//        DashboardFragment.ppw = 2;
//        assertEquals(DashboardFragment.updateCalories(), "You will need to consume 612 calories a day in order to reach this goal. *Warning* Eating this amount of calories per day can be a health risk.");
//    }
//    @Test
//    public void testUpdateCaloriesUnhealthy() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        DashboardFragment.button = new Button(appContext);
//        DashboardFragment.calTextView = new TextView(appContext);
//        DashboardFragment.ageInYears = 94;
//        DashboardFragment.ppw = 3;
//        assertEquals(DashboardFragment.updateCalories(), "You will need to consume 112 calories a day in order to reach this goal. Losing/Gaining more than 2 pounds per week can be a very difficult goal to achieve. People have more success with a slower approach. *Warning* Eating this amount of calories per day can be a health risk.");
//    }
}