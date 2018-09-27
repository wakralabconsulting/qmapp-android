package com.qatarmuseums.qatarmuseumsapp.apicall;


import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEvents;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.education.EducationEvents;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsList;
import com.qatarmuseums.qatarmuseumsapp.park.ParkList;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuideStarterModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("{language}/mobile_api/gethomeList.json")
    Call<ArrayList<HomeList>> getHomepageDetails(@Path("language") String language);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<CommonModel>> getCommonpageList(@Path("language") String language,
                                                   @Path("pageName") String pageName);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<CommonModel>> getCommonpageListWithID(@Path("language") String language,
                                                         @Path("pageName") String pageName,
                                                         @Query("museum_id") String museumId);

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

    @GET("{language}/mobile_api/museum_collection_category.json")
    Call<ArrayList<CommonModel>> getCollectionList(@Path("language") String language,
                                                       @Query("museum_id") String museumId);

    @GET("{language}/geturl.php")
    Call<ArrayList<CalendarEvents>> getCalendarDetails(@Path("language") String language,
                                                       @Header("date") Long currentTimestamp,
                                                       @Header("inst") String institution,
                                                       @Header("age") String ageGroup,
                                                       @Header("ptype") String programType);

    @GET("{language}/mobile_api/museum-detail.json")
    Call<ArrayList<MuseumAboutModel>> getMuseumAboutDetails(@Path("language") String language,
                                                            @Query("nid") String nid);

    @GET("{language}/geturl.php")
    Call<ArrayList<EducationEvents>> getEducationCalendarDetails(@Path("language") String language,
                                                                 @Header("date") Long currentTimestamp,
                                                                 @Header("inst") String institution,
                                                                 @Header("age") String ageGroup,
                                                                 @Header("ptype") String programType);

     @GET("{language}/mobile_api/collection_ws.json")
    Call<ArrayList<CollectionDetailsList>> getMuseumCollectionDetails(@Path("language") String language,
                                                                      @Query("category") String category);
    @GET("{language}/mobile_api/tour_guide_list_museums.json")
    Call<ArrayList<SelfGuideStarterModel>> getSelfGuideStarterPageDetails(@Path("language") String language,
                                                                          @Query("museum_id") String museum_id);

}
