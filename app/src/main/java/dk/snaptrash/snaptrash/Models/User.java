package dk.snaptrash.snaptrash.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class User extends Model<User> {

    @Setter @Getter private String email;
    @Setter @Getter private String username;
    @Setter @Getter private String avatar;

}
