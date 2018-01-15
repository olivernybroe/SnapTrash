package dk.snaptrash.snaptrash.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class User extends Model<User> {

    @NonNull @Getter
    private String id;
    @NonNull @Getter
    private String email;
    @Setter @Getter
    private String username;
    @NonNull @Setter @Getter
    private String avatarUrl;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
