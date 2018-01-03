package dk.snaptrash.snaptrash.Services.SnapTrash.Route;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class RouteModule {
    @Singleton
    @Provides
    static RouteService provideRouteService() {
        return new FakeRouteService();
    }
}