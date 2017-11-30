package dk.snaptrash.snaptrash.Services.Auth;

import android.accounts.AuthenticatorException;
import android.support.annotation.NonNull;
import android.util.Log;

import dk.snaptrash.snaptrash.Models.User;

public class FakeAuthProvider implements AuthProvider {
    private User user;
    private String password;

    @Override
    public User login(String username, String password) throws AuthenticationException {
        this.user = new User(username, "avatar");
        return this.user;
    }

    @Override
    public User login() throws AuthenticatorException {
        return this.user();
        //return this.user = new User("Saved user", "avatar");
    }

    @NonNull
    @Override
    public AuthProvider logout() throws AuthenticationException {
        this.user = null;
        this.password = null;
        return this;
    }

    @Override
    public User user() throws AuthenticatorException {
        if(!isLoggedIn()) {
            throw new AuthenticatorException();
        }

        return this.user;
    }

    @Override
    public boolean isLoggedIn() {
        return this.user != null;
    }

}
