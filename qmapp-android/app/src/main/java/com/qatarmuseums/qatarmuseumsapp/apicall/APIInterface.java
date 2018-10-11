package com.qatarmuseums.qatarmuseumsapp.apicall;


import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEvents;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.education.EducationEvents;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsList;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewModel;
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

    @GET("{language}/mobile_api/new_ws_educations.json")
    Call<ArrayList<CalendarEvents>> getCalendarDetails(@Path("language") String language,
                                                                 @Query("institution") String institution,
                                                                 @Query("age") String ageGroup,
                                                                 @Query("programe") String programType,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][month]") String month,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][day]") String day,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][year]") String year,
                                                                 @Query("cck_multiple_field_remove_fields") String cckValue);


    @GET("{language}/mobile_api/museum-detail.json")
    Call<ArrayList<MuseumAboutModel>> getMuseumAboutDetails(@Path("language") String language,
                                                            @Query("nid") String nid);

    @GET("{language}/mobile_api/new_ws_educations.json")
    Call<ArrayList<EducationEvents>> getEducationCalendarDetails(@Path("language") String language,
                                                                 @Query("institution") String institution,
                                                                 @Query("age") String ageGroup,
                                                                 @Query("programe") String programType,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][month]") String month,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][day]") String day,
                                                                 @Query("field_eduprog_repeat_field_date_value[value][year]") String year,
                                                                 @Query("cck_multiple_field_remove_fields") String cckValue);


    @GET("{language}/mobile_api/collection_ws.json")
    Call<ArrayList<CollectionDetailsList>> getMuseumCollectionDetails(@Path("language") String language,
                                                                      @Query("category") String category);
    @GET("{language}/mobile_api/tour_guide_list_museums.json")
    Call<ArrayList<SelfGuideStarterModel>> getSelfGuideStarterPageDetails(@Path("language") String language,
                                                                          @Query("museum_id") String museum_id);
    @GET("{language}/mobile_api/collection_by_tour_guide.json")
    Call<ArrayList<ArtifactDetails>> getObjectPreviewDetails(@Path("language") String language,
                                                                       @Query("tour_guide_id") String tourGuideId);

    @GET("{language}/mobile_api/collection_by_tour_guide.json")
    Call<ArrayList<ArtifactDetails>> getArtifactList(@Path("language") String language);

}
