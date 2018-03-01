package com.aakash.siege;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.annotation.Nonnull;

public class MainActivity extends saveSP {

    EditText Mobile,Password;
    Button skip,newUser,login,google,facebook;
    String MobilePattern = "[0-9]{10}",uname1,pass1,respass,id;
    Location mLastLocation;
    static Integer GAC_READY = 0;
    GoogleApiClient googleApiClient = null;

    GoogleSignInClient mGoogleSignInClient;

    public static final String TAG = "MainActivity";
    public static final String GEOFENCE_ID ="MyGeofenceId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1234);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG,"Connected to GoogleApiClient");
                        GAC_READY = 1;
                        if(saveSP.getoffLocation(MainActivity.this).length() != 0)
                        {
                            String defpref = saveSP.getoffLocation(MainActivity.this);
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<String>>() {}.getType();
                            ArrayList<String> locList = gson.fromJson(defpref, type);
                            Log.d("tag","Location"+locList);
                            for(int i=0;i<1;i++){
                                Double latval,lonval;
                                String id;
                                String[] locval = locList.get(i).split(",");
                                latval = Double.parseDouble(locval[0]);
                                lonval = Double.parseDouble(locval[1]);
                                id = locval[2];
                                startGeofenceMonitoring(latval,lonval,id);
                            }
                        }
//
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG,"Suspended to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener(){

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG,"Failed to connect to GoogleApiClient - "+connectionResult.getErrorMessage());
                    }
                })
                .build();

        if(saveSP.getUserName(MainActivity.this).length() == 0) {
            setContentView(R.layout.activity_main);

            Mobile = (EditText) findViewById(R.id.mobile);
            Password = (EditText) findViewById(R.id.passw);
            skip = (Button) findViewById(R.id.skip);
            newUser = (Button) findViewById(R.id.newuser);
            login = (Button) findViewById(R.id.login);
            google = (Button) findViewById(R.id.google);
            facebook = (Button) findViewById(R.id.facebook);


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    uname1 = Mobile.getText().toString().trim();
                    pass1 = Password.getText().toString().trim();
                    if (!Mobile.getText().toString().matches(MobilePattern)) {
                        Mobile.setError("Enter 10 digit number");
                        Mobile.requestFocus();


                    } else if (Password.getText().toString().trim().length() == 0) {
                        Password.setError("Enter Password");
                        Password.requestFocus();
                    } else {

                        callQuery(uname1);

                    }

                }
            });

            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.google:
                            signIn();
                            break;
                        // ...
                    }



                }
            });






            newUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                }
            });
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NavActivity.class);
                    startActivity(intent);
                }
            });

        }else
        {
            Intent intent = new Intent(MainActivity.this, NavActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void signIn() {

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("tag","Permission Results");
        if(GAC_READY == 1)
        startLocationMonitoring();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


        private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);

                // Signed in successfully, show authenticated UI.
                updateUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }







    @Override
    protected void onResume() {
        Log.d(TAG,"onResume called");
        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS){
            Log.d(TAG,"Google play service not available");
            GoogleApiAvailability.getInstance().getErrorDialog(this,response,1).show();
        }else {
            Log.d(TAG,"Google play service is available");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);

        googleApiClient.reconnect();
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account != null){
            Intent intent = new Intent(MainActivity.this, NavActivity.class);
            finish();
            startActivity(intent);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
    private void startLocationMonitoring() {


        Log.d(TAG, "startLocation called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(mLastLocation != null)
            setLocation(MainActivity.this,mLastLocation.getLatitude(),mLastLocation.getLongitude());
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(mLastLocation != null)
                    setLocation(MainActivity.this,location.getLatitude(),location.getLongitude());
                }


            });
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityException" + e.getMessage());
        }
    }

    public void startGeofenceMonitoring(final Double lat, final Double lon, final String id) {
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(lat, lon,100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();
            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            if (!googleApiClient.isConnected()) {
                Log.d(TAG,"GoogleApi is not connected");
            }else {
                LocationServices.GeofencingApi.addGeofences(googleApiClient,geofenceRequest,pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG,"Successfully added geofence"+lat+","+lon+","+id);
                                }else {
                                    Log.d(TAG,"Failed to add geofence" + status.getStatus());
                                }
                            }
                        });
            }

        }catch (SecurityException e)
        {
            Log.d(TAG, "SecurityException" + e.getMessage());
        }
    }

    void callQuery(String mono){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        UserQuery userQuery = UserQuery.builder()
                .mobno(mono)
                .build();
        apolloClient.query(userQuery)
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(new ApolloCall.Callback<UserQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<UserQuery.Data> response) {

                UserQuery.Data data = response.data();
                if(data.user().size() == 0){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Log.d("tag", "Resdata" + data);
                    respass = data.user.get(0).password;
                    id = data.user.get(0).id.toString();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pass1.equals(respass.trim())) {
                                if (id.length() != 0)
                                    setUserName(MainActivity.this, id);
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, NavActivity.class);
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("Fail", "onFailure: ",e );
            }
        });
    }
}
