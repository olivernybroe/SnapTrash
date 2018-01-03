package dk.snaptrash.snaptrash.DependencyInjections;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dk.snaptrash.snaptrash.Services.SnapTrash.FirebaseSnapTrashModule;
import dk.snaptrash.snaptrash.SnapTrashApplication;

@Component(modules = {
        ActivityBuilder.class,
        FirebaseSnapTrashModule.class,
        AndroidInjectionModule.class,
})
@Singleton
public interface AppComponent {

    void inject(SnapTrashApplication app);
}
