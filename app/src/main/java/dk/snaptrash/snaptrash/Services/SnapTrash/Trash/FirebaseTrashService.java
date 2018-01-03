package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dk.snaptrash.snaptrash.Models.Trash;

public class FirebaseTrashService implements TrashService, EventListener<QuerySnapshot> {
    private List<EventListener<Collection<Trash>>> eventListeners = Collections.synchronizedList(new ArrayList<>());

    public FirebaseTrashService() {
        FirebaseFirestore.getInstance().collection("trashes").addSnapshotListener(this);
    }

    @NonNull
    @Override
    public Task<Collection<Trash>> closeTo(@NonNull LatLng location) {
        return FirebaseFirestore.getInstance().collection("trashes")
            .get().continueWith(task ->
                task.getResult().getDocuments().stream()
                    .map(this::toTrash)
                    .collect(Collectors.toList())
        );
    }

    @NonNull
    public FirebaseTrashService addTrashChangeListener(EventListener<Collection<Trash>> eventListener) {
        this.eventListeners.add(eventListener);
        return this;
    }

    @NonNull
    private Trash toTrash(DocumentSnapshot documentSnapshot) {
        return new Trash(
                documentSnapshot.getId(),
                this.toLatLng(documentSnapshot.getGeoPoint("location")),
                null,
                documentSnapshot.getString("description"),
                documentSnapshot.getString("authorId")
        );
    }

    GeoPoint toGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    LatLng toLatLng(GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    @NonNull
    @Override
    public Task<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo) {
        return null;
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        Log.e("TRASH", "TRASH CHANGE!");
        eventListeners.forEach(eventListener -> eventListener.onEvent(
                documentSnapshots.getDocuments().stream().map(this::toTrash).collect(Collectors.toList()),
                e
        ));
    }
}
