package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.Trash;

@Singleton
public interface TrashService {

    public enum TrashState {
        FREE,
        RESERVED,
        PENDING_PICK_UP_CONFIRMED,
        PICKED_UP
    }

    public interface OnTrashAddedListener {
        public void trashAdded(Trash trash);
    }

    public interface OnTrashRemovedListener {
        public void trashRemoved(Trash trash);
    }

    public interface OnTrashPickedUpListener {
        public void pickedUp(Trash trash);
    }

    public interface OnPickUpVerifiedListener {
        public void pickUpVerified(Trash trash);
    }

    public interface OnPickUpRejectedListener {
        public void pickUpRejected(Trash trash);
    }

    public interface OnTrashStatusChangedListener {
        public void trashStatusChanged(Trash trash, TrashState state);
    }

    @NonNull
    CompletableFuture<Set<Trash>> trashes();

    @NonNull
    CompletableFuture<Set<Trash>> availableTrashes();

    @NonNull
    CompletableFuture<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo);

    void setTrashState(Trash trash, TrashState status);

    TrashState getTrashState(Trash trash);

    @NonNull
    CompletableFuture<Boolean> trashCanBePickedUp(@NonNull Trash trash);

    public void addOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);
    public void removeOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);

    public void addOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);
    public void removedOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);

    public void addOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener);
    public void removedOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener);

    public void addOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);
    public void removeOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);

    public void addOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener);
    public void removeOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener);

    public void addOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener);
    public void removeOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener);


}
