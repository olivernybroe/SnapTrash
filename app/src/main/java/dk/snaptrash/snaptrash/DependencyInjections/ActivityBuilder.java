package dk.snaptrash.snaptrash.DependencyInjections;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dk.snaptrash.snaptrash.Map.Trash.TrashDialog;
import dk.snaptrash.snaptrash.Menu.ProfileDialog;
import dk.snaptrash.snaptrash.Menu.Routes.RouteDialog;
import dk.snaptrash.snaptrash.PickUp.PickUpActivity;
import dk.snaptrash.snaptrash.PickUp.PickUpRecordingFragment;
import dk.snaptrash.snaptrash.login.LogInFragment;
import dk.snaptrash.snaptrash.login.LoginActivity;
import dk.snaptrash.snaptrash.Map.MapActivity;
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
    abstract RouteDialog bindRouteDialog();

    @ContributesAndroidInjector
    abstract TrashDialog bindTrashInfoDialog();

    @ContributesAndroidInjector
    abstract PickUpActivity bindPickUpActivity();

    @ContributesAndroidInjector
    abstract PickUpRecordingFragment bindPickUpRecordingFragment();

    @ContributesAndroidInjector
    abstract ProfileDialog bindProfileDialog();

}