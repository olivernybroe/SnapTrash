package dk.snaptrash.snaptrash.Menu.Routes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.Route.RouteService;

import static dk.snaptrash.snaptrash.MapActivity.mGoogleApiClient;


public class RouteAdapter extends ArrayAdapter<Route> {
    RouteService routeService;

    public RouteAdapter(@NonNull Context context, RouteService routeService) {
        super(context, -1);
        this.routeService = routeService;

        @SuppressLint("MissingPermission")
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        this.addAll(routeService.getRoutes(new LatLng(location.getLatitude(), location.getLongitude())));
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
