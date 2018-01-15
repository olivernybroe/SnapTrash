package dk.snaptrash.snaptrash.Services.SnapTrash.Route.Routes;

import android.graphics.Color;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;

import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import lombok.Getter;

public class MapRoute {

    private enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    @Getter private Status status = Status.IN_PROGRESS;

    private Route route;
    private GoogleMap googleMap;

    private List<Polygon> polygons;

    public MapRoute(GoogleMap googleMap, String serverKey) {
        this.googleMap = googleMap;

    }

    private void calculateRoute() {

//        GoogleDirection.withServerKey(serverKey)
//            .from(new LatLng(55.730177, 12.397181))
//            .and(new LatLng(55.730917, 12.395164))
//            .and(new LatLng(55.730115, 12.398567))
//            .and(new LatLng(55.730784, 12.397488))
//            .to(new LatLng(55.730177, 12.397181))
//            .transportMode(TransportMode.WALKING)
//            .execute(new DirectionCallback() {
//                @Override
//                public void onDirectionSuccess(Direction direction, String rawBody) {
//                    if(direction.isOK()) {
//                        Log.e("DIRECTION", "is ok");
//                        MapActivity.this.runOnUiThread(() -> {
//
//                            Log.e("DIRECTION", direction.getRouteList().get(0).getLegList().get(0).getEndAddress());
//
//                            direction.getRouteList().get(0).getLegList().stream().map((leg) ->
//                                DirectionConverter.createPolyline(
//                                    MapActivity.this,
//                                    leg.getDirectionPoint(),
//                                    5,
//                                    Color.RED
//                                )).forEach(googleMap::addPolyline);
//                        });
//
//                        // Do something
//                    } else {
//                        Log.e("DIRECTION", "is not ok");
//                        // Do something
//                    }
//                }
//
//                @Override
//                public void onDirectionFailure(Throwable t) {
//                    Log.e("DIRECTION", "is really not ok", t);
//                }
//            });
    }

    private void end() {
        this.polygons.forEach(Polygon::remove);
    }

    public void remove() {
        this.status = Status.CANCELED;
        this.end();
    }

    public void finish() {
        this.status = Status.COMPLETED;
        this.end();
    }

}
