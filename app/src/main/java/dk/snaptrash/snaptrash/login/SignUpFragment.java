package dk.snaptrash.snaptrash.login;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.Map.MapActivity;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;

public class SignUpFragment extends Fragment {

    TextView alreadyMember;
    Button signUpButton;
    EditText password;
    EditText displayName;
    EditText email;
    ImageView profilePic;
    Uri profilePicUri;
    @Inject UserService userService;
    LoginActivity loginActivity;
    static final int PICK_IMAGE = 99991;
    private boolean hasWarnedNoProfilePic = false;

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        loginActivity = (LoginActivity) this.getActivity();
        loginActivity.inSignUp = true;

        alreadyMember = view.findViewById(R.id.link_login);
        alreadyMember.setOnClickListener(v -> this.alreadyMember());

        signUpButton = view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> this.signUp());

        profilePic = view.findViewById(R.id.imageView2);
        profilePic.setOnClickListener(v -> this.selectPicture());

        password = view.findViewById(R.id.input_password);
        email = view.findViewById(R.id.input_email);
        displayName = view.findViewById(R.id.input_name);

        return view;
    }

    public void alreadyMember() {
        this.getFragmentManager()
            .beginTransaction()
            .replace(R.id.screenView, LogInFragment.newInstance())
            .commit();
    }

    public void signUp() {
        if(!validate()) {
            return;
        }

        signUpButton.setEnabled(false);
        userService.create(
            displayName.getText().toString(),
            email.getText().toString(),
            password.getText().toString(),
            profilePicUri
        ).whenComplete((user, throwable) -> {
            if(throwable == null) {
                Log.e("auth", "succeful sign up, loggin in");
                this.loginActivity.login(email.getText().toString(), password.getText().toString())
                .whenCompleteAsync((_user, _throwable) -> {
                    Log.e("auth", "signup", _throwable);
                    loginActivity.runOnUiThread(
                        () -> {
                            if (_throwable == null) {
                                this.startActivity(
                                    new Intent(this.getActivity(), MapActivity.class)
                                );
                            } else {
                                Toast.makeText(
                                    loginActivity,
                                    "Failed login",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    );
                });
            }
            else {
                Log.e("SignUp", "failed signing up", throwable);
                loginActivity.runOnUiThread(() -> {
                    Toast.makeText(loginActivity, "Failed signing up.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    loginActivity.getContentResolver(),
                    profilePicUri = data.getData()
                );
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(loginActivity, "Failed adding picture.", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validate() {
        boolean valid = true;

        String name = this.displayName.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if(profilePicUri == null && !hasWarnedNoProfilePic) {
            hasWarnedNoProfilePic = true;
            Toast.makeText(loginActivity, "(Optional) Remember to add profile picture.", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Email is invalid.");
            valid = false;
        } else {
            this.email.setError(null);
        }

        if (name.isEmpty() || name.length() < 5) {
            this.displayName.setError("at least 5 characters.");
            valid = false;
        } else {
            this.displayName.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            this.password.setError("at least 6 characters");
            valid = false;
        } else {
            this.password.setError(null);
        }
        return valid;
    }

    private void selectPicture() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select profile picture");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

}
