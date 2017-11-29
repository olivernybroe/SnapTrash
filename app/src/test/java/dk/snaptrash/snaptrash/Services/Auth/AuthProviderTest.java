package dk.snaptrash.snaptrash.Services.Auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import dk.snaptrash.snaptrash.DaggerAppComponent;
import dk.snaptrash.snaptrash.Models.User;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class AuthProviderTest {
    AuthProvider authProvider;

    @Before
    public void setUp() throws Exception {
        authProvider = DaggerAppComponent.create().auth();
    }

    @After
    public void tearDown() throws Exception {
        authProvider = null;
    }

    @Test
    public void can_login() throws Exception {
        assertThat(authProvider.isLoggedIn()).isFalse();
        User user = authProvider.login("username", "password");

        assertThat(authProvider.isLoggedIn()).isTrue();
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("username");
    }

    @Test
    public void can_logout() throws Exception {
        assertThat(authProvider.isLoggedIn()).isFalse();
        authProvider.login("username", "password");
        assertThat(authProvider.isLoggedIn()).isTrue();
        /*
        authProvider.logout();

        assertThat(authProvider.isLoggedIn()).isFalse();
        assertThat(authProvider.user()).isNull();
        */
    }

}