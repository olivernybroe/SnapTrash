package dk.snaptrash.snaptrash.Utils.Geo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.R;

public class Direction {

    private Route route;

    private Direction(Route route) {
        this.route = route;
    }

    public Collection<PolylineOptions> toPolylineOptions(Context context) {
        return this.route.getLegList().stream().map(leg ->
            DirectionConverter.createPolyline(
                context,
                leg.getDirectionPoint(),
                5,
                Color.RED
            )
        ).collect(Collectors.toList());
    }

    public static CompletableFuture<Optional<Direction>> directionFromTrashes(Coordinate startAndEndPos, LinkedHashSet<Trash> trashes) {
        CompletableFuture<Optional<Direction>> completableFuture = new CompletableFuture<>();

        fromTrashes(startAndEndPos, trashes).whenComplete((directions, throwable) -> {
            if(throwable == null) {
                completableFuture.complete(directions.stream().findFirst());
            }
            else {
                completableFuture.completeExceptionally(throwable);
            }
        });

        return completableFuture;
    }

    public static CompletableFuture<Collection<Direction>> fromTrashes(Coordinate startAndEndPos, LinkedHashSet<Trash> trashes) {
        return fromTrashes(startAndEndPos, startAndEndPos, trashes);
    }

    public static CompletableFuture<Collection<Direction>> fromTrashes(Coordinate startPos, Coordinate endPos, LinkedHashSet<Trash> trashes) {
        CompletableFuture<Collection<Direction>> completableFuture = new CompletableFuture<>();

        GoogleDirection.withServerKey(Resources.getSystem().getString(R.string.google_navigation_key))
            .from(Geo.toLatLng(startPos))
            .and(trashes.stream().map(Trash::toLatLng).collect(Collectors.toList()))
            .to(Geo.toLatLng(endPos))
            .transportMode(TransportMode.WALKING)
            .execute(new DirectionCallback() {
                @Override
                public void onDirectionSuccess(com.akexorcist.googledirection.model.Direction direction, String rawBody) {
                    if(direction.isOK()) {
                        completableFuture.complete(
                            direction.getRouteList().stream().map(Direction::new).collect(Collectors.toList())
                        );
                    } else {
                       completableFuture.completeExceptionally(new RuntimeException(direction.getErrorMessage()));
                    }
                }

                @Override
                public void onDirectionFailure(Throwable t) {
                    completableFuture.completeExceptionally(t);
                }
            });

        return completableFuture;
    }
}
