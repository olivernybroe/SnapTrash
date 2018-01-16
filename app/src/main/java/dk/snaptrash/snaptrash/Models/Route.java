package dk.snaptrash.snaptrash.Models;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import dk.snaptrash.snaptrash.Utils.Geo.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public class Route extends Model<Route> {

    @NonNull@Getter
    String Id;
    @NonNull@Getter
    Collection<Trash> trashes;
    @NonNull@Getter
    String userId;

    @Nullable
    private transient Direction direction;

    public Route(String id, Collection<Trash> trashes, String userId) {
        Id = id;
        this.trashes = trashes;
        this.userId = userId;
    }

    public List<LatLng> getLatLngs() {
        return trashes.stream().map(Trash::toLatLng).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return Id.equals(route.Id);
    }

    @Override
    public int hashCode() {
        return Id.hashCode();
    }
}
