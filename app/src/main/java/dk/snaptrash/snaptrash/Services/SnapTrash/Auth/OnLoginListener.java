package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import dk.snaptrash.snaptrash.Models.User;

public interface OnLoginListener {

    void onSuccesLogin(User user);

    void OnFailedLogin();

}
