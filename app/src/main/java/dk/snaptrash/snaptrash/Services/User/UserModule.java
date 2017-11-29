package dk.snaptrash.snaptrash.Services.User;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.Services.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.Auth.FakeAuthProvider;

@Module
public abstract class UserModule {
    @Singleton
    @Provides
    static UserService priveUserService() {
        return new FakeUserService();
    }
}

