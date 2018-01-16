package dk.snaptrash.snaptrash.Services.SnapTrash.Route.Routes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
        void routeFinished(MapRoute mapRoute);
    }

    private Set<OnRouteFinishedListener> listeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    public enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    @Getter private Status status = Status.IN_PROGRESS;

    @Getter private Route route;
    private GoogleMap googleMap;
    private Activity activity;
    private TrashService trashService;

    private Collection<Polyline> polyLines;

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

        this.trashService.addOnTrashRemovedListener(
            trash -> {
                if (this.route.getTrashes().contains(trash)) {
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
                if (this.route.getTrashes().contains(trash)) {
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
                if (this.route.getTrashes().contains(trash)) {
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
        if (this.status != Status.IN_PROGRESS) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new Exception());
            return future;
        }
        Log.e("maproute", "updating for: " + this.route.getTrashes().stream().map(trash -> this.trashService.getTrashState(trash)).collect(Collectors.toSet()));
        LinkedHashSet<Trash> trashes = new LinkedHashSet<>(
            this.route.getTrashes()
                .stream()
                .filter(
                    trash -> {
                        TrashService.TrashState state = this.trashService.getTrashState(trash);
                        return
                            state == TrashService.TrashState.FREE
                            || state == TrashService.TrashState.RESERVED;
                    }
                )
                .collect(Collectors.toList())
        );
        Log.e("maproute", "updating for: " + trashes);
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
                trash -> this.trashService.getTrashState(trash) == TrashService.TrashState.PICKED_UP
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

    private synchronized void finish(boolean completed) {
        if (this.status != Status.IN_PROGRESS) {
            return;
        }
        this.unDraw();
        this.status = completed ? Status.COMPLETED : Status.CANCELED;
        this.listeners.forEach(
            listeners -> listeners.routeFinished(this)
        );
    }

    public void cancel() {
        this.finish(false);
    }

    private void complete() {
        this.finish(true);
    }


}
