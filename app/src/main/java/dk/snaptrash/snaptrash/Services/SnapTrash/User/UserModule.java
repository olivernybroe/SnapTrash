package dk.snaptrash.snaptrash.Services.SnapTrash.User;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.FirebaseAuthProvider;

@Module
public abstract class UserModule {
    @Singleton
    @Provides
    static UserService provideUserService(FirebaseAuthProvider authProvider) {
        return new FirebaseUserService(authProvider);
    }
}

