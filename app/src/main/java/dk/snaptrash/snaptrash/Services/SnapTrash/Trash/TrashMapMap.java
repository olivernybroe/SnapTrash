package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Utils.Geo;

public class TrashMapMap implements BiMap<Trash, Marker> {

    private BiMap<Trash, Marker> biMap = HashBiMap.create();

    private GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private TrashService trashService;

    public TrashMapMap(TrashService trashService, GoogleMap map, Drawable drawable) {
        super();
        this.trashService = trashService;
        this.googleMap = map;
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
        return this.biMap.computeIfAbsent(
            trash,
            _trash -> this.googleMap.addMarker(
                markerOptions.position(
                    Geo.toLatLng(_trash.getLocation())
                )
            )
        );
    }

    public TrashMapMap put(Iterable<Trash> trashes) {
        trashes.forEach(this::put);
        return this;
    }

    public Trash getTrash(Marker marker) {
        return this.biMap.inverse().get(marker);
    }

    public Marker getMarker(Trash trash) {
        return this.biMap.get(trash);
    }

    @Deprecated
    @Override
    public boolean replace(Trash key, Marker oldValue, Marker newValue) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker replace(Trash key, Marker value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker computeIfAbsent(Trash key, Function<? super Trash, ? extends Marker> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker computeIfPresent(Trash key, BiFunction<? super Trash, ? super Marker, ? extends Marker> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker compute(Trash key, BiFunction<? super Trash, ? super Marker, ? extends Marker> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker merge(Trash key, Marker value, BiFunction<? super Marker, ? super Marker, ? extends Marker> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.biMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.biMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.biMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.biMap.containsValue(value);
    }

    @Override
    public Marker get(Object key) {
        return this.biMap.get(key);
    }

    @Deprecated
    @Override
    public Marker put(Trash key, Marker value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Marker remove(Object key) {
        return null;
    }

    @Deprecated
    @Override
    public Marker forcePut(Trash key, Marker value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void putAll(Map<? extends Trash, ? extends Marker> map) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Set<Trash> keySet() {
        return this.biMap.keySet();
    }

    @Override
    public Set<Marker> values() {
        return this.biMap.values();
    }

    @NonNull
    @Override
    public Set<Entry<Trash, Marker>> entrySet() {
        return this.biMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return this.biMap.hashCode();
    }

    @Override
    public Marker getOrDefault(Object key, Marker defaultValue) {
        return this.biMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super Trash, ? super Marker> action) {
        this.biMap.forEach(action);
    }

    @Deprecated
    @Override
    public void replaceAll(BiFunction<? super Trash, ? super Marker, ? extends Marker> function) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Marker putIfAbsent(Trash key, Marker value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public BiMap<Marker, Trash> inverse() {
        throw new UnsupportedOperationException();
    }

}
