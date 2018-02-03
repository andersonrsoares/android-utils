package br.com.andersonsoares.activityutil;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 111;


    protected GoogleApiClient mGoogleApiClient;
    private static Location mCurrentLocation;
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mCurrentLocation = LocationServices
                    .FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if(mCurrentLocation != null){
                Log.i("LOG", "latitude: "+mCurrentLocation.getLatitude());
                Log.i("LOG", "longitude: "+mCurrentLocation.getLongitude());
                onLocationChanged(mCurrentLocation);
            }
            startLocationUpdate();
        }catch (SecurityException ex){

        }
    }
    @SuppressLint("MissingPermission")
    public Location getCurrentLocation(){
        try {
            if(mCurrentLocation==null)
                return LocationServices
                        .FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            else
                return mCurrentLocation;
        }catch (SecurityException ex){
            Log.e("Location", "startLocationUpdate: ",ex );
        }
        catch (Exception ex){
            Log.e("Location", "startLocationUpdate: ",ex );
        }
        return null;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callConnection();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mGoogleApiClient !=null && mGoogleApiClient.isConnected()){
            startLocationUpdate();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mGoogleApiClient != null){
            stopLocationUpdate();
        }
    }

    LocationRequest mLocationRequest;
    protected synchronized void callConnection(){
        try {
            if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
                Log.i("LOG", "callConnection()");
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addOnConnectionFailedListener(this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            if(!mGoogleApiClient.isConnecting())
                mGoogleApiClient.connect();
        }catch (Exception ex){
            Log.e("callConnection", "startLocationUpdate: ",ex );
        }

    }

    private int locationInterval = 5000;
    private int locationFastestInterval = 2000;
    private int locationNumUpdates = 1;
    private int locationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;

    public int getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(int locationInterval) {
        this.locationInterval = locationInterval;
    }

    public int getLocationFastestInterval() {
        return locationFastestInterval;
    }

    public void setLocationFastestInterval(int locationFastestInterval) {
        this.locationFastestInterval = locationFastestInterval;
    }

    public int getLocationNumUpdates() {
        return locationNumUpdates;
    }

    public void setLocationNumUpdates(int locationNumUpdates) {
        this.locationNumUpdates = locationNumUpdates;
    }

    public int getLocationPriority() {
        return locationPriority;
    }

    public void setLocationPriority(int locationPriority) {
        this.locationPriority = locationPriority;
    }

    protected void initLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(locationInterval);
        mLocationRequest.setFastestInterval(locationFastestInterval);
        mLocationRequest.setNumUpdates(locationNumUpdates);
        mLocationRequest.setPriority(locationPriority);
    }


    protected void startLocationUpdate(){
        try {
            if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
                initLocationRequest();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
        catch (SecurityException ex){
            Log.e("Location", "startLocationUpdate: ",ex );
        }
        catch (Exception ex){
            Log.e("Location", "startLocationUpdate: ",ex );
        }
    }

    protected void stopLocationUpdate(){
        try {
            if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }

        }catch (Exception ex){
            Log.e("Location", "stopLocationUpdate: ",ex );
        }
    }
}
