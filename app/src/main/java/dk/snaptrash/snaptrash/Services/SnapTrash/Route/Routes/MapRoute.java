package dk.snaptrash.snaptrash.Services.SnapTrash.Route.Routes;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
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

//    public
//
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

        this.trashService.addOnTrashRemovedListener(
            trash -> {
                Optional<Trash> removedTrash = this.route.getTrashes()
                    .stream()
                    .filter(_trash -> _trash == trash)
                    .findAny();
                if (removedTrash.isPresent()) {
                    removedTrash.get().setStatus(Trash.Status.PICKED_UP);
                    if (!this.checkCompleted()) {
                        this.calculateRoute(
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
                    this.calculateRoute(
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
                    this.calculateRoute(
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

    private synchronized CompletableFuture<Void> calculateRoute(Coordinate origin) {
        return Direction.directionFromTrashes(
            origin,
            new LinkedHashSet<>(
                this.route.getTrashes()
                    .stream()
                    .filter(trash -> trash.getStatus() == Trash.Status.AVAILABLE)
                    .collect(Collectors.toList())
            )
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
            this.finish();
            return true;
        } else {
            return false;
        }
    }

    private void unDraw() {
        if (this.polyLines != null) {
            this.activity.runOnUiThread(
                () -> this.polyLines.forEach(Polyline::remove)
            );
        }
        this.polyLines = new ArrayList<>();
    }

    public void cancel() {
        this.status = Status.CANCELED;
        this.unDraw();
    }

    private void finish() {
        this.status = Status.COMPLETED;
        this.unDraw();
    }

}
