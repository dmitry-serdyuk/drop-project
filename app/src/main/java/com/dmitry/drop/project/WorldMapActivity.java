package com.dmitry.drop.project;


import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dmitry.drop.project.adapter.PostAdapter;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModelImpl;
import com.dmitry.drop.project.presenter.WorldMapPresenter;
import com.dmitry.drop.project.presenter.WorldMapPresenterImpl;
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
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import static com.dmitry.drop.project.utility.AnimUtils.getFastOutSlowInInterpolator;

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

    static final int ADD_POST_REQUEST = 1;
    static final int VIEW_POST_REQUEST = 2;
    //Google Play request codes
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //Default Circle variable
    private final static int RADIUS = 100;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.worldMap_layout)
    RelativeLayout worldMapLayout;
    @BindView(R.id.worldMap_debugDeleteButton)
    Button debugDeleteButton;
    @BindView(R.id.woldMap_postReveal)
    FrameLayout postReveal;
    @BindView(R.id.worldMap_postSelectorLayout)
    LinearLayout postSelectorLayout;
    @BindView(R.id.worldMap_postSelector)
    RecyclerView postSelector;
    private GoogleMap mMap;
    private Location mLastLocation;
    private PostAdapter mAdapter;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private boolean mCameraTracking = true;
    private boolean debugDelete = false;
    //Databese
    private List<Post> posts = Collections.emptyList();
    private PostModelImpl postModel;

    LatLng debugPos;

    /* ------------------------ Activity Lifecycle Methods ------------------------ */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_map);
        ButterKnife.bind(this);

        postReveal.setAlpha(0.2f);



        PermissionUtils.verifyStoragePermissions(this);

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

    /* ------------------------ Mosby Methods ------------------------ */

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @NonNull
    @Override
    public WorldMapPresenter createPresenter() {
        postModel = new PostModelImpl();
        return new WorldMapPresenterImpl(postModel);
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
    public void createPost() {
        if (mLastLocation != null) {

            Intent cameraIntent = CreatePostActivity.createIntent(this, mLastLocation.getLatitude(), mLastLocation.getLongitude());
            startActivityForResult(cameraIntent, ADD_POST_REQUEST);
        }
    }

    @Override
    public void viewPost(Post post) {
        if (debugDelete) {
            postModel.delete(post.getId());
            mMap.clear();
            mMapView.invalidate();
            presenter.onStart();
        } else {
            Intent viewPost = ViewPostActivity.createIntent(this, post);
            startActivityForResult(viewPost, VIEW_POST_REQUEST);
        }
    }

    @Override
    public void showPosts(List<Post> posts) {
        this.posts = posts;
        drawAllPosts();
    }

    @Override
    public void addPost(Post post) {
        posts.add(post);
        drawPost(post);
    }


    //TODO: Errors
    @Override
    public void showPostSelector(final List<Post> clickedPosts) {

        Post post = clickedPosts.get(0);
        LatLng pos = new LatLng(post.getLatitude(), post.getLongitude());

        Projection projection = mMap.getProjection();
        Point screenPosition = projection.toScreenLocation(pos);

        CircleView circle = new CircleView(this, (float) post.getRadius(), screenPosition.x, screenPosition.y);
        postReveal.addView(circle);
        postReveal.setVisibility(View.VISIBLE);
        Interpolator interp = getFastOutSlowInInterpolator(this);

        postReveal.setScaleX(1);
        postReveal.setTranslationY(1);
        postReveal.setPivotX(screenPosition.x);
        postReveal.setPivotY(screenPosition.y);

        postReveal.animate()
                .alpha(1f)
                .scaleX(18)
                .scaleY(18)
                .setDuration(1500)
                .setInterpolator(interp)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (animation.getCurrentPlayTime() >= 1000) {
                            showSelectedPosts(clickedPosts);
                            animation.removeAllUpdateListeners();
                        }

                    }
                })
                .start();

    }

    private void showSelectedPosts(final List<Post> clickedPosts) {
        postSelectorLayout.setVisibility(View.VISIBLE);
        postSelector.setLayoutManager(new RecyclerViewUtils.LinearLayoutManagerWithSmoothScroller(this));

        List<Post> animatePosts = new ArrayList<Post>();
        postSelector.addItemDecoration(new RecyclerViewUtils.SimpleDividerItemDecoration(this));
        postSelector.setItemAnimator(new FadeInAnimator());
        postSelector.getItemAnimator().setAddDuration(3000);
        mAdapter = new PostAdapter(clickedPosts, this);
        //AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mAdapter);
        //dapter.setDuration(5000);

        mAdapter.SetOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(WorldMapActivity.this, ViewPostActivity.class);
                View sharedView = view;
                intent.putExtra("post", clickedPosts.get(position));

                startActivity(intent);
            }
        });
        postSelector.setAdapter(mAdapter);
    }

    @Override
    public void showClickPostError(String error) {

    }

    @Override
    public void showLoadingPostsError(String error) {

    }

    @Override
    public void showAddPostError(String error) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_POST_REQUEST && resultCode == RESULT_OK) {
            addPost((Post) data.getParcelableExtra(CreatePostView.POST_EXTRA));
        }
    }

    private void addPostThumbnail(Post post) {
        LatLng postPosition = new LatLng(post.getLatitude(), post.getLongitude());
        GroundOverlayOptions postPicture = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromPath(post.getThumbnailFilePath()))
                .position(postPosition, (float) post.getRadius() + Constants.POST_RADIUS_OFFSET)
                .transparency(Constants.POST_THUMB_TRANSPARENCY);

        mMap.addGroundOverlay(postPicture);
    }

    /* ------------------------ Google Maps Methods ------------------------ */

    private void addPostCircle(double latitude, double longitude) {
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(RADIUS);

        circleOptions.strokeColor(ContextCompat.getColor(this, R.color.blue));
        circleOptions.fillColor(ContextCompat.getColor(this, R.color.light_blue_transp));
        mMap.addCircle(circleOptions);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationButtonClickListener(this);

        presenter.onStart();
    }

    private void drawAllPosts() {
        for (Post post : posts) {
            drawPost(post);
        }
    }

    /* ------------------------ Google Services Methods ------------------------ */

    private void drawPost(Post post) {
        addPostCircle(post.getLatitude(), post.getLongitude());
        addPostThumbnail(post);
    }

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

        if (mCameraTracking)
            moveCameraToMyLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("debug", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

     /* ------------------------ OnClick Methods ------------------------ */

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

    @OnClick(R.id.worldMap_addPostButton)
    public void onAddClick() {
        presenter.onCreatePostClick();
    }


    @Override
    public void onMapClick(LatLng position) {
        debugPos = position;
        presenter.onMapClicked(position.latitude, position.longitude);
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

    public void onDeleteClick(View view) {
        if (!debugDelete) {
            debugDelete = true;
            debugDeleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        } else {
            debugDeleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_white));
            debugDelete = false;
        }
    }
}