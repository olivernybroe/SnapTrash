package dk.snaptrash.snaptrash.login;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.MapActivity;
import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;


/**
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment implements View.OnClickListener, OnCompleteListener<User> {

    ProgressBar progressBar;

    @Inject AuthProvider auth;

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
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Log.e("AUTH", auth.toString());
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

        auth.login("username", "password");
    }

    @Override
    public void onComplete(@NonNull Task<User> task) {
        if(task.isSuccessful()) {
            this.auth.removeOnLoginListener(this);
        }
        else {
            this.getActivity().runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
        }
    }
}
