package com.user.service.dto;

public class PromotionDto {
    private Long id;
    private String title;
    private String description;
    private Integer costPoints;
    private String imageUrl;
    private String tierLevel;
    private Boolean active;

    public PromotionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCostPoints() { return costPoints; }
    public void setCostPoints(Integer costPoints) { this.costPoints = costPoints; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTierLevel() { return tierLevel; }
    public void setTierLevel(String tierLevel) { this.tierLevel = tierLevel; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
