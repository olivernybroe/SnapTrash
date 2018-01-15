package dk.snaptrash.snaptrash.DependencyInjections;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.SnapTrashApplication;

@Module
public class AppModule {

    @Provides
    Context provideContext(SnapTrashApplication application) {
        return application.getApplicationContext();
    }

}