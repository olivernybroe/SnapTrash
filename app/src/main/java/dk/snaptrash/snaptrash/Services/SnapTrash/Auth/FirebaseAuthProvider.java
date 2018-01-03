package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dk.snaptrash.snaptrash.Models.User;

public class FirebaseAuthProvider implements AuthProvider {

    private FirebaseAuth auth;

    public FirebaseAuthProvider() {
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Task<User> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password).continueWith(
            task -> FirebaseAuthProvider.toUser(task.getResult())
        );

    }

    @NonNull
    @Override
    public Task<User> login(GoogleSignInAccount account) {
        return null;
    }

    @NonNull
    @Override
    public Task<User> login() {
        return Tasks.forException(new Exception());
    }

    @NonNull
    @Override
    public Task<Void> logout() {
        return Tasks.call(() -> {
            auth.signOut();
            return null;
        });
    }

    @Nullable
    @Override
    public User user() {
        return auth.getCurrentUser() != null ? toUser(auth.getCurrentUser()) : null;
    }

    private static User toUser(@NonNull FirebaseUser user) {
        return new User(user.getEmail(), user.getDisplayName(), "photo");
    }

    private static User toUser(@NonNull AuthResult result) {
        return FirebaseAuthProvider.toUser(result.getUser());
    }

}
