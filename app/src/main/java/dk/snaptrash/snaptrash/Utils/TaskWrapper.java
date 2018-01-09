package dk.snaptrash.snaptrash.Utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class TaskWrapper {

    public static <T> CompletableFuture<T> wrapAsync (Task<T> task) {
        return CompletableFuture.supplyAsync(
            () -> {
                try {
                    return Tasks.await(task);
                } catch (ExecutionException | InterruptedException e) {
                    throw new CompletionException(e);
                }
            }
        );
    }

    public static <T> CompletableFuture<T> anyOff(CompletableFuture<T>... completableFutures) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(completableFutures);
    }

}
