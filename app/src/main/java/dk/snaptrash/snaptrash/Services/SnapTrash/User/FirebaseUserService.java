package dk.snaptrash.snaptrash.Services.User;

import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.Auth.FirebaseAuthProvider;

public class FirebaseUserService implements UserService {

    public FirebaseUserService(FirebaseAuthProvider authProvider) {

    }

    @Override
    public User get(String id) {
        return null;
    }
}
