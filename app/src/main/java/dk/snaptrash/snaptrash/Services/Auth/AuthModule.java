package dk.snaptrash.snaptrash.Services.Auth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthModule {
    @Singleton @Provides static AuthProvider provideAuthProvider() {
        return new FakeAuthProvider();
    }
}
