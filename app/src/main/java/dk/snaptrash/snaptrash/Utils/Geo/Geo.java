package dk.snaptrash.snaptrash.Utils.Geo;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Geo {

    private static final int earthRadius = 6371;

    public static double distance(LatLng frm, LatLng to) {

        double latDistance = Math.toRadians(to.latitude - frm.latitude);
        double lonDistance = Math.toRadians(to.longitude - frm.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(frm.latitude)) * Math.cos(Math.toRadians(to.latitude))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Geo.earthRadius * c * 1000;
    }

    public static double distance(Coordinate frm, Coordinate to) {
        return Geo.distance(
            Geo.toLatLng(frm),
            Geo.toLatLng(to)
        );
    }

    @NonNull
    public static LatLng toLatLng(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    @NonNull
    public static LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @NonNull
    public static LatLng toLatLng(Coordinate coordinate) {
        return new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
    }

}
