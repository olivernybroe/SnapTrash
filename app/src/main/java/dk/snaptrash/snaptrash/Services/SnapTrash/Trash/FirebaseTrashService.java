package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;
import dk.snaptrash.snaptrash.Utils.TaskWrapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseTrashService implements TrashService, EventListener<QuerySnapshot> {

    private Set<OnTrashAddedListener> addedListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Set<OnTrashRemovedListener> removedListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Set<OnTrashPickedUpListener> pickedUpListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Set<OnPickUpVerifiedListener> verifiedListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Set<OnPickUpRejectedListener> rejectedListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Set<OnTrashStatusChangedListener> statusChangedListeners = Collections.synchronizedSet(
        new HashSet<>()
    );

    private Map<Trash, TrashState> trashes;

    private OkHttpClient client = new OkHttpClient();

    private Context context;

    AuthProvider authProvider;

    @Inject
    public FirebaseTrashService(Context context, AuthProvider authProvider) {
        this.context = context;
        this.authProvider = authProvider;
        this.trashCollection().addSnapshotListener(this);
    }

    private Query trashCollection() {
        return FirebaseFirestore.getInstance()
            .collection("trashes");
    }

    private HttpUrl.Builder urlBuilder(String userId) {
        return new HttpUrl.Builder()
            .host("us-central1-snaptrash-1507812289113.cloudfunctions.net")
            .scheme("https")
            .addPathSegments("snaptrash/users")
            .addPathSegment(userId)
            .addPathSegment("trashes");
    }

    public Optional<Trash> toTrash(@Nullable JSONObject jsonObject) {
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
                data.optString("pictureUrl", null),
                data.optString("description"),
                data.optString("created_by", null),
                data.optString("reserved_by", null)
            ));
        } catch (JSONException e) {
            return Optional.empty();
        }

    }

    private boolean reservedByCurrentUser(Trash trash, Date now) {
            return trash.getReservedUntil() != null
            && trash.getReservedUntil().after(now)
            && trash.getReservedById().equals(this.authProvider.getUser().getId());
    }

    @NonNull
    private Optional<Trash> toTrash(DocumentSnapshot documentSnapshot) {
        if(documentSnapshot.getGeoPoint("location") == null) {
            return Optional.empty();
        }

        return Optional.of(new Trash(
            documentSnapshot.getId(),
            Geo.toLatLng(documentSnapshot.getGeoPoint("location")),
            documentSnapshot.getString("pictureUrl"),
            documentSnapshot.getString("description"),
            documentSnapshot.getString("created_by"),
            documentSnapshot.getString("reserved_by"),
            documentSnapshot.getDate("reserved_until")
        ));
    }

    @NonNull
    @Override
    public CompletableFuture<Set<Trash>> trashes() {
        if (this.trashes != null) {
            return CompletableFuture.completedFuture(
                new HashSet<>(this.trashes.keySet())
            );
        }
        return CompletableFuture.supplyAsync(
            () -> {
                Request request = new Request.Builder()
                    .url(urlBuilder(authProvider.getUser().getId()).build())
                    .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray jsonArray = new JSONArray(response.body().string());

                    this.updateTrashes(
                        IntStream.range(0, jsonArray.length())
                            .mapToObj(i -> toTrash(jsonArray.optJSONObject(i)))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet())
                    );

                    return new HashSet<>(this.trashes.keySet());

                } catch (IOException|JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    @NonNull
    @Override
    public CompletableFuture<Set<Trash>> freeTrashes() {
        return this.trashes().thenApply(
            _trashes -> _trashes.stream().filter(
                trash -> this.trashes.get(trash) == TrashState.FREE
            ).collect(Collectors.toSet())
        );
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo) {
        return CompletableFuture.runAsync(
            () -> {
                Request request = new Request.Builder()
                    .url(urlBuilder(authProvider.getUser().getId())
                        .addPathSegment(trash.getId())
                        .addPathSegment("pick-up")
                        .build()
                    )
                    .build();
                //todo readd remove from backend
//                try {
//                    Response response = client.newCall(request).execute();
//
//                    if(response.code() != 204) {
//                        throw new RuntimeException("PICKUP IS NOT A 204 RESPONSE CODE.");
//                    }
                    this.setTrashState(
                        trash,
                        TrashState.PENDING_PICK_UP_CONFIRMED
                    );
                    this.pickedUpListeners.forEach(
                        listener -> listener.pickedUp(trash)
                    );
            }
        );
    }

    @Override
    public void setTrashState(Trash trash, TrashState state) {
        TrashState previousStatus = this.trashes.get(trash);
        if (previousStatus != null && previousStatus != state) {
            this.trashes.replace(trash, state);
            this.statusChangedListeners.forEach(
                listener -> listener.trashStatusChanged(trash, state)
            );
        }
    }

    @Override
    public TrashState getTrashState(Trash trash) {
        return this.trashes.get(trash);
    }

    @SuppressLint("MissingPermission")
    @Override
    public CompletableFuture<Boolean> trashCanBePickedUp(@NonNull Trash trash) {
        return TaskWrapper.wrapAsync(
            LocationServices.getFusedLocationProviderClient(this.context).getLastLocation()
        ).thenApply(
            location -> {
                try {
                    return this.trashes().get().stream().filter(
                        _trash -> Geo.distance(
                            Geo.toLatLng(_trash.getLocation()),
                            Geo.toLatLng(location)
                        ) <= 50
                    ).anyMatch(
                        _trash -> _trash == trash
                    );
                } catch (InterruptedException | ExecutionException e) {
                    throw new CompletionException(e);
                }
            }
        );
    }

    @Override
    public void addOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener) {
        this.addedListeners.add(onTrashAddedListener);
    }

    @Override
    public void removeOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener) {
        this.addedListeners.remove(onTrashAddedListener);
    }

    @Override
    public void addOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener) {
        this.removedListeners.add(onTrashRemovedListener);
    }

    @Override
    public void removedOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener) {
        this.removedListeners.remove(onTrashRemovedListener);
    }

    @Override
    public void addOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener) {
        this.pickedUpListeners.add(onTrashPickedUpListener);
    }

    @Override
    public void removedOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener) {
        this.pickedUpListeners.remove(onTrashPickedUpListener);
    }

    @Override
    public void addOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener) {
        this.verifiedListeners.add(onPickUpVerifiedListener);
    }

    @Override
    public void removeOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener) {
        this.verifiedListeners.remove(onPickUpVerifiedListener);
    }

    @Override
    public void addOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener) {
        this.rejectedListeners.add(onPickUpRejectedListener);
    }

    @Override
    public void removeOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener) {
        this.rejectedListeners.remove(onPickUpRejectedListener);
    }

    @Override
    public void addOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener) {
        this.statusChangedListeners.add(onTrashStatusChangedListener);
    }

    @Override
    public void removeOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener) {
        this.statusChangedListeners.remove(onTrashStatusChangedListener);
    }

    private void updateTrashes(Set<Trash> newTrashes) {
        if (this.trashes == null) {
            this.trashes = Collections.synchronizedMap(new HashMap<>());
        }

        Set<Trash> currentTrashes =
            this.trashes.keySet()
                .stream()
                .filter(
                    trash -> this.getTrashState(trash) != TrashState.PICKED_UP
                )
                .collect(Collectors.toSet());

        CollectionUtils.subtract(currentTrashes, newTrashes).forEach(
            trash -> {
                this.setTrashState(trash, TrashState.PICKED_UP);
                this.removedListeners.forEach(
                    listener -> listener.trashRemoved(trash)
                );
            }
        );

        CollectionUtils.subtract(newTrashes, currentTrashes).forEach(
            trash -> {
                this.trashes.put(
                    trash,
                    this.reservedByCurrentUser(trash, new Date()) ?
                        TrashState.RESERVED
                        : TrashState.FREE
                );
                this.addedListeners.forEach(
                    listener -> listener.trashAdded(trash)
                );
            }
        );

    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if(documentSnapshots == null) {
            return;
        }
        Date now = new Date();
        this.updateTrashes(
            documentSnapshots.getDocuments().stream()
                .map(this::toTrash)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(
                    trash -> trash.getReservedUntil() == null
                    || trash.getReservedUntil().before(now)
                    || trash.getReservedById().equals(
                        this.authProvider.getUser().getId()
                    )
                )
                .collect(Collectors.toSet())
        );
    }

}
