package com.mysite.core.models;

import com.day.cq.wcm.api.Page;
import com.mysite.core.services.GraphqlConnection;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import javax.jcr.Session;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ProductModelTest {

    private final AemContext aemContext = new AemContext();
    private ProductModel productModel;
    private ProductModel productModel2;

    @Mock
    GraphqlConnection graphqlConnection;

    @Mock
    JSONObject jsonObjects;

    @Mock
    Session session;

    @Mock
    Node node;

    @Mock
    Node node1;

    @Mock
    Page currentPage;

    JSONArray jsonArray = new JSONArray();
    JSONObject product = new JSONObject();
    JSONObject image = new JSONObject();
    JSONObject priceRange = new JSONObject();
    JSONObject minimumPrice = new JSONObject();
    JSONObject regularPrice = new JSONObject();
    JSONObject shortDescription = new JSONObject();


    @BeforeEach
    void setUp() throws Exception {
        aemContext.addModelsForClasses(ProductModel.class);
        aemContext.load().json("/com/mysite/core/Product.json", "/component");
        Resource resource = aemContext.currentResource("/component/product");
        productModel = aemContext.getService(ModelFactory.class).createModel(resource, ProductModel.class);

        product.put("image", image);
        product.put("name", "product");
        product.put("price_range", priceRange);
        priceRange.put("minimum_price", minimumPrice);
        minimumPrice.put("regular_price", regularPrice);
        regularPrice.put("value","599");
        regularPrice.put("currency","USD");
        product.put("short_description", shortDescription);
        shortDescription.put("html","mysite description");
        image.put("url","content/mysite/us/image.jpg");
        jsonArray.put(0,product);
    }

    /*@Test
    void testGetFileReference() throws IOException, JSONException{
        final String expected = "content/mysite/us/image.jpg";
        String actual = productModel.getFileReference();
        assertEquals(expected, actual);
    }

    @Test
    void testGetFileReferenceWithoutImage() throws IOException, JSONException, Exception{
        String fullPath = "/content/mysite/us/en_us/product2";
        aemContext.registerAdapter(ResourceResolver.class, Session.class, session);
        when(session.getNode(fullPath)).thenReturn(node1);
        final String expected = "content/mysite/us/image.jpg";
        when(productModel2.graphqlConnection.createConnection(anyString())).thenReturn(jsonObjects);
        when(jsonObjects.getJSONArray(anyString())).thenReturn(jsonArray);
        String actual = productModel2.getFileReference();
        assertEquals(expected, actual);
    }

    @Test
    void testGetAltText(){
        final String expected = "product Image Text";
        String actual = productModel.getAltText();
        assertEquals(expected, actual);
    }

    @Test
    void testGetImageLinkTo(){

        final String expected = "/productpage/A2300.html";
        String actual = productModel.getImageLinkTo();
        assertEquals(expected, actual);
    }

    @Test
    void testGetProductSku(){
        final String expected = "056909";
        String actual = productModel.getProductSku();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTextAlignment(){
        final String expected = "left";
        String actual = productModel.getTextAlignment();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTitle() throws IOException, JSONException {
        final String expected = "Blender";
        String actual = productModel.getTitle();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTitleWithoutTitle() throws IOException, JSONException, Exception{
        String fullPath = "/content/mysite/us/en_us/product2";
        aemContext.registerAdapter(ResourceResolver.class, Session.class, session);
        when(session.getNode(fullPath)).thenReturn(node1);
        final String expected = "product";
        when(productModel2.graphqlConnection.createConnection(anyString())).thenReturn(jsonObjects);
        when(jsonObjects.getJSONArray(anyString())).thenReturn(jsonArray);
        String actual = productModel2.getTitle();
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrice() throws IOException, JSONException{
        final String expected = "5000";
        String actual = productModel.getPrice();
        assertEquals(expected, actual);
    }

    @Test
    void testGetPriceWithoutPrice() throws IOException, JSONException, Exception{
        String fullPath = "/content/mysite/us/en_us/product2";
        aemContext.registerAdapter(ResourceResolver.class, Session.class, session);
        when(session.getNode(fullPath)).thenReturn(node1);
        final String expected = "$599.0";
        when(productModel2.graphqlConnection.createConnection(anyString())).thenReturn(jsonObjects);
        when(jsonObjects.getJSONArray(anyString())).thenReturn(jsonArray);
        String actual = productModel2.getPrice();
        assertEquals(expected, actual);
    }

    @Test
    void testGetDescription(){
        final String expected = "mysite blender";
        String actual = productModel.getDescription();
        assertEquals(expected, actual);
    }

    @Test
    void testGetButtonLabel(){
        final String expected = "Buy Now";
        String actual = productModel.getButtonLabel();
        assertEquals(expected, actual);
    }

    @Test
    void testGetButtonLinkTo(){
        final String expected = "/productpage/A2300.html";
        String actual = productModel.getButtonLinkTo();
        assertEquals(expected, actual);
    }

    @Test
    void testGetBackgroundColor(){
        final String expected = "mysite-gray";
        String actual = productModel.getBackgroundColor();
        assertEquals(expected, actual);
    }

    @Test
    void testGetText() throws IOException, JSONException{
        final String expected = "mysite blender";
        String actual = productModel.getText();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTextWithoutText() throws IOException, JSONException, Exception{
        String fullPath = "/content/mysite/us/en_us/product2";
        aemContext.registerAdapter(ResourceResolver.class, Session.class, session);
        when(session.getNode(fullPath)).thenReturn(node1);
        final String expected = "mysite description";
        when(productModel2.graphqlConnection.createConnection(anyString())).thenReturn(jsonObjects);
        when(jsonObjects.getJSONArray(anyString())).thenReturn(jsonArray);
        String actual = productModel2.getText();
        assertEquals(expected, actual);
    }

    @Test
    void testGetPreTitle(){
        final String expected = "1960";
        String actual = productModel.getPreTitle();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTitleType(){
        final String expected = "h4";
        String actual = productModel.getTitleType();
        assertEquals(expected, actual);
    }

    @Test
    void testGetTitleStyle(){
        final String expected = "callout";
        String actual = productModel.getTitleStyle();
        assertEquals(expected, actual);
    }

    @Test
    void testGetCustomBgColor(){
        final String expected = "red";
        String actual = productModel.getCustomBgColor();
        assertEquals(expected, actual);
    }*/
}