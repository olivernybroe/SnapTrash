package dk.snaptrash.snaptrash.login;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.FirebaseUserService;
import dk.snaptrash.snaptrash.Utils.TaskWrapper;

public abstract class AuthenticationActivity extends AppCompatActivity {

    private static final int C_SAVE = 0;

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient credentialsApiClient;

    private interface OnApiClientConnectedListener {
        void onConnected(@Nullable Bundle bundle);
    }

    private interface OnCredentialsChosenListener {
        void onCredentialsChosen(
            @Nullable Credential credentials,
            int resultCode
        );
    }

    private Set<OnApiClientConnectedListener> onApiClientConnectedListeners
        = Collections.synchronizedSet(
            Collections.newSetFromMap(
                new WeakHashMap<>()
            )
    );

    private Set<OnCredentialsChosenListener> onCredentialsChosenListeners
        = Collections.synchronizedSet(
            Collections.newSetFromMap(
                new WeakHashMap<>()
            )
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.firebaseAuth = FirebaseAuth.getInstance();

        this.connectToGoogleApi();

    }

    protected CompletableFuture<GoogleApiClient> getGoogleApiClient() {
        if (this.credentialsApiClient.isConnected()) {
            return CompletableFuture.completedFuture(this.credentialsApiClient);
        } else {
            CompletableFuture<GoogleApiClient> future = new CompletableFuture<>();
            this.onApiClientConnectedListeners.add(
                bundle -> future.complete(this.credentialsApiClient)
            );
            return future;
        }
    }

    protected CompletableFuture<GoogleApiClient> getGoogleApiClient(long timeout) {
        return TaskWrapper.anyOff(
            this.getGoogleApiClient(),
            CompletableFuture.supplyAsync(
                () -> {
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        throw new CompletionException(e);
                    }
                    Log.e("auth", "get api client timeout");
                    throw new CompletionException(new TimeoutException());
                }
            )
        );
    }

    private void connectToGoogleApi() {
        this.credentialsApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(
                new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        AuthenticationActivity.this.onApiClientConnectedListeners.forEach(
                            listener -> listener.onConnected(bundle)
                        );
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }
            )
            .enableAutoManage(
                this,
                connectionResult -> Log.e("auth", "connection failed")
            )
            .addApi(Auth.CREDENTIALS_API)
            .build();
    }

    protected CompletableFuture<Void> saveCredentials(String email, String password, long timeout) {
        return this.getGoogleApiClient(timeout).thenAcceptAsync(
            client -> {
                Auth.CredentialsApi.save(
                    client,
                    new Credential.Builder(email).setPassword(password).build()
                )
                    .setResultCallback(
                        status -> {
                            Log.e("auth", status.getClass().getName());
                            if (status.isSuccess()) {
                                Log.e("auth", "credentials saved");
                            } else {
                                if (status.hasResolution()) {
                                    Log.e("auth", "status has resolution");
                                } else {
                                    Log.e("auth", "Save Failed");
                                }
                            }
                        }
                    );
            }
        );
    }

    private CompletableFuture<Void> saveCredentials(String email, String password) {
        return this.saveCredentials(email, password, 3000);
    }

    protected CompletableFuture<User> login(String email, String password) {
        return TaskWrapper.wrapAsync(
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            )
        ).thenApplyAsync(
            authResult -> {
                this.saveCredentials(email, password);
                return FirebaseUserService.toUser(authResult);
            }
        );
    }

    protected CompletableFuture<User> login() {
        return this.getGoogleApiClient(3000).thenApplyAsync(
        client -> {
                Log.e("auth", "got apiclient");
                CredentialRequest credentialRequest = new CredentialRequest.Builder()
                    .setPasswordLoginSupported(true)
                    .build();
                CredentialRequestResult result = Auth.CredentialsApi.request(
                    this.credentialsApiClient,
                    credentialRequest
                ).await();
                Credential credential;
                if (!result.getStatus().isSuccess()) {
                    Log.e("auth", "credential request failed: " + result.getStatus().toString());
                    if (
                        result.getStatus().getStatusCode()
                        == CommonStatusCodes.RESOLUTION_REQUIRED
                    ) {
                        CompletableFuture<Credential> future = new CompletableFuture<>();
                        this.onCredentialsChosenListeners.add(
                            (credentials, resultCode) -> future.complete(credentials)
                        );
                        try {
                            result.getStatus().startResolutionForResult(
                                this,
                                AuthenticationActivity.C_SAVE
                            );
                        } catch (IntentSender.SendIntentException e) {
                            throw new CompletionException(e);
                        }
                        try {
                            credential = future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            Log.e("auth", "failed retrieving credentials from future", e);
                            throw new CompletionException(e);
                        }
                        if (credential == null) {
                            Log.e("auth", "credential is null");
                            throw new CompletionException(new Exception());
                        }
                        Log.e("auth", "getAuthor chose credentials");
                    } else {
                        throw new CompletionException(new Exception());
                    }
                } else {
                    credential = result.getCredential();
                }
                Log.e("auth", "successful retrieval");
                Log.e("auth", String.valueOf(credential == null));
                Log.e(
                    "auth",
                    String.format(
                        "id: %s, password: %s",
                        credential.getId(),
                        credential.getPassword()
                    )
                );
                try {
                    return this.login(
                        credential.getId(),
                        credential.getPassword()
                    ).get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("auth", "failed login");
                    throw new CompletionException(e);
                }
            }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AuthenticationActivity.C_SAVE) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                this.onCredentialsChosenListeners.forEach(
                    listener -> listener.onCredentialsChosen(credential, resultCode)
                );
            } else {
                this.onCredentialsChosenListeners.forEach(
                    listener -> listener.onCredentialsChosen(null, resultCode)
                );
            }
        }
    }

}
