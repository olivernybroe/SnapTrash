package dk.snaptrash.snaptrash;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;


/**
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment implements View.OnClickListener {

    ProgressBar progressBar;

    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogInFragment.
     */
    public static LogInFragment newInstance() {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        view.findViewById(R.id.logIn).setOnClickListener(this);
        progressBar = view.findViewById(R.id.loading);

        return view;
    }

    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private Activity activity;

            Runnable setActivity(Activity activity) {
                this.activity = activity;
                return this;
            }

            public void run() {
                Intent intent = new Intent(activity, MapActivity.class);
                progressBar.setVisibility(View.INVISIBLE);
                activity.startActivity(intent);
            }
        }.setActivity(this.getActivity());
        handler.postDelayed(runnable, 1000);

    }
}
