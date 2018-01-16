package dk.snaptrash.snaptrash.Services.SnapTrash;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.FirebaseAuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.FirebaseRouteService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.FirebaseTrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.FirebaseUserService;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;


@Module()
public class FirebaseSnapTrashModule implements SnapTrashModule {

    @Singleton
    @Provides
    public AuthProvider provideAuthProvider() {
        return new FirebaseAuthProvider();
    }

    @Singleton
    @Provides
    public UserService provideUserService() {
        return new FirebaseUserService();
    }

    @Singleton
    @Provides
    public TrashService provideTrashService(Context context) {
        return new FirebaseTrashService(context);
    }

    @Singleton
    @Provides
    public RouteService provideRouteService(AuthProvider authProvider, TrashService trashService) {
        return new FirebaseRouteService(authProvider, (FirebaseTrashService) trashService);
    }
}
