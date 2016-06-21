package com.dmitry.drop.project;


import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.presenter.WorldMapPresenter;
import com.dmitry.drop.project.presenter.WorldMapPresenterImpl;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.view.WorldMapView;
import com.dmitry.drop.project.utility.PermissionUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorldMapActivity extends MvpActivity<WorldMapView, WorldMapPresenter>
        implements
        WorldMapView,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener {


    @BindView(R.id.map)
    MapView mMapView;

    private GoogleMap mMap;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private boolean mCameraTracking = true;

    //Google Play request codes
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    static final int ADD_POST_REQUEST = 1;
    static final int VIEW_POST_REQUEST = 2;

    //Default Circle variable
    private final static int RADIUS = 100;

    //Databese
    private List<Post> posts;

    /* ------------------------ Activity Lifecycle Methods ------------------------ */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_map);
        ButterKnife.bind(this);

        PermissionUtils.verifyStoragePermissions(this);

        // TODO: Move to model
        posts = Post.getAll();
        if (posts == null)
            posts = Collections.emptyList();

        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        }

        //Check availability of play services
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    /* ------------------------ Mosby Methods ------------------------ */

    @NonNull
    @Override
    public WorldMapPresenter createPresenter() {
        return new WorldMapPresenterImpl();
    }

    /* ------------------------ Implemented View Methods ------------------------ */
    @Override
    public void moveCameraToMyLocation() {

        if (mLastLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(Constants.ZOOM)
                    .bearing(mLastLocation.getBearing())
                    .tilt(Constants.TILT)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


    }

    @Override
    public void addPost() {
        if (mLastLocation != null) {

            Intent cameraIntent = CreatePostActivity.createIntent(this, mLastLocation.getLatitude(), mLastLocation.getLongitude());
            startActivityForResult(cameraIntent, ADD_POST_REQUEST);
        }
    }

    @Override
    public void viewPost(Post post) {
        Intent viewPost = ViewPostActivity.createIntent(this, post.getId());

        //Toast.makeText(this,  reply.getId().toString() + "", Toast.LENGTH_SHORT).show();


        startActivityForResult(viewPost, VIEW_POST_REQUEST);

        //Toast.makeText(this, "I am Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_POST_REQUEST && resultCode == RESULT_OK) {

            double latitude = data.getDoubleExtra(Constants.LATITUDE, 0);
            double longitude = data.getDoubleExtra(Constants.LONGITUDE, 0);

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(RADIUS);


            circleOptions.strokeColor(ContextCompat.getColor(this, R.color.blue));
            circleOptions.fillColor(ContextCompat.getColor(this, R.color.light_blue));

            mMap.addCircle(circleOptions);

            // TODO: Move to model
            posts = Post.getAll();
        }

    }

    /* ------------------------ Google Maps Methods ------------------------ */

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationButtonClickListener(this);

        for (Post post : posts) {

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(post.getLatitude(), post.getLongitude()))
                    .radius(RADIUS);


            circleOptions.strokeColor(ContextCompat.getColor(this, R.color.blue));
            circleOptions.fillColor(ContextCompat.getColor(this, R.color.light_blue));

            mMap.addCircle(circleOptions);
        }
    }

    /* ------------------------ Google Services Methods ------------------------ */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(Constants.INFO_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {

            mRequestingLocationUpdates = true;

            startLocationUpdates();

            Log.d(Constants.DEBUG_TAG, "Periodic location updates started!");
        } else {
            mRequestingLocationUpdates = false;

            stopLocationUpdates();

            Log.d(Constants.DEBUG_TAG, "Periodic location updates stopped!");
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(Constants.DISPLACEMENT);
    }

    public void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Once connected with google api, get the location
        getLastLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        createLocationRequest();
        togglePeriodicLocationUpdates();
        enableMyLocation();
        getLastLocation();

        presenter.onMyLocationClicked();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        //presenter.onLocationClicked(mLastLocation.getLongitude(), mLastLocation.getLatitude());
        if (mCameraTracking)
            moveCameraToMyLocation();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("debug", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

     /* ------------------------ OnClick Methods ------------------------ */

    @OnClick(R.id.worldMap_addPostButton)
    public void onAddClick() {
        presenter.onAddPostClick();
    }

    @Override
    public void onMapClick(LatLng position) {
        presenter.onMapClicked(posts, position.latitude, position.longitude);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom != Constants.ZOOM) {
            mCameraTracking = false;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mCameraTracking = true;
        return false;
    }
}