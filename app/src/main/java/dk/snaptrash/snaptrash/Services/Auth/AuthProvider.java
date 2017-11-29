package dk.snaptrash.snaptrash.Services.Auth;

import android.accounts.AuthenticatorException;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface AuthProvider {

    User login(String username, String password) throws AuthenticationException;

    @NonNull AuthProvider logout() throws AuthenticationException;

    User user() throws AuthenticatorException;

    boolean isLoggedIn();
}
