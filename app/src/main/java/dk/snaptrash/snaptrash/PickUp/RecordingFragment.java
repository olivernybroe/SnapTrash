package dk.snaptrash.snaptrash.PickUp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;

import dk.snaptrash.snaptrash.R;

public class RecordingFragment extends Fragment implements View.OnClickListener {

    private CameraView cameraView;

    public RecordingFragment() {
        // Required empty public constructor
    }

    public static RecordingFragment newInstance() {
        RecordingFragment fragment = new RecordingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_recording, container, false);

        this.cameraView = view.findViewById(R.id.CameraView);

        this.cameraView.setOnClickListener(this);

        this.cameraView.addCameraListener(
            new CameraListener() {
                @Override
                public void onVideoTaken(File video) {
                    super.onVideoTaken(video);
                }
            }
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.cameraView.destroy();
    }

    @Override
    public void onClick(View v) {
        this.cameraView.startCapturingVideo(null, 3000);
    }


}
