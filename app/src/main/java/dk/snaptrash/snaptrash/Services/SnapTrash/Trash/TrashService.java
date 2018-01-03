package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.Collection;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.Trash;

@Singleton
public interface TrashService {

    @NonNull Task<Collection<Trash>> closeTo(@NonNull LatLng location);
    @NonNull Task<Void> pickUp(@NonNull Trash trash,@NonNull File pickUpVideo);

}
