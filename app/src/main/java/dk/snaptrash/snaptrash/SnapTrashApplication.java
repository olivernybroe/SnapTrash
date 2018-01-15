package dk.snaptrash.snaptrash;


import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dk.snaptrash.snaptrash.DependencyInjections.DaggerAppComponent;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class SnapTrashApplication extends Application implements HasActivityInjector{

    @Inject DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.create()
            .inject(this);

        final OkHttpClient client = new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build();

        final Picasso picasso = new Picasso.Builder(this)
            .listener((picasso1, uri, exception) -> {
                Log.e("PICASSO", "failed loading image on url:"+uri.toString()
                    , exception);
            })
            .build();

        Picasso.setSingletonInstance(picasso);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder)
                    .fit().centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }
}
