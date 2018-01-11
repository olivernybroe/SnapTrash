package dk.snaptrash.snaptrash.Services.SnapTrash.User;

import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface UserService {

    CompletableFuture<User> get(String id);

}
