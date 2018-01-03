package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.snaptrash.snaptrash.Models.User;

public class FirebaseAuthProvider implements AuthProvider {
    private List<OnLoginListener> loginListeners = Collections.synchronizedList(new ArrayList<>());
    private List<OnLogoutListener> logoutListeners = Collections.synchronizedList(new ArrayList<>());


    private FirebaseAuth auth;

    public FirebaseAuthProvider() {
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public AuthProvider login(String username, String password) {
        return null;
    }

    @Override
    public AuthProvider login(GoogleSignInAccount account) {
        return null;
    }

    @Override
    public AuthProvider login() {
        return null;
    }

    @NonNull
    @Override
    public AuthProvider logout() {
        return null;
    }

    @Nullable
    @Override
    public User user() {
        return auth.getCurrentUser() != null ? toUser(auth.getCurrentUser()) : null;
    }

    @Override
    public AuthProvider addOnLoginListener(OnLoginListener completeListener) {
        this.loginListeners.add(completeListener);
        return this;
    }

    @Override
    public AuthProvider removeOnLoginListener(OnLoginListener completeListener) {
        this.loginListeners.remove(completeListener);
        return this;
    }

    @Override
    public AuthProvider addOnLogoutListener(OnLogoutListener completeListener) {
        this.logoutListeners.add(completeListener);
        return this;
    }

    private static User toUser(@NonNull FirebaseUser user) {
        return new User(user.getEmail(), user.getDisplayName(), "photo");
    }

}
