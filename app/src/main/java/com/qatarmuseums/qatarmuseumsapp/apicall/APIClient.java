package com.qatarmuseums.qatarmuseumsapp.apicall;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static String apiBaseUrl = "https://www.qm.org.qa/";

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // SSL Pinning - for API security
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("www.qm.org.qa", "sha256/k+i4Cxf8v2rkPvB5i3UnjN7PnxZIOmU81OBeFR29db0=")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .certificatePinner(certificatePinner)
                .build();

        return new Retrofit.Builder()
                .baseUrl(apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
