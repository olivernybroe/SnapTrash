package dk.snaptrash.snaptrash.Models;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.security.Timestamp;
import java.util.Date;

import dk.snaptrash.snaptrash.Utils.Geo.Coordinate;
import dk.snaptrash.snaptrash.Utils.Geo.Geo;
import lombok.Getter;
import lombok.Setter;

public class Trash extends Model<Trash> {

    @Getter private String id;
    @Getter private Coordinate location;
    @Getter private String pictureUrl;
    @Getter private String description;
    @Getter private String authorId;
    @Getter private String reservedById;
    @Nullable@Getter private Date reservedUntil;

    public enum Status {
        AVAILABLE,
        PENDING_REMOVAL_CONFIRMED,
        PICKED_UP
    }

    @Getter@Setter private Status status = Status.AVAILABLE;

    public Trash(String id, Coordinate location, String pictureUrl, String description, String authorId, String reservedById) {
        this.id = id;
        this.location = location;
        this.pictureUrl = pictureUrl != null ? pictureUrl : "https://firebasestorage.googleapis.com/v0/b/snaptrash-1507812289113.appspot.com/o/IMG_20180110_144336.jpg?alt=media&token=2ff58097-37a5-45d3-8450-cabd65b6b229";
        this.description = description;
        this.authorId = authorId == null ? "Cfl8DtrGjIYXOgM5PTOXLXwnCAJ2" : authorId;
        this.reservedById = reservedById;
    }

    public Trash(String id, LatLng location, String pictureUrl, String description, String authorId, String reservedById) {
        this(id, new Coordinate(location.latitude, location.longitude), pictureUrl, description, authorId, reservedById);
    }

    public Trash(String id, LatLng location, String pictureUrl, String description, String authorId, String reservedById, Date reservedUntil) {
        this(id, new Coordinate(location.latitude, location.longitude), pictureUrl, description, authorId, reservedById);
        this.reservedUntil = reservedUntil;
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

    public LatLng toLatLng() {
        return Geo.toLatLng(this.getLocation());
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
