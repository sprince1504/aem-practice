package com.mysite.core.models;

import com.mysite.core.services.GraphqlConnection;
import com.mysite.core.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductModel {
    private static final Logger LOG = LoggerFactory.getLogger(ProductModel.class);

    @SlingObject
    Resource resource;

    @SlingObject
    ResourceResolver resourceResolver;

    @SlingObject
    SlingHttpServletRequest slingRequest;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String imageLinkTo;

    @ValueMapValue
    private String productSku;

    @ValueMapValue
    private String textAlignment;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String price;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String buttonLabel;

    @ValueMapValue
    private String buttonLinkTo;

    @ValueMapValue
    private String backgroundColor;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String textColor;

    @ValueMapValue
    private String preTitle;

    @ValueMapValue
    private String titleType;

    @ValueMapValue
    private String titleStyle;

    @ValueMapValue
    private String customBgColor;

    @OSGiService
    GraphqlConnection graphqlConnection;

    public List<ProductResultModel> getProductSearchResult() throws IOException, JSONException {
        List<ProductResultModel> productSearchList = new ArrayList<>();
        JSONObject jsonproducts = graphqlConnection.createConnection(getProductInfo());
        JSONArray items = jsonproducts.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            ProductResultModel product = new ProductResultModel();
            product.setName(items.getJSONObject(i).getString("name"));
            product.setPricevalue(items.getJSONObject(i).getJSONObject("price_range").getJSONObject("minimum_price").getJSONObject("regular_price").getDouble("value"));
            product.setCurrency(items.getJSONObject(i).getJSONObject("price_range").getJSONObject("minimum_price").getJSONObject("regular_price").getString("currency"));
            product.setImageurl(items.getJSONObject(i).getJSONObject("image").getString("url"));
            product.setShortdescription(items.getJSONObject(i).getJSONObject("short_description").getString("html"));
            productSearchList.add(product);
        }
        return productSearchList;
    }

    public String getProductInfo() {
        String prod = "\\\"" + productSku + "\\\"";
        return "{\"query\": \"query" + "{ products( search: " + prod + ") { items { name sku url_key image{url} short_description{html} url_rewrites {url} price_range { minimum_price {regular_price {value currency}}} }} }\"}";
    }
}
