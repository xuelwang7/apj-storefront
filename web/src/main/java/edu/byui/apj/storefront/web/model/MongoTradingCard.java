package edu.byui.apj.storefront.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MongoTradingCard {
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("specialty")
    private String specialty;
    @JsonProperty("contribution")
    private String contribution;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("imageUrl")
    private String imageUrl;
}