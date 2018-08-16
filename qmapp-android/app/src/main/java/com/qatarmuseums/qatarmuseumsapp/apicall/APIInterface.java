package com.qatarmuseums.qatarmuseumsapp.apicall;


import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.park.ParkList;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("{language}/mobile_api/gethomeList.json")
    Call<ArrayList<HomeList>> getHomepageDetails(@Path("language") String language);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<CommonModel>> getCommonpageList(@Path("language") String language,
                                                   @Path("pageName") String pageName);

    @GET("{language}/mobile_api/getpublicartdetail.json")
    Call<ArrayList<PublicArtModel>> getPublicArtsDetails(@Path("language") String language,
                                                         @Query("nid") String nid);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<HeritageOrExhibitionDetailModel>> getHeritageOrExhebitionDetails(@Path("language") String language,
                                                                                    @Path("pageName") String pageName,
                                                                                    @Query("nid") String nid);

    @GET("{language}/mobile_api/getDiningdetail.json")
    Call<ArrayList<DiningDetailModel>> getDiningDetails(@Path("language") String language,
                                                        @Query("nid") String nid);

    @GET("{language}/mobile_api/park_service_combined.json")
    Call<ArrayList<ParkList>> getParkDetails(@Path("language") String language);

}
