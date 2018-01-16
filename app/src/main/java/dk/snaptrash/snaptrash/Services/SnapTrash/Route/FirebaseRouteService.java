package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.FirebaseTrashService;
import dk.snaptrash.snaptrash.Utils.Geo.Coordinate;
import dk.snaptrash.snaptrash.Utils.Geo.Direction;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseRouteService implements RouteService {
    private OkHttpClient client = new OkHttpClient();
    private static final String TAG = "RouteService";
    AuthProvider auth;
    FirebaseTrashService trashService;

    public FirebaseRouteService(AuthProvider auth, FirebaseTrashService trashService) {
        this.auth = auth;
        this.trashService = trashService;
    }

    private HttpUrl.Builder urlBuilder() {
        return new HttpUrl.Builder()
            .host("us-central1-snaptrash-1507812289113.cloudfunctions.net")
            .scheme("https")
            .addPathSegments("snaptrash/users/")
            .addPathSegment("qI68ry3rTCVv9CbujcfCF9PeP263") //TODO: add user id
            .addPathSegment("routes");
    }

    @NonNull
    @Override
    public CompletableFuture<Collection<Route>> getRoutes(Coordinate position) {
        return CompletableFuture.supplyAsync(() -> {

            Request request = new Request.Builder()
                .url(urlBuilder().build())
                .build();

            try {
                Response response = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());

                return IntStream.range(0, jsonArray.length())
                    .mapToObj(i -> toRoute(jsonArray.optJSONObject(i)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            } catch (IOException |JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NonNull
    @Override
    public CompletableFuture<Collection<Route>> getRoutesWithDirections(Coordinate position) {
        return getRoutes(position).thenApplyAsync(routes -> {
            Collection<Route> completableRoutes = new ArrayList<>();

            try {
                CompletableFuture.allOf(
                    routes.stream().map(route -> CompletableFuture.supplyAsync(() -> {
                        try {
                            route.setDirection(Direction.directionFromTrashes(position, route.getTrashes()).get());
                            completableRoutes.add(route);
                            return route;
                        } catch (InterruptedException|ExecutionException e) {
                            completableRoutes.add(route);
                            return route;
                        }
                    })).toArray((IntFunction<CompletableFuture<Route>[]>) CompletableFuture[]::new)
                ).get();
            } catch (InterruptedException|ExecutionException e) {
                throw new RuntimeException(e);
            }

            return completableRoutes;
        });
    }

    private Optional<Route> toRoute(@Nullable JSONObject jsonRoute) {
        if(jsonRoute == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new Route(
                jsonRoute.getString("id"),
                new LinkedHashSet<>(
                    IntStream.range(0, jsonRoute.getJSONObject("data").getJSONArray("trashes").length())
                    .mapToObj(value -> {
                        try {
                            return trashService.toTrash(jsonRoute.getJSONObject("data").getJSONArray("trashes").getJSONObject(0));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList())
                ),
                auth.getUser().getId()
            ));
        } catch (JSONException|RuntimeException e) {
            return Optional.empty();
        }
    }

    @NonNull
    @Override
    public CompletableFuture<Optional<Route>> getCurrentRoute() {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder()
                    .addPathSegment("active")
                    .build()
                ).build();

            try {
                Response response = client.newCall(request).execute();
                return toRoute(new JSONObject(response.body().string()));

            } catch (IOException|JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NonNull
    @Override
    public CompletableFuture<Route> selectRoute(Route route) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder()
                    .addPathSegment(route.getId())
                    .addPathSegment("reserve")
                    .build()
                ).build();

            try {
                client.newCall(request).execute();
                return route;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NonNull
    @Override
    public CompletableFuture<Route> abandonRoute(Route route) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder()
                    .addPathSegment(route.getId())
                    .addPathSegment("unreserve")
                    .build()
                ).build();

            try {
                client.newCall(request).execute();
                return route;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
