package dk.snaptrash.snaptrash.Services.Trash;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dk.snaptrash.snaptrash.Models.Trash;

public class FakeTrashService implements TrashService {

    @Override
    public Collection<Trash> closeTo(LatLng location) throws TrashException {
        return IntStream.range(-100, 100)
                .mapToObj( i -> new Trash("id_"+i, new LatLng(location.latitude+(i/10000.0), location.longitude), null, "Din mor", "MyAuthorId"))
                .collect(Collectors.toList());
    }


    @Override
    public void pickUp(Trash trash, File pickUpVideo) throws TrashException {

    }
}
