package dk.snaptrash.snaptrash.Services.Trash;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Services.Auth.AuthProvider;

public class FakeTrashService implements TrashService {

    @Override
    public Collection<Trash> closeTo(LatLng location) throws TrashException {
        return Arrays.asList(
                new Trash(new LatLng(55.7308, 12.3996), null, "Din mor", "MyAuthorId"),
                new Trash(new LatLng(55.7303, 12.3996), null, "Din far", "MyAuthorId")
        );
    }


    @Override
    public void pickUp(Trash trash, File pickUpVideo) throws TrashException {

    }
}
