package dk.snaptrash.snaptrash.Services.SnapTrash.User;

import android.net.Uri;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface UserService {

    interface OnUserLoggedInListener {
        void userLoggedIn(User user);
    }

    CompletableFuture<Optional<User>> get(String id);

    CompletableFuture<User> create(String name, String email, String password, Uri profilePic);

    void addOnUserLoggedInListener(OnUserLoggedInListener onUserLoggedInListener);
    void removeOnUserLoggedInListener(OnUserLoggedInListener onUserLoggedInListener);

}
