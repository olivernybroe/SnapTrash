package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.Collection;

import dk.snaptrash.snaptrash.Models.Route;

public class FirebaseRouteService implements RouteService {

    @NonNull
    @Override
    public Task<Collection<Route>> getRoutes(LatLng position) {
        return null;
    }

    @NonNull
    @Override
    public Task<Route> getCurrentRoute() {
        return null;
    }

    @NonNull
    @Override
    public Task<Void> selectRoute(Route route) {
        return null;
    }
}
