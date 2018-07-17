package com.canli.oya.bakingapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Step {

    @SerializedName("id")
    private int stepId;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailUrl;

    public Step(int stepId, String shortDescription, String description, String videoUrl, String thumbnailUrl) {
        this.stepId = stepId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getStepId() {
        return stepId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
