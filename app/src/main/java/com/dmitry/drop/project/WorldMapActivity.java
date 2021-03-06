package com.dmitry.drop.project;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.dmitry.drop.project.adapter.PostAdapter;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModelImpl;
import com.dmitry.drop.project.presenter.WorldMapPresenter;
import com.dmitry.drop.project.presenter.WorldMapPresenterImpl;
import com.dmitry.drop.project.utility.AnimUtils;
import com.dmitry.drop.project.utility.CircleView;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.utility.PermissionUtils;
import com.dmitry.drop.project.utility.RecyclerViewUtils;
import com.dmitry.drop.project.view.CreatePostView;
import com.dmitry.drop.project.view.WorldMapView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import static com.dmitry.drop.project.utility.AnimUtils.getFastOutSlowInInterpolator;

/**
 * Created by Dmitry on 15/06/2016.
 * <p/>
 * Main activity and home screen for the application
 * <p/>
 * Loads Google Maps and Google Play Services api's
 * Loads and draws all posts onto the google map
 */

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


    // Constants
    //================================================================================
    private static final int CENTRE_MARKER_RADIUS = 55;
    private static final int CAN_REPLY_RADIUS = 28;
    //Google Play request codes
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final float POST_RADIUS_OFFSET = 3f;
    public static final float POST_THUMB_TRANSPARENCY = 0.3f;
    // Location updates intervals in sec
    public static int UPDATE_INTERVAL = 500; // 1 sec
    public static int FATEST_INTERVAL = 100; // 0.2 sec

    //Camera constants
    public static int CAMERA_TILT = 30;
    public static int CAMERA_ZOOM = 15;

    // Views
    //================================================================================
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.worldMap_layout)
    RelativeLayout worldMapLayout;
    @BindView(R.id.woldMap_postReveal)
    FrameLayout postReveal;
    @BindView(R.id.worldMap_postSelector)
    RecyclerView postSelector;

    // Variables
    //================================================================================
    private GoogleMap mMap;
    private Location mLastLocation;
    private PostAdapter mAdapter;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    //boolean flag if user changes camera
    private boolean mCameraTracking = true;

    //Database
    private List<Post> posts = Collections.emptyList();
    private PostModelImpl postModel;

    // Constructors
    //================================================================================
    @NonNull
    @Override
    public WorldMapPresenter createPresenter() {
        postModel = new PostModelImpl();
        return new WorldMapPresenterImpl(postModel);
    }

    // Activity lifecycle methods
    //================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_map);
        ButterKnife.bind(this);

        //Verify permissions upon startup
        PermissionUtils.verifyStoragePermissions(this);

        postSelector.setLayoutManager(new RecyclerViewUtils.LinearLayoutManagerWithSmoothScroller(this));
        postSelector.addItemDecoration(new RecyclerViewUtils.SimpleDividerItemDecoration(this));
        postSelector.setItemAnimator(new FadeInAnimator());
        postSelector.getItemAnimator().setAddDuration(AnimUtils.ONE_SECOND);

        //Set up google map object
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
    public void onMapReady(GoogleMap map) {
        //When Google Maps API is ready and map is loaded apply listeners to map
        //Load and draw all posts in the databse to map
        mMap = map;
        map.setIndoorEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationButtonClickListener(this);

        presenter.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_POST_REQUEST && resultCode == RESULT_OK) {
            addPost((Post) data.getParcelableExtra(CreatePostView.POST_EXTRA));
        } else if (requestCode == VIEW_POST_REQUEST) {
            //Reset animations and hide post selector
            resetViewPostAnim();
            resetPostSelector();
        }
        drawAllPosts();
    }

    // Implemented methods
    //================================================================================
    @Override
    public void moveCameraToMyLocation() {
        if (mLastLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(CAMERA_ZOOM)
                    .bearing(mLastLocation.getBearing())
                    .tilt(CAMERA_TILT)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void createPost() {
        if (mLastLocation != null) {
            Intent cameraIntent = CreatePostActivity.createIntent(this, mLastLocation.getLatitude(), mLastLocation.getLongitude());
            startActivityForResult(cameraIntent, ADD_POST_REQUEST);
        }
    }

    @Override
    public void viewPost(final Post post) {
        showPostClickAnim(post.getLatitude(), post.getLongitude(), new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getCurrentPlayTime() > AnimUtils.VIEW_POST / 2) {
                    animation.removeAllUpdateListeners();
                    Intent viewPost = ViewPostActivity.createIntent(WorldMapActivity.this, post.getId(), canReply(post));
                    startActivityForResult(viewPost, VIEW_POST_REQUEST);
                }
            }
        });

    }

    @Override
    public void showPosts(List<Post> posts) {
        this.posts = posts;
        drawAllPosts();
    }

    @Override
    public void addPost(Post post) {
        posts.add(post);
    }

    private boolean canReply(Post post) {
        if (mLastLocation != null) {
            float[] distance = new float[1];
            Location.distanceBetween(post.getLatitude(), post.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getLongitude(), distance);
            return distance[0] < CAN_REPLY_RADIUS;
        }
        return false;
    }

    @Override
    public void showPostSelector(double latitude, double longitude, final List<Post> clickedPosts) {
        //Play post clicked animation with click coordinates
        showPostClickAnim(latitude, longitude, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getCurrentPlayTime() > AnimUtils.VIEW_POST / 2) {
                    animation.removeAllUpdateListeners();
                    postSelector.setVisibility(View.VISIBLE);

                    //Create a separate list for the adapter to animate
                    List<Post> animatePost = new ArrayList<>();
                    //Pass coordinates to adapter in order to determine if user is within radius
                    mAdapter = new PostAdapter(animatePost, WorldMapActivity.this, mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    mAdapter.SetOnItemClickListener(new PostAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            viewPost(clickedPosts.get(position));
                        }
                    });
                    postSelector.setAdapter(mAdapter);

                    //Insert each clicked post individually in order to animate them
                    for (Post post : clickedPosts) {
                        mAdapter.add(post);
                    }
                }
            }
        });
    }

    private void resetPostSelector() {
        if (mAdapter != null)
            mAdapter.reset();
    }

    private void resetViewPostAnim() {
        postReveal.removeAllViews();
        postReveal.setVisibility(View.INVISIBLE);
        postReveal.setScaleX(AnimUtils.ORIGINAL_SCALE);
        postReveal.setScaleY(AnimUtils.ORIGINAL_SCALE);
    }

    private void addPostThumbnail(Post post) {
        //Draw post thumbnail as overlay and add center marker
        //Center marker will be used display the radius the user needs to be within
        //in order to reply to a post
        LatLng postPosition = new LatLng(post.getLatitude(), post.getLongitude());
        GroundOverlayOptions postPicture = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromPath(post.getThumbnailFilePath()))
                .position(postPosition, post.getRadius() * POST_RADIUS_OFFSET)
                .transparency(POST_THUMB_TRANSPARENCY);

        mMap.addGroundOverlay(postPicture);


        //Determine if user is withing post radius
        //Display appropriate post center marker
        Bitmap centreMarkerBitmap;
        if (canReply(post))
            centreMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.centre_mark_activated);
        else
            centreMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.centre_mark);

        GroundOverlayOptions centreMark = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(centreMarkerBitmap))
                .position(postPosition, CENTRE_MARKER_RADIUS);

        mMap.addGroundOverlay(centreMark);
    }

    private void addPostCircle(Post post) {
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(post.getLatitude(), post.getLongitude()))
                .radius(post.getRadius());

        if (canReply(post)) {
            circleOptions.strokeColor(ContextCompat.getColor(this, R.color.green));
            circleOptions.fillColor(ContextCompat.getColor(this, R.color.light_green_transp));
        } else {
            circleOptions.strokeColor(ContextCompat.getColor(this, R.color.blue));
            circleOptions.fillColor(ContextCompat.getColor(this, R.color.light_blue_transp));
        }
        mMap.addCircle(circleOptions);
    }

    private void drawAllPosts() {
        mMap.clear();
        mMapView.invalidate();
        for (Post post : posts) {
            drawPost(post);
        }
    }

    private void drawPost(Post post) {
        addPostCircle(post);
        addPostThumbnail(post);
    }

    @Override
    public void showPostClickAnim(double latitude, double longitude, ValueAnimator.AnimatorUpdateListener listener) {

        //Translate post or map click latitude and longitude into screen x and y values
        LatLng pos = new LatLng(latitude, longitude);
        Projection projection = mMap.getProjection();
        Point screenPosition = projection.toScreenLocation(pos);

        CircleView circleExpandAnim = new CircleView(this, screenPosition.x, screenPosition.y);
        postReveal.addView(circleExpandAnim);
        postReveal.setVisibility(View.VISIBLE);

        Interpolator interp = getFastOutSlowInInterpolator(this);

        //Setup the animation with passed listener
        postReveal.setAlpha(AnimUtils.TRANSPARENT_ALPHA);
        postReveal.setPivotX(screenPosition.x);
        postReveal.setPivotY(screenPosition.y);
        postReveal.animate()
                .alpha(AnimUtils.ORIGINAL_ALPHA)
                .scaleX(AnimUtils.MAXIMUM_SCALE)
                .scaleY(AnimUtils.MAXIMUM_SCALE)
                .setDuration(AnimUtils.VIEW_POST)
                .setUpdateListener(listener)
                .setInterpolator(interp)
                .start();
    }

    // Google play services
    //================================================================================
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(Constants.INFO_TAG, getString(R.string.play_services_device_not_supported_error));
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
        } else {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
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

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        createLocationRequest();
        togglePeriodicLocationUpdates();
        //Enable button to position camera at users current location
        enableMyLocation();
        // Once connected with google api, get the location
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
        drawAllPosts();

        if (mCameraTracking)
            moveCameraToMyLocation();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(Constants.DEBUG_TAG, getString(R.string.play_services_connection_error)
                + connectionResult.getErrorCode());
    }

    // Listeners
    //================================================================================

    @OnClick(R.id.worldMap_addPostButton)
    public void onAddClick() {
        presenter.onAddPostClick();
    }

    @Override
    public void onMapClick(LatLng position) {
        presenter.onMapClicked(position.latitude, position.longitude);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom != CAMERA_ZOOM) {
            mCameraTracking = false;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mCameraTracking = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (postReveal.getVisibility() == View.VISIBLE) {
            resetViewPostAnim();
            resetPostSelector();
        } else {
            super.onBackPressed();
        }
    }

    // Error messages
    //================================================================================
    @Override
    public void showClickPostError(String error) {

    }

    @Override
    public void showLoadingPostsError(String error) {

    }

    @Override
    public void showAddPostError(String error) {

    }
}