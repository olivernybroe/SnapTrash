package dk.snaptrash.snaptrash.login;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dk.snaptrash.snaptrash.MapActivity;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;

public class LoginActivity extends AppCompatActivity implements HasFragmentInjector {
    @Inject DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject AuthProvider auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        auth.login();
        Intent intent = new Intent(this, MapActivity.class);
        this.startActivity(intent);

        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .add(R.id.screenView, LogInFragment.newInstance())
                .commit();
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }
}
