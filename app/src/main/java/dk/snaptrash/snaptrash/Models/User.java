package dk.snaptrash.snaptrash.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class User extends Model {

    @Getter @Setter private String username;
    @Getter @Setter private String avatar;
}
