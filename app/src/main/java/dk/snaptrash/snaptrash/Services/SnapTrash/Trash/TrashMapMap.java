package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import dk.snaptrash.snaptrash.Models.Trash;

public class TrashMapMap extends HashMap<Trash, Marker> {
    private GoogleMap map;
    private MarkerOptions markerOptions;
    private TrashService trashService;


    public TrashMapMap(TrashService trashService, GoogleMap map, Drawable drawable) {
        super();
        this.trashService = trashService;
        this.map = map;
        this.markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapFromSvg(drawable)));

        trashService.addTrashChangeListener((trashes, e) -> this.put(trashes));
    }

    private Bitmap bitmapFromSvg(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getMinimumHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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

    public Trash getTrash(Marker marker) {
        return this.entrySet().stream()
                .filter(markerTrashEntry -> markerTrashEntry.getValue().equals(marker))
                .findAny()
                .map(Entry::getKey)
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
