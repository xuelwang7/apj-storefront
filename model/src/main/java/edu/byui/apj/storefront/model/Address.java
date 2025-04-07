package edu.byui.apj.storefront.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Data;

@Data

public class Address {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String addressLine1;
    String addressLine2;
    String city;
    String state;
    String zipCode;
    String country;
}
