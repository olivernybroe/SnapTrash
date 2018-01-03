package dk.snaptrash.snaptrash.DependencyInjections;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dk.snaptrash.snaptrash.Menu.Routes.RouteFragment;
import dk.snaptrash.snaptrash.login.LogInFragment;
import dk.snaptrash.snaptrash.login.LoginActivity;
import dk.snaptrash.snaptrash.MapActivity;
import dk.snaptrash.snaptrash.login.SplashScreenFragment;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector()
    abstract LogInFragment bindLogInFragment();

    @ContributesAndroidInjector()
    abstract SplashScreenFragment bindSplashScreenFragment();

    @ContributesAndroidInjector()
    abstract LoginActivity bindMainActivity();

    @ContributesAndroidInjector()
    abstract MapActivity bindMapActivity();

    @ContributesAndroidInjector
    abstract RouteFragment bindRouteFragment();
}
