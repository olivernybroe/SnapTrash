package dk.snaptrash.snaptrash.PickUp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import dk.snaptrash.snaptrash.R;

public class PickUpRecordingFragment extends Fragment implements View.OnClickListener {

    public interface OnVideoTakenListener {
        public void videoTaken(File video);
    }

    private Set<OnVideoTakenListener> listeners = Collections.synchronizedSet(
        Collections.newSetFromMap(
            new WeakHashMap<>()
        )
    );

    private CameraView cameraView;

    public PickUpRecordingFragment() {
        // Required empty public constructor
    }

    public static PickUpRecordingFragment newInstance() {
        PickUpRecordingFragment fragment = new PickUpRecordingFragment();
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

        this.cameraView.setAudio(Audio.OFF);
        this.cameraView.setSessionType(SessionType.VIDEO);

        view.findViewById(R.id.RecordButton).setOnClickListener(this);

        this.cameraView.addCameraListener(
            new CameraListener() {
                @Override
                public void onVideoTaken(File video) {
                    PickUpRecordingFragment.this.listeners.forEach(
                        listener -> listener.videoTaken(video)
                    );
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
        Log.e("recordingfragment", "view clicked");
        this.cameraView.startCapturingVideo(null, 3000);
    }

    public void addVideoTakenListener(OnVideoTakenListener listener) {
        this.listeners.add(listener);
    }

    public void removeVideoTakenListener(OnVideoTakenListener listener) {
        this.listeners.remove(listener);
    }


}