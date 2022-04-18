package com.example.lifeline;

import android.location.Location;

public interface LocationSubscriber {
    public void locationFound(Location location);
}
