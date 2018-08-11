package com.qatarmuseums.qatarmuseumsapp.apicall;



import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
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

//    @GET("{language}/mobile_api/getpublicartdetail.json?nid={id}")
//    Call<ArrayList<CommonModel>> getPublicArtsDetails(@Path("language") String language,
//                                                      @Path("id") int id);

    @GET("{language}/mobile_api/getpublicartdetail.json")
    Call<ArrayList<PublicArtModel>> getPublicArtsDetails(@Path("language") String language,
                                                         @Query("nid") String nid);

    @GET("{language}/mobile_api/heritage_detail_Page.json")
    Call<ArrayList<HeritageDetailModel>> getHeritageDetails(@Path("language") String language,
                                                            @Query("nid") String nid);

}
