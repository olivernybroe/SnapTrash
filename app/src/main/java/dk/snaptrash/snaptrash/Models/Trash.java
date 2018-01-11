package dk.snaptrash.snaptrash.Models;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import lombok.Getter;

public class Trash extends Model<Trash> {

    @Getter private String id;
    @Getter private Location location;
    @Getter private String pictureUrl;
    @Getter private String description;
    @Getter private String authorId;

    public Trash(String id, Location location, String pictureUrl, String description, String authorId) {
        this.id = id;
        this.location = location;
        this.pictureUrl = pictureUrl != null ? pictureUrl : "https://firebasestorage.googleapis.com/v0/b/snaptrash-1507812289113.appspot.com/o/trash_placeholder.jpglt=media&token=62b453b8-eee3-4cad-9d75-f728a9a15b13";
        this.pictureUrl = pictureUrl;
        this.description = description;
        this.authorId = authorId;
    }

    public Trash(String id, LatLng location, String pictureUrl, String description, String authorId) {
        this.id = id;
        this.location = new Location("");
        this.location.setLatitude(location.latitude);
        this.location.setLongitude(location.longitude);
        this.pictureUrl = pictureUrl;
        this.description = description;
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
        return Picasso.with(context).load(this.pictureUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trash trash = (Trash) o;

        return id != null ? id.equals(trash.id) : trash.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
