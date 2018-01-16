package dk.snaptrash.snaptrash.Menu.Routes;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.function.BiConsumer;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;

public class RouteDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    @Inject RouteService routeService;
    Listener listener;
    private ListView listView;
    private ProgressBar progressBar;

    public interface Listener {
        void onRouteSelected(Route route);
    }

    public RouteDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        progressBar = view.findViewById(R.id.routeDialogProgressBar);
        listView = view.findViewById(R.id.routeAdapterView);
        listView.setAdapter(new RouteAdapter(this, progressBar, routeService));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e("RouteDialog", "item clicked");
        listView.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Route route = (Route) adapterView.getItemAtPosition(i);

        routeService.selectRoute(route).whenComplete((selectedRoute, throwable) -> {
            if(throwable == null) {
                this.getActivity().runOnUiThread(() -> {
                    this.dismiss();
                    if(listener != null) {
                        listener.onRouteSelected(selectedRoute);
                    }
                });
            }
            else {
                this.getActivity().runOnUiThread(() -> {
                    this.dismiss();
                    Log.e("RouteDialog", "failed");
                    Toast.makeText(this.getActivity(), "Failed choosing the route, please try again.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
