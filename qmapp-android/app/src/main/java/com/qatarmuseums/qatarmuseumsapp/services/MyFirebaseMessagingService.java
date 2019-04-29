package com.qatarmuseums.qatarmuseumsapp.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.culturepass.LoginData;
import com.qatarmuseums.qatarmuseumsapp.culturepass.TokenForPushNotification;
import com.qatarmuseums.qatarmuseumsapp.home.HomeActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.d("From: %s", remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: %s", remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Timber.e("Exception: %s", e.getMessage());
            }
        }

        // Check if message contains a notification payload.
        else if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Timber.e("push json: %s", json.toString());

        try {
            JSONObject data = json.getJSONObject("app");
            String message = data.getString("alert");
            Timber.e("message: %s", message);
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("ALERT", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                resultIntent.putExtra("MESSAGE", message);
                showNotificationMessage(getApplicationContext(), message, message, resultIntent);

            }
        } catch (JSONException e) {
            Timber.e("Json Exception: %s", e.getMessage());
        } catch (Exception e) {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    private void sendRegistrationToServer(String token) {
        getLoginToken(LocaleManager.getLanguage(this), token);

    }

    private void getLoginToken(String lan, String fireBaseToken) {
        LoginData loginData = new LoginData("", "");
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ProfileDetails> call = apiService.generateToken(lan, loginData);
        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(Call<ProfileDetails> call, Response<ProfileDetails> response) {
                String loginToken;
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        loginToken = response.body().getToken();
                        sendFireBaseToken(loginToken, fireBaseToken, lan);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {

            }
        });
    }

    private void sendFireBaseToken(String loginToken, String fireBaseToken, String lan) {
        Timber.i("sendFireBaseToken()");
        TokenForPushNotification tokenForPushNotification = new TokenForPushNotification(fireBaseToken, "android");
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<Void> call = apiService.sendTokenToServer(lan, loginToken, tokenForPushNotification);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Timber.i("sendFireBaseToken() - isSuccessful");

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("sendFireBaseToken() - onFailure: %s", t.getMessage());
            }
        });
    }
}
