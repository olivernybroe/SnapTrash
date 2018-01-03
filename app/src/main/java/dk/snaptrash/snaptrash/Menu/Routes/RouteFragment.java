package dk.snaptrash.snaptrash.Menu.Routes;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;

public class RouteFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Inject RouteService routeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        ListView listView = view.findViewById(R.id.routeAdapterView);
        listView.setAdapter(new RouteAdapter(view.getContext(), routeService));
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Route route = (Route) adapterView.getItemAtPosition(i);

        routeService.selectRoute(route);
        getFragmentManager().popBackStack();
    }
}
