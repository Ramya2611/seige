package com.aakash.siege;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by aakash on 7/2/18.
 */

public class GeofenceService extends IntentService {
    public static  final String TAG = "GeofenceService";

    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()){

        }
        else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                addNotification("You are assigned shelter on");
                Log.d(TAG,"Entering geofence -" + requestId);
            }else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                addNotification("Thank you for using Siege");
                Log.d(TAG,"Exiting geofence -" + requestId);
            }
        }
    }

    private void addNotification(String mes){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(
                        "Siege")
                .setContentText(mes)
                .setSmallIcon(R.drawable.common_full_open_on_phone);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
