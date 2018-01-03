package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Optional;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface AuthProvider {

    @NonNull Task<User> login(String email, String password);
    @NonNull Task<User> login(GoogleSignInAccount account);
    @NonNull Task<User> login();

    @NonNull Task<Void> logout();

    @Nullable User user();

}
