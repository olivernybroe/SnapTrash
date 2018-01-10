package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.security.auth.login.LoginException;

import dk.snaptrash.snaptrash.Models.Trash;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseTrashService implements TrashService, EventListener<QuerySnapshot> {
    private List<EventListener<Collection<Trash>>> eventListeners = Collections.synchronizedList(new ArrayList<>());
    private OkHttpClient client = new OkHttpClient();

    public FirebaseTrashService() {
        trashCollection().addSnapshotListener(this);
    }

    public Query trashCollection() {
        return FirebaseFirestore.getInstance().collection("trashes").whereLessThan("reserved_until", new Date());
    }

    private HttpUrl.Builder urlBuilder() {
        return new HttpUrl.Builder()
            .host("us-central1-snaptrash-1507812289113.cloudfunctions.net")
            .scheme("https")
            .addPathSegments("snaptrash/trashes");
    }

    @NonNull
    @Override
    public CompletableFuture<Collection<Trash>> closeTo(@NonNull LatLng location) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder().build())
                .build();

            try {
                Response response = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());

                return IntStream.range(0, jsonArray.length())
                        .mapToObj(i -> toTrash(jsonArray.optJSONObject(i)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

            } catch (IOException|JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NonNull
    public FirebaseTrashService addTrashChangeListener(EventListener<Collection<Trash>> eventListener) {
        this.eventListeners.add(eventListener);
        return this;
    }

    private Optional<Trash> toTrash(@Nullable JSONObject jsonObject) {
        if(jsonObject == null) {
            return Optional.empty();
        }

        if(jsonObject.optString("type") == null || !jsonObject.optString("type").equals("Trash")) {
            return Optional.empty();
        }

        JSONObject data = jsonObject.optJSONObject("data");
        if(data == null) {
            return Optional.empty();
        }

        JSONObject location = data.optJSONObject("location");
        if(location == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new Trash(
                jsonObject.getString("id"),
                new LatLng(location.getDouble("latitude"), location.getDouble("longitude")),
                jsonObject.optString("pictureUrl", null),
                data.optString("description"),
                null
            ));
        } catch (JSONException e) {
            return Optional.empty();
        }

    }

    @Nullable
    private Optional<Trash> toTrash(DocumentSnapshot documentSnapshot) {
        if(documentSnapshot.getGeoPoint("location") == null) {
            return Optional.empty();
        }
        return Optional.of(new Trash(
            documentSnapshot.getId(),
            this.toLatLng(documentSnapshot.getGeoPoint("location")),
            documentSnapshot.getString("pictureUrl"),
            documentSnapshot.getString("description"),
            documentSnapshot.getString("authorId")
        ));
    }

    GeoPoint toGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    @NonNull
    private LatLng toLatLng(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    @NonNull
    @Override
    public CompletableFuture<Trash> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder()
                    .addPathSegment(trash.getId())
                    .addPathSegment("pick-up")
                    .build()
                )
                .build();

            try {
                Response response = client.newCall(request).execute();

                if(response.code() != 204) {
                    throw new RuntimeException("PICKUP IS NOT A 204 RESPONSE CODE.");
                }

                return trash;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if(documentSnapshots == null) {
            return;
        }
        Log.e("TRASH", "TRASH CHANGE!");
        eventListeners.forEach(eventListener -> eventListener.onEvent(
            documentSnapshots.getDocuments().stream().map(this::toTrash)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()),
            e
        ));
    }
}
