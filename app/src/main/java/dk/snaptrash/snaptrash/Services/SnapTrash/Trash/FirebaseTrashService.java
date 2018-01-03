package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.Collection;

import dk.snaptrash.snaptrash.Models.Trash;

public class FirebaseTrashService implements TrashService {
    @NonNull
    @Override
    public Task<Collection<Trash>> closeTo(@NonNull LatLng location) {
        return FirebaseFirestore.getInstance().collection("trashes")
                .get().continueWith(task -> task.getResult().toObjects(Trash.class));
    }

    GeoPoint toGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    @NonNull
    @Override
    public Task<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo) {
        return null;
    }
}
