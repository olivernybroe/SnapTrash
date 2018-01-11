package dk.snaptrash.snaptrash.PickUp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dk.snaptrash.snaptrash.R;

public class PickUpModeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {
            this.getFragmentManager().beginTransaction()
                .add(R.id.screenView, RecordingFragment.newInstance())
                .commit();
        }

    }
}
