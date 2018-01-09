package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

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

    @NonNull
    CompletableFuture<Collection<Trash>> closeTo(@NonNull LatLng location);
    @NonNull CompletableFuture<Trash> pickUp(@NonNull Trash trash,@NonNull File pickUpVideo);

    @NonNull
    FirebaseTrashService addTrashChangeListener(EventListener<Collection<Trash>> eventListener);

}
