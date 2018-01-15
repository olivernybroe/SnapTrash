package dk.snaptrash.snaptrash.DependencyInjections;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dk.snaptrash.snaptrash.Services.SnapTrash.FirebaseSnapTrashModule;
import dk.snaptrash.snaptrash.SnapTrashApplication;

@Component(modules = {
    ActivityBuilder.class,
    FirebaseSnapTrashModule.class,
    AndroidInjectionModule.class,
    AppModule.class
})
@Singleton
public interface AppComponent {

    void inject(SnapTrashApplication app);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(SnapTrashApplication application);
        AppComponent build();
    }

}
