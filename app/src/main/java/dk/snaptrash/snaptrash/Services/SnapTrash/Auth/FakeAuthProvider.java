package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;

import dk.snaptrash.snaptrash.Models.User;

public class FakeAuthProvider implements AuthProvider {
    @Override
    public AuthProvider login(String username, String password) {
        return this;
    }

    @Override
    public AuthProvider login(GoogleSignInAccount account) {
        return this;
    }

    @Override
    public AuthProvider login() {
        return this;
    }

    @NonNull
    @Override
    public AuthProvider logout() {
        return this;
    }

    @Nullable
    @Override
    public User user() {
        return new User("CE@lugter.dk", "Sammy er tyk", "XD");
    }

    @Override
    public AuthProvider addOnLoginListener(OnLoginListener completeListener) {
        return null;
    }

    @Override
    public AuthProvider removeOnLoginListener(OnLoginListener completeListener) {
        return null;
    }

    @Override
    public AuthProvider addOnLogoutListener(OnLogoutListener completeListener) {
        return null;
    }

}
