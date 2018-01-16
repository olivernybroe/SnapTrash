package dk.snaptrash.snaptrash.Services.SnapTrash.User;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Utils.TaskWrapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseUserService implements UserService {

    private OkHttpClient client = new OkHttpClient();

    private HttpUrl.Builder urlBuilder(String userId) {
        return new HttpUrl.Builder()
            .host("us-central1-snaptrash-1507812289113.cloudfunctions.net")
            .scheme("https")
            .addPathSegments("snaptrash/users/")
            .addPathSegment(userId);
    }

    @Override
    public CompletableFuture<Optional<User>> get(String id) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                .url(urlBuilder(id).build())
                .build();

            try {
                Response response = client.newCall(request).execute();
                return toUser(new JSONObject(response.body().string()));

            } catch (IOException |JSONException e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<User> create(String name, String email, String password, Uri profilePic) {
        return TaskWrapper.wrapAsync(FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password))
            .thenApplyAsync(authResult -> {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(profilePic)
                    .build();

                try {
                    TaskWrapper.wrapAsync(authResult.getUser().updateProfile(profileUpdates)).get();
                } catch (InterruptedException|ExecutionException e) {
                   throw new CompletionException(e);
                }
                return authResult;
            })
            .thenApply(FirebaseUserService::toUser);
    }

    private static Optional<User> toUser(JSONObject object) {
        if(object == null) {
            return Optional.empty();
        }
        try {
            JSONObject data = object.getJSONObject("data");

            return Optional.of(new User(
                object.getString("id"),
                data.getString("email"),
                data.optString("displayName", null),
                data.getString("pictureUrl")
            ));
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    @Nullable public static User toUser(@Nullable FirebaseUser user) {
        return user == null ?
            null
            : new User(
                user.getUid(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "https://firebasestorage.googleapis.com/v0/b/snaptrash-1507812289113.appspot.com/o/IMG_20180111_133329.jpg?alt=media&token=a297c769-03e4-4a95-a8ec-2c92bb2803fc"
             );
    }

    @Nullable public static User toUser(@NonNull AuthResult result) {
        return FirebaseUserService.toUser(result.getUser());
    }

}
