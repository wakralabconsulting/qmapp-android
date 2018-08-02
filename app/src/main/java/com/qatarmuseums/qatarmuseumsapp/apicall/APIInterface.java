package com.qatarmuseums.qatarmuseumsapp.apicall;

import com.qatarmuseums.qatarmuseumsapp.home.HomeList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {
    @GET("{language}/mobile_api/gethomeList.json")
    Call<ArrayList<HomeList>> getHomepageDetails(@Path("language") String language);
}
