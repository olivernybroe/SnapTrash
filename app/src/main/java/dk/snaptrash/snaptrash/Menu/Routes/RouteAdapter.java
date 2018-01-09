package dk.snaptrash.snaptrash.Menu.Routes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.Collection;
import java.util.function.BiConsumer;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;

import static dk.snaptrash.snaptrash.MapActivity.mGoogleApiClient;


public class RouteAdapter extends ArrayAdapter<Route> {
    RouteService routeService;

    @SuppressLint("MissingPermission")
    public RouteAdapter(@NonNull Activity activity, RouteService routeService) {
        super(activity, -1);
        this.routeService = routeService;


        LocationServices.getFusedLocationProviderClient(activity).getLastLocation().addOnSuccessListener(location ->
            routeService.getRoutes(new LatLng(location.getLatitude(), location.getLongitude())).whenComplete((routes, throwable) -> {
                if (throwable == null) {
                    activity.runOnUiThread(() -> this.addAll(routes));
                }
                else {
                    Log.e("RouteAdapter", "failed getting the routes.", throwable);
                }
            })
        ).addOnFailureListener(e -> {
            Log.e("RouteAdapter", "Failed getting current location.", e);
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Route route = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_route, parent, false);
        }

        return convertView;
    }


}
