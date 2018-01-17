package dk.snaptrash.snaptrash.Services.SnapTrash;

import android.content.Context;

import dagger.Module;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.Route.RouteService;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;

@Module
public interface SnapTrashModule {

    AuthProvider provideAuthProvider();

    UserService provideUserService();

    TrashService provideTrashService(Context context, AuthProvider authProvider, UserService userService);

    RouteService provideRouteService(AuthProvider authProvider, TrashService trashService);
}
