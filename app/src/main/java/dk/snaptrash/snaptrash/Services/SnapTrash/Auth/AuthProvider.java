package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface AuthProvider {

    @Nullable User getUser();

    boolean loggedIn();

    CompletableFuture<Void> signOut();

}
