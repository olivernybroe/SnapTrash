package dk.snaptrash.snaptrash.Services.SnapTrash.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Singleton;

import dk.snaptrash.snaptrash.Models.User;

@Singleton
public interface UserService {

    CompletableFuture<Optional<User>> get(String id);

}
