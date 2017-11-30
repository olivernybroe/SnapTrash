package dk.snaptrash.snaptrash;

import android.Manifest;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dk.snaptrash.snaptrash.Models.Trash;
import dk.snaptrash.snaptrash.Models.User;
import dk.snaptrash.snaptrash.Services.Auth.AuthProvider;
import dk.snaptrash.snaptrash.Services.Trash.TrashMapMap;
import dk.snaptrash.snaptrash.Services.Trash.TrashService;
import dk.snaptrash.snaptrash.login.LoginActivity;

public class MapActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;

    public static GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Drawer leftSideMenu;
    private TextView leftSideMenuButton;
    @Inject AuthProvider auth;
    @Inject TrashService trashService;
    private User user;
    private TrashMapMap trashMarkerMap;

    public MapActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.e("AUTH", auth.toString());

        // Create the Google Api Client with location services.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        try {
            user = auth.user();
        } catch (AuthenticatorException e) {
            Log.e("Authentication", "Not logged in!");
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent); //TODO add message about not being logged in.
            return;
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        leftSideMenu = new DrawerBuilder()
                .withActivity(this)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_routes_title).withIcon(R.drawable.menu_routes_logo),
                        new PrimaryDrawerItem().withName(R.string.menu_store_title).withIcon(R.drawable.menu_store_logo),
                        new PrimaryDrawerItem().withName(R.string.menu_social_title).withIcon(R.drawable.menu_social_logo),
                        new PrimaryDrawerItem().withName(R.string.menu_settings_title).withIcon(R.drawable.menu_settings_logo),
                        new PrimaryDrawerItem().withName(R.string.menu_help_title).withIcon(R.drawable.menu_help_logo)
                )
                .withAccountHeader(new AccountHeaderBuilder()
                        .withSelectionListEnabled(false)
                        .withActivity(this)
                        .addProfiles(
                                new ProfileDrawerItem().withName(user.getUsername()).withIcon(R.drawable.menu_account_logo)
                        )
                        .build()
                )
                .build();
        leftSideMenu.getSlider();

        leftSideMenu.deselect();

        leftSideMenuButton = findViewById(R.id.openSideMenuButton);
        leftSideMenuButton.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMaxZoomPreference(20);
        googleMap.setMinZoomPreference(20);
        googleMap.setOnMarkerClickListener(this);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(39.87266, -4.028275))
                .zoom(20)
                .tilt(67.5f)
                .bearing(314)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap = googleMap;

        trashMarkerMap = new TrashMapMap(
                mMap,
                new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmapFromSvg(R.drawable.trash_icon))),
                trashService.closeTo(new LatLng(39.87266, -4.028275))
        );

    }

    private Bitmap bitmapFromSvg(int svgId) {
        Drawable drawable = getDrawable(R.drawable.trash_icon);

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getMinimumHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.onMapReady(mMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            return;
        }

        locationRequest = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Connection failed", connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMap == null) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())
        ));
    }

    @Override
    public void onClick(View view) {
        if (view == leftSideMenuButton) {
            leftSideMenu.openDrawer();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MarkerClicked", "CLICKED");
        Trash trash = trashMarkerMap.remove(marker);
        trashService.pickUp(trash, null);
        return true;
    }
}
