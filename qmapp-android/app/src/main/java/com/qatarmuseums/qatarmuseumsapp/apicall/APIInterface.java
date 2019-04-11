package com.qatarmuseums.qatarmuseumsapp.apicall;


import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.culturepass.LoginData;
import com.qatarmuseums.qatarmuseumsapp.culturepass.TokenForPushNotification;
import com.qatarmuseums.qatarmuseumsapp.detailspage.RegistrationDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.education.Events;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilitiesDetailModel;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactDetails;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.home.UserRegistrationModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsList;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.NMoQParkListDetails;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQPark;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkList;
import com.qatarmuseums.qatarmuseumsapp.park.ParkList;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.profile.RsvpData;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsModel;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuideStarterModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("{language}/mobile_api/gethomeList.json")
    Call<ArrayList<HomeList>> getMuseumsList(@Path("language") String language);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<CommonModel>> getCommonPageList(@Path("language") String language,
                                                   @Path("pageName") String pageName);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<CommonModel>> getCommonPageListWithID(@Path("language") String language,
                                                         @Path("pageName") String pageName,
                                                         @Query("museum_id") String museumId);

    @GET("{language}/mobile_api/getpublicartdetail.json")
    Call<ArrayList<PublicArtModel>> getPublicArtsDetails(@Path("language") String language,
                                                         @Query("nid") String nid);

    @GET("{language}/mobile_api/{pageName}")
    Call<ArrayList<HeritageOrExhibitionDetailModel>> getHeritageOrExhibitionDetails(@Path("language") String language,
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


    @GET("{language}/mobile_api/museum-detail.json")
    Call<ArrayList<MuseumAboutModel>> getMuseumAboutDetails(@Path("language") String language,
                                                            @Query("nid") String nid);

    @GET("{language}/mobile_api/new_ws_educations.json")
    Call<ArrayList<Events>> getCalendarData(@Path("language") String language,
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

    @GET("{language}/mobile_api/detail_of_collection_tourguide.json")
    Call<ArrayList<ArtifactDetails>> getObjectSearchDetails(@Path("language") String language,
                                                            @Query("artifact_number") String artifactId);

    @GET("{language}/mobile_api/collection_by_tour_guide.json")
    Call<ArrayList<ArtifactDetails>> getArtifactList(@Path("language") String language);

    @POST("{language}/mobile_api/user/token.json")
    Call<ProfileDetails> generateToken(@Path("language") String language, @Body LoginData loginData);

    @POST("{language}/mobile_api/user/login.json")
    Call<ProfileDetails> login(@Path("language") String language, @Header("X-CSRF-Token") String token,
                               @Body LoginData loginData);

    @POST("{language}/mobile_api/user/logout.json")
    Call<UserData> logout(@Path("language") String language, @Header("X-CSRF-Token") String token);


    @POST("{language}/mobile_api/push_notifications.json")
    Call<Void> sendTokenToServer(@Path("language") String language, @Header("X-CSRF-Token") String token,
                                 @Body TokenForPushNotification tokenForPushNotification);

    @POST("{language}/mobile_api/user/request_new_password.json")
    Call<ArrayList<String>> forgotPassword(@Path("language") String language,
                                           @Header("X-CSRF-Token") String token, @Body LoginData loginData);

    @GET("{language}/mobile_api/user/{uid}.json")
    Call<UserData> getRSVP(@Path("language") String language, @Path("uid") String uid,
                           @Header("X-CSRF-Token") String token);

    @PUT("{language}/mobile_api/user/{uid}.json")
    Call<UserData> setRSVP(@Path("language") String language, @Path("uid") String uid,
                           @Header("X-CSRF-Token") String token, @Body RsvpData rsvpData);

    @GET("{language}/mobile_api/nmoq_banner_banner.json")
    Call<ArrayList<HomeList>> getBannerDetails(@Path("language") String language);

    @GET("{language}/mobile_api/nmoq_about_page.json")
    Call<ArrayList<MuseumAboutModel>> getLaunchMuseumAboutDetails(@Path("language") String language,
                                                                  @Query("nid") String nid);

    @GET("{language}/mobile_api/nmoq_list_day.json")
    Call<ArrayList<CommonModel>> getTourList(@Path("language") String language);

    @GET("{language}/mobile_api/list_facility_category.json")
    Call<ArrayList<CommonModel>> getFacilityList(@Path("language") String language);

    @GET("{language}/mobile_api/nmoq_list_partner.json")
    Call<ArrayList<CommonModel>> getTravelData(@Path("language") String language);

    @GET("{language}/mobile_api/nmoq_special_event.json")
    Call<ArrayList<CommonModel>> getSpecialEvents(@Path("language") String language);

    @GET("{language}/mobile_api/list_tour_per_day.json")
    Call<ArrayList<TourDetailsModel>> getTourDetails(@Path("language") String language,
                                                     @Query("event_id") String evetId);

    @GET("{language}/mobile_api/facility-detail_by_category.json")
    Call<ArrayList<FacilitiesDetailModel>> getFacilityDetails(@Path("language") String language,
                                                              @Query("category_id") String facilityId);

    @GET("{language}/mobile_api/user_registeration_event.json")
    Call<ArrayList<UserRegistrationModel>> getUserRegistrationDetails(@Path("language") String language,
                                                                      @Query("uid") String userId);

    @DELETE("en/mobile_api/entity_registration/{registration_id}.json")
    Call<String> deleteEventRegistration(@Path("registration_id") String registrationId,
                                         @Header("X-CSRF-Token") String token);

    @POST("en/mobile_api/entity_registration.json")
    Call<RegistrationDetailsModel> eventRegistration(@Header("Content-Type") String content,
                                                     @Header("X-CSRF-Token") String token,
                                                     @Body RegistrationDetailsModel model);

    @PUT("en/mobile_api/entity_registration/{registration_id}.json")
    Call<RegistrationDetailsModel> eventRegistrationCompletion(@Header("Content-Type") String content,
                                                               @Header("X-CSRF-Token") String token,
                                                               @Path("registration_id") String registrationId,
                                                               @Body RegistrationDetailsModel model);

    @GET("{language}/mobile_api/nmoq_category.json")
    Call<ArrayList<NMoQPark>> getNMoQPark(@Path("language") String language);

    @GET("{language}/mobile_api/nmoq_list_parks.json")
    Call<ArrayList<NMoQParkList>> getNMoQParkList(@Path("language") String language);

    @GET("{language}/mobile_api/nmoq_list_playground_by_park.json")
    Call<ArrayList<NMoQParkListDetails>> getNMoQParkListDetails(@Path("language") String language,
                                                                @Query("nid") String nid);

}
