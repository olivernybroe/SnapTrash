package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.Collection;

import dk.snaptrash.snaptrash.Models.Route;

public interface RouteService {

    @NonNull Task<Collection<Route>> getRoutes(LatLng position);

    @NonNull Task<Route> getCurrentRoute();

    @NonNull Task<Void> selectRoute(Route route);

}
