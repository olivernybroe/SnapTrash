package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.FirebaseUserService;
import lombok.Getter;

public class FirebaseAuthProvider implements AuthProvider {

    @Getter private FirebaseAuth firebaseAuth;

    public FirebaseAuthProvider() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public User getUser() {
        return FirebaseUserService.toUser(
            this.firebaseAuth.getCurrentUser()
        );
    }

    @Override
    public boolean loggedIn() {
        return this.firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public CompletableFuture<Void> signOut() {
        this.firebaseAuth.signOut();
        return CompletableFuture.completedFuture(null);
    }

}
