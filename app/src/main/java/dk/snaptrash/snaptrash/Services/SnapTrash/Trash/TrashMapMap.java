package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;
import dk.snaptrash.snaptrash.Utils.Graphic;

public class TrashMapMap {

    private BiMap<Trash, Marker> biMap = HashBiMap.create();

    private Activity activity;
    private GoogleMap googleMap;
    private TrashService trashService;

    private MarkerOptions unselectedMarkerOptions;
    private MarkerOptions selectedMarkerOptions;
    private BitmapDescriptor unselectedBitmap;
    private BitmapDescriptor selectedBitmap;

    public TrashMapMap(Activity activity, TrashService trashService, GoogleMap map) {
        super();
        this.activity = activity;
        this.trashService = trashService;
        this.googleMap = map;

        this.unselectedBitmap = BitmapDescriptorFactory.fromBitmap(
            Graphic.bitmapFromSvg(
                this.activity.getDrawable(
                    R.drawable.trash_icon
                )
            )
        );

        this.selectedBitmap = BitmapDescriptorFactory.fromBitmap(
            Graphic.bitmapFromSvg(
                this.activity.getDrawable(
                    R.drawable.trash_icon_green
                )
            )
        );

        this.unselectedMarkerOptions = new MarkerOptions()
            .icon(
                this.unselectedBitmap
            );
        this.selectedMarkerOptions = new MarkerOptions()
            .icon(
                this.selectedBitmap
            );

        trashService.addOnTrashAddedListener(this::put);
        trashService.addOnTrashRemovedListener(this::remove);
        trashService.addOnTrashPickedUpListener(this::remove);
        trashService.addOnPickUpRejectedListener(this::put);
        trashService.addOnTrashStatusChangedListener(
            (trash, state) -> {
                if (this.biMap.containsKey(trash)) {
                    Log.e("trashmapmap", "status updated on: " + trash + " to: " + state);
                    this.activity.runOnUiThread(
                        () -> {
                            if (state == TrashService.TrashState.RESERVED) {
                                this.getMarker(trash).setIcon(this.selectedBitmap);
                            } else {
                                this.getMarker(trash).setIcon(this.unselectedBitmap);
                            }
                        }
                    );
                }
            }
        );

        trashService.availableTrashes().thenAcceptAsync(
            trashes -> trashes.forEach(this::put)
        );
    }

    private void put(Trash trash) {
        this.activity.runOnUiThread(
            () -> this.biMap.computeIfAbsent(
                trash,
                _trash -> {
                    Log.e("trashmapmap", "putting trash: " + trash + " state: " + this.trashService.getTrashState(trash));
                    return this.googleMap.addMarker(
                        this.trashService.getTrashState(trash) == TrashService.TrashState.RESERVED
                            ? this.selectedMarkerOptions.position(
                            Geo.toLatLng(_trash.getLocation())
                        )
                            : this.unselectedMarkerOptions.position(
                            Geo.toLatLng(_trash.getLocation())
                        )
                    );
                }
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
