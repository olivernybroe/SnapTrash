package dk.snaptrash.snaptrash.Utils.Geo;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import lombok.Getter;

public class Coordinate implements Serializable {

    @Getter private double latitude;
    @Getter private double longtitude;

    public Coordinate(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

}
