package dk.snaptrash.snaptrash.login;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;

public class LoginActivity
extends
    AuthenticationActivity
implements
    HasFragmentInjector
{

    public static final String loginReasonArgument = "LOGIN_REASON";
    public boolean inSignUp = false;

    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject protected AuthProvider auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_container);

        this.getFragmentManager().beginTransaction()
            .add(R.id.screenView, SplashScreenFragment.newInstance())
            .commit();

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            String reason = extras.getString(LoginActivity.loginReasonArgument);
            if (reason != null) {
                Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(inSignUp) {
            return;
        }

        if (this.auth.loggedIn()) {
            Log.e("loginacticity", "already logged in as: " + this.auth.getUser());
            this.goToMap();
        } else {
            this.login()
                .whenCompleteAsync(
                    (user, throwable) -> this.runOnUiThread(
                        () -> {
                            if (throwable == null) {
                                this.goToMap();
                            } else {
                                this.openSignIn();
                            }
                        }
                    )
                );
        }

    }

    private void goToMap() {
        this.startActivity(
            new Intent(this, MapActivity.class)
        );
    }

    private void openSignIn() {
        this.getFragmentManager()
            .beginTransaction()
            .replace(R.id.screenView, LogInFragment.newInstance())
            .commit();
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public void onBackPressed() {
        Log.e("loginaktivity", "backpressed");
        this.finishAffinity();
    }
}
