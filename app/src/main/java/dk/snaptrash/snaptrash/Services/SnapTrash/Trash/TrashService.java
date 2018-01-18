package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.Trash;

@Singleton
public interface TrashService {

    enum TrashState {
        FREE,
        RESERVED,
        PENDING_PICK_UP_CONFIRMED,
        PICKED_UP
    }

    interface OnTrashAddedListener {
        void trashAdded(Trash trash);
    }

    interface OnTrashRemovedListener {
        void trashRemoved(Trash trash);
    }

    interface OnTrashPickedUpListener {
        void pickedUp(Trash trash);
    }

    interface OnPickUpVerifiedListener {
        void pickUpVerified(Trash trash);
    }

    interface OnPickUpRejectedListener {
        void pickUpRejected(Trash trash);
    }

    interface OnTrashStatusChangedListener {
        void trashStatusChanged(Trash trash, TrashState state);
    }

    @NonNull
    CompletableFuture<Set<Trash>> trashes();

    @NonNull
    CompletableFuture<Set<Trash>> availableTrashes();

    @NonNull
    CompletableFuture<Void> pickUp(@NonNull Trash trash, @NonNull File pickUpVideo);

    void setTrashState(Trash trash, TrashState status);

    TrashState getTrashState(Trash trash);

    @NonNull CompletableFuture<Boolean> trashCanBePickedUp(@NonNull Trash trash);

    void addOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);
    void removeOnTrashAddedListener(OnTrashAddedListener onTrashAddedListener);

    void addOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);
    void removedOnTrashRemovedListener(OnTrashRemovedListener onTrashRemovedListener);

    void addOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener);
    void removedOnTrashPickedUpListener(OnTrashPickedUpListener onTrashPickedUpListener);

    void addOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);
    void removeOnPickUpVerifiedListener(OnPickUpVerifiedListener onPickUpVerifiedListener);

    void addOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener);
    void removeOnPickUpRejectedListener(OnPickUpRejectedListener onPickUpRejectedListener);

    void addOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener);
    void removeOnTrashStatusChangedListener(OnTrashStatusChangedListener onTrashStatusChangedListener);

}
