package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;

public class TrashMapMap {

    private BiMap<Trash, Marker> biMap = HashBiMap.create();

    private Activity activity;
    private GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private TrashService trashService;

    public TrashMapMap(Activity activity, TrashService trashService, GoogleMap map, Drawable drawable) {
        super();
        this.activity = activity;
        this.trashService = trashService;
        this.googleMap = map;
        this.markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapFromSvg(drawable)));

        trashService.addOnTrashAddedListener(this::put);
        trashService.addOnTrashRemovedListener(this::remove);
        trashService.addOnTrashPickedUpListener(this::remove);
        trashService.addOnPickUpRejectedListener(this::put);

        trashService.availableTrashes().thenAcceptAsync(
            trashes -> trashes.forEach(this::put)
        );
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

    private void put(Trash trash) {
        this.activity.runOnUiThread(
            () -> this.biMap.computeIfAbsent(
                trash,
                _trash -> this.googleMap.addMarker(
                    markerOptions.position(
                        Geo.toLatLng(_trash.getLocation())
                    )
                )
            )
        );
    }

    public Trash getTrash(Marker marker) {
        return this.biMap.inverse().get(marker);
    }

    public Marker getMarker(Trash trash) {
        return this.biMap.get(trash);
    }

    private void remove(Trash key) {
        this.activity.runOnUiThread(
            () -> {
                Marker marker = this.biMap.remove(key);
                if (marker != null) {
                    marker.remove();
                }
            }
        );
    }


}
