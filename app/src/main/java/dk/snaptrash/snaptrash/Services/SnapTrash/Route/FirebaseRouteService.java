package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.FirebaseTrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;
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
            .addPathSegment(auth.getUser().getId())
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

                if (response.code() == 403) {
                    throw new CompletionException(new RouteInProgressException());
                }

                JSONArray jsonArray = new JSONArray(response.body().string());

                Log.e("routeservice", "Routes length: " + String.valueOf(jsonArray.length()));

                return IntStream.range(0, jsonArray.length())
                    .mapToObj(i -> toRoute(jsonArray.optJSONObject(i)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            } catch (IOException | JSONException e) {
                throw new CompletionException(e);
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
                        } catch (InterruptedException | ExecutionException e) {
                            throw new CompletionException(e);
                        }
                    })).toArray((IntFunction<CompletableFuture<Route>[]>) CompletableFuture[]::new)
                ).get();
            } catch (InterruptedException|ExecutionException e) {
                throw new CompletionException(e);
            }
            return completableRoutes;
        });
    }

    private Optional<Route> toRoute(@Nullable JSONObject jsonRoute) {
        if(jsonRoute == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                new Route(
                    jsonRoute.getString("id"),
                    new LinkedHashSet<>(
                        IntStream.range(
                            0,
                            jsonRoute
                                .getJSONObject("data")
                                .getJSONArray("trashes")
                                .length()
                        )
                            .mapToObj(
                                value -> {
                                    try {
                                        return trashService
                                            .toTrash(
                                                jsonRoute
                                                    .getJSONObject("data")
                                                    .getJSONArray("trashes")
                                                    .getJSONObject(value)
                                            );
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            )
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList())
                    ),
                    auth.getUser().getId()
                )
            );
        } catch (JSONException|RuntimeException e) {
            Log.e("routeservice", "rip", e);
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
                return this.toRoute(
                    new JSONObject(
                        response
                            .body()
                            .string()
                    )
                );
            } catch (IOException | JSONException e) {
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
                route.getTrashes().forEach(
                    trash -> {
                        Log.e("routeservice", "reserving: " + trash.getId());
                        this.trashService.setTrashState(trash, TrashService.TrashState.RESERVED);
                    }
                );
                return route;

            } catch (Throwable e) {
//                IOException
                Log.e("routeservice", "selectroute rekked", e);
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
                route.getTrashes().forEach(
                    trash -> {
                        Log.e("routeservice", "abandoning: " + trash.getAuthorId());
                        this.trashService.setTrashState(trash, TrashService.TrashState.FREE);
                    }
                );
                return route;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
