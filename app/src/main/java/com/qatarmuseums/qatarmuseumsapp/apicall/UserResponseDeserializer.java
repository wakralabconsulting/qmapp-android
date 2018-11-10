package com.qatarmuseums.qatarmuseumsapp.apicall;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;

import java.lang.reflect.Type;

public class UserResponseDeserializer implements JsonDeserializer<ProfileDetails> {
    @Override
    public ProfileDetails deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (((JsonObject) json).get("field_date_of_birth") instanceof JsonObject){
            return new Gson().fromJson(json, Model.class);
        } else {
            return new Gson().fromJson(json, ResponseArray.class);
        }

    }
}