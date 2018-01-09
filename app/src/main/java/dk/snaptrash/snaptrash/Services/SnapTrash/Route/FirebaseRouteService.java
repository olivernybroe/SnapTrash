package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Models.Route;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseRouteService implements RouteService {
    private OkHttpClient client = new OkHttpClient();
    private static final String TAG = "RouteService";

    @Inject
    AuthProvider auth;

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
    public CompletableFuture<Collection<Route>> getRoutes(LatLng position) {
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

    private Optional<Route> toRoute(@Nullable JSONObject jsonRoute) {
        if(jsonRoute == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new Route(
                jsonRoute.getString("id"),
                null
            ));
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    @NonNull
    @Override
    public CompletableFuture<Route> getCurrentRoute() {
        return null;
    }

    @NonNull
    @Override
    public CompletableFuture<Route> selectRoute(Route route) {
        return null;
    }
}
