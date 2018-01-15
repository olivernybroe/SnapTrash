package dk.snaptrash.snaptrash.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Route extends Model<Route> {

    @Getter String Id;
    @Getter Collection<Trash> trashes;

    public List<LatLng> getLatLngs() {
        return trashes.stream().map(Trash::toLatLng).collect(Collectors.toList());
    }
}
