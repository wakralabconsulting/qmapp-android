package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    private static final String PREF_COOKIES = "PREF_COOKIES";
    // Storing stuff in a database made just for cookies called PREF_COOKIES.
    // Recommend to do this, and don't change this default value.
    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) PreferenceManager
                .getDefaultSharedPreferences(context).getStringSet(PREF_COOKIES, new HashSet<>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}
