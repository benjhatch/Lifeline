package com.example.lifeline;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;




import com.example.lifeline.ui.dashboard.DashboardFragment;

public class ExampleUnitTest {
    @Test
    public void testBMI() {
        assertEquals(DashboardFragment.calcBMI(60,150), 29.3,0.1);
    }
    @Test
    public void testBMRMale() {
        DashboardFragment.sex = "Male";
        DashboardFragment.active = false;
        assertEquals(DashboardFragment.calcBMR(), 2109,0.1);
    }

    @Test
    public void testBMRFemale() {
        DashboardFragment.sex = "Female";
        DashboardFragment.active = false;
        assertEquals(DashboardFragment.calcBMR(), 1862,0.1);
    }

    @Test
    public void testBMRMaleActive() {
        DashboardFragment.sex = "Male";
        DashboardFragment.active = true;
        assertEquals(DashboardFragment.calcBMR(), 2724,0.1);
    }

    @Test
    public void testBMRFemaleActive() {
        DashboardFragment.sex = "Female";
        DashboardFragment.active = true;
        assertEquals(DashboardFragment.calcBMR(), 2405,0.1);
    }

}