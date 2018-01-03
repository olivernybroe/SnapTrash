package dk.snaptrash.snaptrash.Services;

import javax.inject.Singleton;

import dagger.Provides;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.FirebaseAuthProvider;

public class FirebaseModule{

    @Singleton
    @Provides
    static AuthProvider provideAuthProvider() {
        return new FirebaseAuthProvider();
    }
}
