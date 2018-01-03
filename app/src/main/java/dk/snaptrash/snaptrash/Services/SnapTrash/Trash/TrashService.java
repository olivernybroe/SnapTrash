package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Collection;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.Trash;

@Singleton
public interface TrashService {
    Collection<Trash> closeTo(LatLng location) throws TrashException;
    void pickUp(Trash trash, File pickUpVideo) throws TrashException;

}
