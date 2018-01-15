package dk.snaptrash.snaptrash.Services.SnapTrash.User;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.DependencyInjections.DaggerAppComponent;
import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;

public class FirebaseUserService implements UserService {

    @Override
    public CompletableFuture<User> get(String id) {
        Log.e("userservice", "get user " + id);
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                return new User("xd@dinmor.dk", "lol", ":)");
            }
        );
    }

    @Nullable public static User toUser(@Nullable FirebaseUser user) {
        return user == null ?
            null
            : new User(
                user.getEmail(),
                user.getDisplayName(),
                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "https://firebasestorage.googleapis.com/v0/b/snaptrash-1507812289113.appspot.com/o/IMG_20180111_133329.jpg?alt=media&token=a297c769-03e4-4a95-a8ec-2c92bb2803fc"
             );
    }

    @Nullable public static User toUser(@NonNull AuthResult result) {
        return FirebaseUserService.toUser(result.getUser());
    }

}
