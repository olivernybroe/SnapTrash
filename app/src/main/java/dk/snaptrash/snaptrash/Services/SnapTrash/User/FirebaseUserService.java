package dk.snaptrash.snaptrash.Services.SnapTrash.User;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import dk.snaptrash.snaptrash.Models.User;

public class FirebaseUserService implements UserService {

    @Override
    public User get(String id) {
        return null;
    }

    @Nullable public static User toUser(@Nullable FirebaseUser user) {
        return user == null ?
            null
            : new User(user.getEmail(), user.getDisplayName(), "photo");
    }

    @Nullable public static User toUser(@NonNull AuthResult result) {
        return FirebaseUserService.toUser(result.getUser());
    }
}
