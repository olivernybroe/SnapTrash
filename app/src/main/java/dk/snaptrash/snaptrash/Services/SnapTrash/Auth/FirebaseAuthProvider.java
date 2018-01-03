package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dk.snaptrash.snaptrash.Models.User;

public class FirebaseAuthProvider implements AuthProvider, FirebaseAuth.AuthStateListener {

    private List<UserInvalidatedListener> userInvalidatedListeners = Collections.synchronizedList(
        new ArrayList<>()
    );

    private FirebaseAuth auth;

    public FirebaseAuthProvider() {
        this.auth = FirebaseAuth.getInstance();
        this.auth.addAuthStateListener(this);
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
//        FirebaseUser user = auth.getCurrentUser();
//        if (user == null) {
//            return Tasks.forException(new Exception());
//        } else {
//            return Tasks.forResult(FirebaseAuthProvider.toUser(user));
//        }
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

    @Override
    public void addUserInvalidatedListener(
            @NonNull UserInvalidatedListener userInvalidatedListener
    ) {
        this.userInvalidatedListeners.add(userInvalidatedListener);
    }

    private static User toUser(@NonNull FirebaseUser user) {
        return new User(user.getEmail(), user.getDisplayName(), "photo");
    }

    private static User toUser(@NonNull AuthResult result) {
        return FirebaseAuthProvider.toUser(result.getUser());
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.e("auth state changed", firebaseAuth.getCurrentUser() == null ? "null" : firebaseAuth.getCurrentUser().getEmail());
        if (firebaseAuth.getCurrentUser() == null) {
            this.userInvalidatedListeners.forEach(UserInvalidatedListener::userInvalidated);
        }
    }
}
