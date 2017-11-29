package dk.snaptrash.snaptrash.Models;

import android.graphics.Picture;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;

import javax.inject.Inject;

import dk.snaptrash.snaptrash.Services.User.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class Trash extends Model<Trash>{
    @Getter private LatLng location;
    @Getter private Picture picture;
    private User author;
    @Getter private String description;
    private String authorId;

    public Trash(LatLng location, Picture picture, String description, String authorId) {
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
