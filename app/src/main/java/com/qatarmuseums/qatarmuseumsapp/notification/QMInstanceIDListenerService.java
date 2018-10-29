package com.qatarmuseums.qatarmuseumsapp.notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class QMInstanceIDListenerService extends FirebaseInstanceIdService {

    public static final String TAG = "NotificationTag";


    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }



}
