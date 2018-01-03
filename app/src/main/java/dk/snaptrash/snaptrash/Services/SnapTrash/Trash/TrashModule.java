package dk.snaptrash.snaptrash.Services.SnapTrash.Trash;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class TrashModule {
    @Singleton
    @Provides
    static TrashService provideTrashService() {
        return new FakeTrashService();
    }
}
