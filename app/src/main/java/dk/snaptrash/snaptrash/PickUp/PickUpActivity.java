package dk.snaptrash.snaptrash.PickUp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Trash.TrashService;

public class PickUpActivity
extends
    AppCompatActivity
implements
    PickUpRecordingFragment.OnVideoTakenListener,
    HasFragmentInjector
{

    public static final int PICK_UP_CODE =  69;

    public static final String trashParameter = "TRASH";

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Inject TrashService trashService;

    private Trash trash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_container);

        Bundle extras = this.getIntent().getExtras();

        Log.e("pickiupactivity", String.valueOf(extras == null));

        if (extras != null) {
            this.trash = (Trash) extras.getSerializable(PickUpActivity.trashParameter);
        }

        Log.e("pickiupactivity", String.valueOf(this.trash == null));

        PickUpRecordingFragment fragment = PickUpRecordingFragment.newInstance();

        fragment.addVideoTakenListener(this);

        this.getFragmentManager().beginTransaction()
            .add(R.id.screenView, fragment)
            .commit();

    }

    @Override
    public void videoTaken(File video) {
        TrashService.OnPickUpVerifiedListener listener = trash -> {
            if (trash == this.trash) {
                this.runOnUiThread(
                    () -> Toast.makeText(
                        this.getApplicationContext(),
                        getString(R.string.TrashPickUpVerified),
                        Toast.LENGTH_SHORT
                    ).show()
                );
            }
        };
        this.trashService.addOnPickUpVerifiedListener(listener);

        this.trashService.pickUp(
            this.trash,
            video
        ).whenCompleteAsync(
            (trash, throwable) -> {
                if (throwable == null) {
                    Intent data = new Intent();
                    data.putExtra(PickUpActivity.trashParameter, this.trash);
                    if (this.getParent() == null) {
                        this.setResult(Activity.RESULT_OK, data);
                    } else {
                        this.getParent().setResult(Activity.RESULT_OK, data);
                    }
                } else {
                    this.trashService.removeOnPickUpVerifiedListener(listener);
                    Log.e("pickactivity", "rip", throwable);
                    this.runOnUiThread(
                        () -> Toast.makeText(this, R.string.FailedPickUp, Toast.LENGTH_SHORT).show()
                    );
                    Log.e("pickupactivity", "after toast");
                }
                this.finish();
            }
        );
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return fragmentInjector;
    }

}
