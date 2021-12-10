package com.mysite.core.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResultModel
{
    private String name;

    private String sku;

    private Image image;

    private String pdpURL;

    private String imageurl;

    private String shortdescription;

    private Double pricevalue;

    private String currency;
}