package dk.snaptrash.snaptrash.Models;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Route extends Model<Route> {
    @Getter String Id;
    @Getter Collection<Trash> trashes;
}
