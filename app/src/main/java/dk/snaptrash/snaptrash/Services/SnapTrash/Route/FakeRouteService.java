package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Collection;

import dk.snaptrash.snaptrash.Models.Route;

public class FakeRouteService implements RouteService {
    @NonNull
    @Override
    public Collection<Route> getRoutes(LatLng position) {
        return Arrays.asList(
                new Route("1", null),
                new Route("2", null),
                new Route("3", null)
        );
    }

    @Override
    public Route getCurrentRoute() {
        return null;
    }

    @Override
    public RouteService selectRoute(Route route) {
        return null;
    }
}
