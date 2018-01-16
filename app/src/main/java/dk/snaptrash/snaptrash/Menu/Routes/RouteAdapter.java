package dk.snaptrash.snaptrash.Menu.Routes;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;


public class RouteAdapter extends ArrayAdapter<Route> {
    RouteService routeService;

    @SuppressLint("MissingPermission")
    public RouteAdapter(@NonNull RouteDialog dialog, View progressBar, RouteService routeService) {
        super(dialog.getActivity(), -1);
        this.routeService = routeService;

        LocationServices.getFusedLocationProviderClient(dialog.getActivity()).getLastLocation().addOnSuccessListener(location -> {
            if(location == null) {
                dialog.getActivity().runOnUiThread(() -> {
                    Toast.makeText(dialog.getActivity(), "Failed finding your location.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                return;
            }
            routeService.getRoutesWithDirections(Geo.toCoordinate(location)).whenComplete((routes, throwable) -> {
                if (throwable == null) {
                    dialog.getActivity().runOnUiThread(() -> {
                        this.addAll(routes);
                        if(progressBar != null) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    dialog.getActivity().runOnUiThread(() -> {
                        Log.e("RouteAdapter", "failed getting the routes.", throwable);
                        Toast.makeText(dialog.getActivity(), R.string.connectionFailed, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            });
        }).addOnFailureListener(e -> {
            Log.e("RouteAdapter", "Failed getting current location.", e);
            dialog.getActivity().runOnUiThread(() -> {
                Toast.makeText(dialog.getActivity(), "Failed finding your location.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
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
        TextView additional = convertView.findViewById(R.id.additional);
        TextView title = convertView.findViewById(R.id.routeTitle);

        int duration = route.getDirection().getDuration()/60;
        double length = route.getDirection().getDistance();

        additional.setText(String.format("Length: %s, Approx: %s",
            length >= 1000 ? Math.round(length/1000)+" km" : Math.round(length)+ " m",
            duration >= 100 ? duration/60+" hours" : duration+" minutes"
        ));
        title.setText(route.getDirection().getDescription());

        return convertView;
    }


}
