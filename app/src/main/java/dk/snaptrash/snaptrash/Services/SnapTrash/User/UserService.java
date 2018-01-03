package dk.snaptrash.snaptrash.Services.SnapTrash.User;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface UserService {
    User get(String id);
}
