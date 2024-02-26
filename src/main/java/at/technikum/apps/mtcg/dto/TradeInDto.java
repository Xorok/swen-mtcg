package at.technikum.apps.mtcg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradeInDto {
    @JsonProperty(value = "CardToTrade", required = true)
    private String offeredCardId;
    @JsonProperty(value = "Type", required = true)
    private String requiredType;
    @JsonProperty(value = "Element", required = false)
    private String requiredElement;
    @JsonProperty(value = "MinimumDamage", required = false)
    private Double requiredMinDamage;

    public TradeInDto() {
    }

    public String getOfferedCardId() {
        return offeredCardId;
    }

    public void setOfferedCardId(String offeredCardId) {
        this.offeredCardId = offeredCardId;
    }

    public String getRequiredType() {
        return requiredType;
    }

    public void setRequiredType(String requiredType) {
        this.requiredType = requiredType;
    }

    public String getRequiredElement() {
        return requiredElement;
    }

    public void setRequiredElement(String requiredElement) {
        this.requiredElement = requiredElement;
    }

    public Double getRequiredMinDamage() {
        return requiredMinDamage;
    }

    public void setRequiredMinDamage(Double requiredMinDamage) {
        this.requiredMinDamage = requiredMinDamage;
    }
}
