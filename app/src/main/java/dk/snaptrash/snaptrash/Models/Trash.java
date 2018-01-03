package dk.snaptrash.snaptrash.Models;

import android.graphics.Picture;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Services.SnapTrash.User.UserService;
import lombok.Getter;


public class Trash extends Model<Trash>{
    @Getter private String id;
    @Getter private LatLng location;
    @Getter private Picture picture;
    private User author;
    @Getter private String description;
    private String authorId;

    public Trash(String id, LatLng location, Picture picture, String description, String authorId) {
        this.id = id;
        this.location = location;
        this.picture = picture;
        this.description = description;
        this.authorId = authorId;
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
