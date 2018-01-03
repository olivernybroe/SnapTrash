package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface AuthProvider {

    AuthProvider login(String username, String password);
    AuthProvider login(GoogleSignInAccount account);
    AuthProvider login();

    @NonNull AuthProvider logout();

    @Nullable User user();

    AuthProvider addOnLoginListener(OnLoginListener completeListener);

    AuthProvider removeOnLoginListener(OnLoginListener completeListener);

    AuthProvider addOnLogoutListener(OnLogoutListener completeListener);
}
