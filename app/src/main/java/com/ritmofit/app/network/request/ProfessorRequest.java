package com.ritmofit.app.network.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfessorRequest {
    @SerializedName("name")
    public String name;
    @SerializedName("gender")
    public String gender;

    @SerializedName("classTypes")
    public List<String> classTypes;
    @SerializedName("taughtClasses")
    public List<Object> taughtClasses = null;
}
