package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.Utils.Geo.Coordinate;

public interface RouteService {

    @NonNull
    CompletableFuture<Collection<Route>> getRoutes(Coordinate position);

    @NonNull
    CompletableFuture<Collection<Route>> getRoutesWithDirections(Coordinate position);

    @NonNull CompletableFuture<Optional<Route>> getCurrentRoute();

    @NonNull CompletableFuture<Route> selectRoute(Route route);

    @NonNull CompletableFuture<Route> abandonRoute(Route route);

}
