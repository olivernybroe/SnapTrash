package dk.snaptrash.snaptrash.Services.SnapTrash.Auth;

import dk.snaptrash.snaptrash.Models.User;

public interface OnLogoutListener {

    void onSuccesLogout(User user);

    void OnFailedLogout(User user);

}
