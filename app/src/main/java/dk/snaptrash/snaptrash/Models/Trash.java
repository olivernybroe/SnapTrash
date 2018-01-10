package dk.snaptrash.snaptrash.Models;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;
import lombok.Getter;


public class Trash extends Model<Trash>{
    @Getter private String id;
    @Getter private LatLng location;
    @Getter private String picture;
    private User author;
    @Getter private String description;
    private String authorId;

    public Trash(String id, LatLng location, String picture, String description, String authorId) {
        this.id = id;
        this.location = location;
        this.picture = picture != null ? picture : "https://firebasestorage.googleapis.com/v0/b/snaptrash-1507812289113.appspot.com/o/IMG_20180110_144336.jpg?alt=media&token=62b453b8-eee3-4cad-9d75-f728a9a15b13";
        this.description = description;
        this.authorId = authorId;
    }

    public void loadPictureInto(ImageView imageView) {
        this.loadPictureInto(imageView.getContext(), imageView);
    }

    public void loadPictureInto(Context context, ImageView imageView) {
        this.loadPictureInto(context, imageView, null);
    }

    public void loadPictureInto(Context context, ImageView imageView, @Nullable Callback callback) {
        this.loadPicture(context).into(imageView, callback);
    }

    public RequestCreator loadPicture(Context context) {
        return Picasso.with(context).load(picture);
    }

    @Inject
    public User getAuthor(UserService userService) {
        if(this.author != null) {
            return this.author;
        }
        this.author = userService.get(this.authorId);
        return this.author;
    }
}
