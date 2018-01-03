package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;

import dk.snaptrash.snaptrash.Models.Route;

public interface RouteService {

    @NonNull
    Collection<Route> getRoutes(LatLng position);

    Route getCurrentRoute();

    RouteService selectRoute(Route route);
}
