package dk.snaptrash.snaptrash.login;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;


public class LogInFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private Button signInButton;

    @Inject
    AuthProvider auth;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance() {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        this.signInButton = view.findViewById(R.id.logIn);
        this.progressBar = view.findViewById(R.id.loading);

        this.signInButton.setOnClickListener(this);

        return view;
    }

    private void working() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.signInButton.setEnabled(false);

    }

    private void idle() {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.signInButton.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        this.working();
        ((AuthenticationActivity) this.getActivity()).login(
            "hotshit@xd.ng",
            ":)))))"
        )
            .whenCompleteAsync(
                (user, throwable) -> {
                    this.getActivity().runOnUiThread(
                        () -> {
                            if (throwable == null) {
                                this.startActivity(
                                    new Intent(this.getActivity(), MapActivity.class)
                                );
                            } else {
                                this.idle();
                                Toast.makeText(
                                    this.getActivity(),
                                    "Failed login",
                                    Toast.LENGTH_SHORT
                                ).show(); //TODO add actual error handling
                                Log.e("loginfragment", "pls", throwable);
                            }
                        }
                    );
                }
            );

    }

}
