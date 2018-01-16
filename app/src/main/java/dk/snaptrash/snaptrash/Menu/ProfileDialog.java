package dk.snaptrash.snaptrash.Menu;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.R;
import dk.snaptrash.snaptrash.Services.SnapTrash.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;

public class ProfileDialog extends DialogFragment implements Callback {

    @Inject
    AuthProvider auth;
    CircleImageView profileView;
    TextView emailView;
    TextView nameView;
    ImageView headerView;
    int loadCount = 0;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile, container, false);

        User user = auth.getUser();

        progressBar = view.findViewById(R.id.profileDialogProgressBar);
        headerView = view.findViewById(R.id.header_view);
        Picasso.with(this.getActivity()).load("http://placeimg.com/640/480/nature")
            .placeholder(R.drawable.profile_background_placeholder)
            .fit()
            .centerInside()
            .into(headerView, this);

        profileView = view.findViewById(R.id.profile_view);
        Picasso.with(this.getActivity()).load(user.getAvatarUrl())
            .noFade()
            .placeholder(R.drawable.placeholder_account_circle)
            .into(profileView, this);

        emailView = view.findViewById(R.id.email_view);
        emailView.setText(user.getEmail());

        nameView = view.findViewById(R.id.name_view);
        nameView.setText(user.getUsername() != null ? user.getUsername() : "Iohan Strässenburg");

        return view;
    }

    @Override
    public void onSuccess() {
        if(++loadCount >= 2) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onError() {
        Toast.makeText(this.getActivity(), "Failed fetching your profile.", Toast.LENGTH_SHORT).show();
        this.dismiss();
    }
}
