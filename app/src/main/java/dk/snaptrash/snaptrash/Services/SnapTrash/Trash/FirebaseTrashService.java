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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
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
import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;
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

    UserService userService;

    @Inject
    public FirebaseTrashService(Context context, AuthProvider authProvider, UserService userService) {
        this.context = context;
        this.authProvider = authProvider;
        this.userService = userService;
        this.trashCollection().addSnapshotListener(this);
        this.userService.addOnUserLoggedInListener(
            user -> this.reset()
        );
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

    private HttpUrl.Builder pickUpBuilder(String trashId) {
        return new HttpUrl.Builder()
            .host("us-central1-snaptrash-1507812289113.cloudfunctions.net")
            .scheme("https")
            .addPathSegments("snaptrash/trashes")
            .addPathSegment(trashId)
            .addPathSegment("pick-up");
    }

    public Optional<Trash> toTrash(@Nullable JSONObject jsonObject) {
        if(jsonObject == null) {
            Log.e("trashservice", "failed to trash: empty");
            return Optional.empty();
        }

        if(jsonObject.optString("type") == null || !jsonObject.optString("type").equals("Trash")) {
            Log.e("trashservice", "failed to trash: no type or not trash");
            return Optional.empty();
        }

        JSONObject data = jsonObject.optJSONObject("data");
        if(data == null) {
            Log.e("trashservice", "failed to trash: no data");
            return Optional.empty();
        }

        JSONObject location = data.optJSONObject("location");
        if(location == null) {
            Log.e("trashservice", "failed to trash: no location");
            return Optional.empty();
        }

        Date date = null;
        String dateString = data.optString("reserved_until", "");
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            Log.e("trashservice", "failed parse date", e);
        }

        try {
            return Optional.of(new Trash(
                jsonObject.getString("id"),
                new LatLng(location.getDouble("latitude"), location.getDouble("longitude")),
                data.optString("pictureUrl", null),
                data.optString("description"),
                data.optString("created_by", null),
                data.optString("reserved_by", null),
                date

            ));
        } catch (JSONException e) {
            Log.e("trashservice", "failed to trash", e);
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

        return Optional.of(
            new Trash(
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
        Log.e("trashservice", "get trashes: " + this.trashes);
        if (this.trashes != null) {
            return CompletableFuture.completedFuture(
                new HashSet<>(this.trashes.keySet())
            );
        }
        Log.e("trashservice", "trash er null");
        return CompletableFuture.supplyAsync(
            () -> {
                Log.e("trashservice", "fetching trash...");
                Request request = new Request.Builder()
                    .url(urlBuilder(authProvider.getUser().getId()).build())
                    .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray jsonArray = new JSONArray(response.body().string());

                    Log.e("trashservice", "fetched!: " + jsonArray.length());

                    this.updateTrashes(
                        IntStream.range(0, jsonArray.length())
                            .mapToObj(i -> toTrash(jsonArray.optJSONObject(i)))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toSet())
                    );

                    Log.e("trashservice", "update done");

                    return new HashSet<>(this.trashes.keySet());

                } catch (Throwable e) {
//                    IOException|JSONException
                    Log.e("trashservice", "fetch failed", e);
                    throw new CompletionException(e);
                }
            }
        );
    }

    @NonNull
    @Override
    public CompletableFuture<Set<Trash>> availableTrashes() {
        return this.trashes().thenApply(
            _trashes -> _trashes.stream().filter(
                trash ->
                    this.trashes.get(trash) == TrashState.FREE
                    || this.trashes.get(trash) == TrashState.RESERVED
            ).collect(Collectors.toSet())
        );
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo) {
        return CompletableFuture.runAsync(
            () -> {
                Request request = new Request.Builder()
                    .url(
                        this.pickUpBuilder(trash.getId())
                            .build()
                    )
                    .build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.code() != 204) {
                        throw new CompletionException(
                            new Exception("PICKUP IS NOT A 204 RESPONSE CODE.")
                        );
                    }

                    this.setTrashState(
                        trash,
                        TrashState.PENDING_PICK_UP_CONFIRMED
                    );
                    this.pickedUpListeners.forEach(
                        listener -> listener.pickedUp(trash)
                    );
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
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
    @NonNull
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
        Log.e("trashservice", "update trashes: " + this.trashes + " new trashes: " + newTrashes);
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

        Log.e("trashservice", "current trashes: " + currentTrashes);

        CollectionUtils.subtract(currentTrashes, newTrashes).forEach(
            trash -> {
                Log.e("trashservice", "remove trash: " + trash);
                this.setTrashState(trash, TrashState.PICKED_UP);
                this.removedListeners.forEach(
                    listener -> listener.trashRemoved(trash)
                );
            }
        );

        Log.e("trashservice", "new trashes: " + CollectionUtils.subtract(newTrashes, currentTrashes));

        CollectionUtils.subtract(newTrashes, currentTrashes).forEach(
            trash -> Log.e("trashservicec", "a trash")
        );

        Log.e("trashservice", "new trash length: " + CollectionUtils.subtract(newTrashes, currentTrashes).size());

        CollectionUtils.subtract(newTrashes, currentTrashes).forEach(
            trash -> {
                Log.e("trashservice", "add trash: " + trash);
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
        if(documentSnapshots == null || this.authProvider.getUser() == null) {
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

    public CompletableFuture<Set<Trash>> reset() {
        this.trashes = null;
        return this.trashes();
    }

}
