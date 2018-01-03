package dk.snaptrash.snaptrash.Services.SnapTrash;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.FakeAuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.FakeRouteService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.FakeTrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.FirebaseUserService;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;

@Module
public class FirebaseSnapTrashModule{

    @Singleton
    @Provides
    public AuthProvider provideAuthProvider() {
        //return new FirebaseAuthProvider();
        return new FakeAuthProvider();
    }

    @Singleton
    @Provides
    public UserService provideUserService() {
        return new FirebaseUserService();
    }

    @Singleton
    @Provides
    public TrashService provideTrashService() {
        return new FakeTrashService();
    }

    @Singleton
    @Provides
    public RouteService provideRouteService() {
        return new FakeRouteService();
    }
}
