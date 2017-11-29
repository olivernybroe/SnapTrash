package dk.snaptrash.snaptrash.Services.Trash;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.Services.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.Auth.FakeAuthProvider;

@Module
public abstract class TrashModule {
    @Singleton
    @Provides
    static TrashService provideTrashService() {
        return new FakeTrashService();
    }
}
