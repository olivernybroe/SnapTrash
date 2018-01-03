package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import dk.snaptrash.snaptrash.Models.Trash;

public class TrashMapMap extends HashMap<Trash, Marker> {
    private GoogleMap map;
    private MarkerOptions markerOptions;

    public TrashMapMap(GoogleMap map, MarkerOptions markerOptions, Iterable<Trash> trashes) {
        super();
        this.map = map;
        this.markerOptions = markerOptions;

        this.put(trashes);

    }

    public Marker put(Trash trash) {
        return super.put(
                trash,
                this.map.addMarker(markerOptions.position(trash.getLocation()))
        );
    }

    public TrashMapMap put(Iterable<Trash> trashes) {
        trashes.forEach(this::put);
        return this;
    }

    @Override
    public Marker put(Trash value, Marker key) {
        throw new RuntimeException("Not supported");
    }

    public Trash remove(Marker marker) {
        return this.entrySet().stream()
                .filter(markerTrashEntry -> markerTrashEntry.getValue().equals(marker))
                .findAny()
                .map(trashMarkerEntry -> {
                    this.remove(trashMarkerEntry.getKey());
                    return trashMarkerEntry.getKey();
                })
                .orElse(null);
    }

    public Marker remove(Trash trash) {
        Marker marker = super.remove(trash);
        if(marker == null) {
            return null;
        }
        marker.remove();
        return marker;
    }

    @Override
    public Marker remove(Object key) {
        throw new RuntimeException("Not supported");
    }
}
