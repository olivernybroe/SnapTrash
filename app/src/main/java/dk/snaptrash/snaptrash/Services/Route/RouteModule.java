package dk.snaptrash.snaptrash.Services.Route;

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