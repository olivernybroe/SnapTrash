package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.Trash;

@Singleton
public interface TrashService {

    public interface OnTrashAddedListener {
        public void trashAdded(Trash trash);
    }

    public interface OnTrashRemovedListener {
        public void trashRemoved(Trash trash);
    }

    public interface OnPickUpVerifiedListener {
        public void pickUpVerified(Trash trash);
    }

    @NonNull
    CompletableFuture<Collection<Trash>> trashes();

    @NonNull
    CompletableFuture<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo);

    CompletableFuture<Boolean> trashCanBePickedUp(@NonNull Trash trash);

    public void addOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);
    public void removeOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);

    public void addOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);
    public void removedOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);

    public void addOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);
    public void removeOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);

}
