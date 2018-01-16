package dk.snaptrash.snaptrash.Services.SnapTrash.Route.Routes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;
import dk.snaptrash.snaptrash.Utils.Geo.Coordinate;
import dk.snaptrash.snaptrash.Utils.Geo.Direction;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;
import lombok.Getter;

public class MapRoute {

    public interface OnRouteFinishedListener {
        public void routeFinised(MapRoute mapRoute);
    }

    Set<OnRouteFinishedListener> listeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    @Getter private Status status = Status.IN_PROGRESS;

    private Route route;
    private GoogleMap googleMap;
    private Activity activity;
    private TrashService trashService;

    private List<Polyline> polyLines;

    @SuppressLint("MissingPermission")
    public MapRoute(
        Route route,
        Activity activity,
        GoogleMap googleMap,
        TrashService trashService
    ) {
        this.route = route;
        this.activity = activity;
        this.googleMap = googleMap;
        this.trashService = trashService;

        Log.e("maproute", "start constructor");

        this.trashService.addOnTrashRemovedListener(
            trash -> {
                Optional<Trash> removedTrash = this.route.getTrashes()
                    .stream()
                    .filter(_trash -> _trash == trash)
                    .findAny();
                if (removedTrash.isPresent()) {
                    removedTrash.get().setStatus(Trash.Status.PICKED_UP);
                    if (!this.checkCompleted()) {
                        this.update(
                            Geo.toCoordinate(
                                LocationServices.FusedLocationApi.getLastLocation(
                                    MapActivity.googleApiClient
                                )
                            )
                        );
                    }
                }
            }
        );

        this.trashService.addOnTrashPickedUpListener(
            trash -> {
                Optional<Trash> removedTrash = this.route.getTrashes()
                    .stream()
                    .filter(_trash -> _trash == trash)
                    .findAny();
                if (removedTrash.isPresent()) {
                    removedTrash.get().setStatus(Trash.Status.PENDING_REMOVAL_CONFIRMED);
                    this.update(
                        Geo.toCoordinate(
                            LocationServices.FusedLocationApi.getLastLocation(
                                MapActivity.googleApiClient
                            )
                        )
                    );
                }
            }
        );

        this.trashService.addOnPickUpRejectedListener(
            trash -> {
                Optional<Trash> removedTrash = this.route.getTrashes()
                    .stream()
                    .filter(_trash -> _trash == trash)
                    .findAny();
                if (removedTrash.isPresent()) {
                    removedTrash.get().setStatus(Trash.Status.AVAILABLE);
                    this.update(
                        Geo.toCoordinate(
                            LocationServices.FusedLocationApi.getLastLocation(
                                MapActivity.googleApiClient
                            )
                        )
                    );
                }
            }
        );
    }

    public MapRoute addOnRouteFinishedListener(OnRouteFinishedListener onRouteFinishedListener) {
        this.listeners.add(onRouteFinishedListener);
        return this;
    }

    public MapRoute removeOnRouteFinishedListener(OnRouteFinishedListener onRouteFinishedListener) {
        this.listeners.remove(onRouteFinishedListener);
        return this;
    }

    public synchronized CompletableFuture<Void> update(Coordinate origin) {
        Log.e("maproute", "updating");
        LinkedHashSet<Trash> trashes = new LinkedHashSet<>(
            this.route.getTrashes()
                .stream()
                .filter(trash -> trash.getStatus() == Trash.Status.AVAILABLE)
                .collect(Collectors.toList())
        );
        Log.e("maproute", trashes.toString());
        return Direction.directionFromTrashes(
            origin,
            trashes
        ).thenAccept(
            direction -> direction.ifPresent(
                    _direction -> this.activity.runOnUiThread(
                    () -> {
                        this.unDraw();
                        _direction
                            .toPolylineOptions(this.activity)
                            .forEach(
                                polylineOptions -> MapRoute.this.polyLines.add(
                                    MapRoute.this.googleMap.addPolyline(polylineOptions)
                                )
                            );
                    }
                )
            )
        );
    }

    private boolean checkCompleted() {
        if (
            this.route.getTrashes().stream().allMatch(
                trash -> trash.getStatus() == Trash.Status.PICKED_UP
            )
        ) {
            this.complete();
            return true;
        } else {
            return false;
        }
    }

    private synchronized void unDraw() {
        if (this.polyLines != null) {
            this.activity.runOnUiThread(
                () -> this.polyLines.forEach(Polyline::remove)
            );
        }
        this.polyLines = new ArrayList<>();
    }

    private void finish(boolean completed) {
        this.unDraw();
        this.status = completed ? Status.COMPLETED : Status.CANCELED;
        this.listeners.forEach(
            listeners -> listeners.routeFinised(this)
        );
    }

    public void cancel() {
        this.finish(false);
    }

    private void complete() {
        this.finish(true);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.status == Status.IN_PROGRESS) {
            Log.w("MapRoute", "Finalizing in progress MapRoute");
            this.finish(false);
        }
    }
}
