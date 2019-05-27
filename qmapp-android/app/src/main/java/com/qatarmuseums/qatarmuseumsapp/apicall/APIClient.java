package com.qatarmuseums.qatarmuseumsapp.apicall;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class APIClient {
    public static String apiBaseUrl = "https://www.qm.org.qa/";
    private static final String HOST = "qm.org.qa";
    private static final String PUBLIC_KEY_HASH = "sha256/wiI3qA5+usYoQ7MpluMNIqvfN+EdSv7GxO5ZnQTOflQ=";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

            // Enable the Network Logs
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // SSL Pinning - for API security
            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add(HOST, PUBLIC_KEY_HASH)
                    .build();

            // Force the connection to use only TLS v.1.2 avoiding the fallback to older version to avoid vulnerabilities
            final ConnectionSpec.Builder connectionSpec =
                    new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS);
            connectionSpec.tlsVersions(TlsVersion.TLS_1_2).build();

            // Enable TLS specific version V.1.2
            TLSSocketFactory tlsSocketFactory;

            try {
                tlsSocketFactory = new TLSSocketFactory();
                httpBuilder.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.systemDefaultTrustManager());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                Timber.w("Failed to create Socket connection ");
                e.printStackTrace();
            }


            OkHttpClient client = httpBuilder
                    .addInterceptor(interceptor)
                    .certificatePinner(certificatePinner)
                    .connectionSpecs(Collections.singletonList(connectionSpec.build()))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
