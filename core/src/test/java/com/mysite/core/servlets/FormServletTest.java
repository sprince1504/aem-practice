package com.mysite.core.servlets;


import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
class FormServletTest {
    private FormServlet underTest;
    Method testPopulateEmailAttributesFromCAConfig;

    private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Mock
    Resource resource;

    @Mock
    Page page;

    @Mock
    private ConfigurationBuilder configurationBuilder;

    @InjectMocks
    org.apache.commons.fileupload.servlet.ServletFileUpload servletFileUpload;

    RequestParameter[] requestParameters;

    @Mock
    RequestParameter rp;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    SlingHttpServletRequest mockRequest;


    byte[] fileBytesMaxAllowed = new byte[1000000];
    byte[] fileBytesAbove = new byte[1000001];

    @Mock
    RequestParameterMap value;


    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        underTest = new FormServlet();

        context.load().json("/com.techdata.core.models/actionItems.json", "/action");


    }

    @Test
    void testGettingInternalEmailBasedOnFormKey() throws InvocationTargetException, IllegalAccessException {

        Method internalEmailAddressMethod;

        try {
            internalEmailAddressMethod = underTest.getClass().getDeclaredMethod("getInternalEmailAddressArray", Map.class, String[].class, Map.class);
            internalEmailAddressMethod.setAccessible(true);
            Map<String, String> testParameterMapWithKey = new HashMap<String, String>();
            Map<String, String> testParameterMapWithoutKey = new HashMap<String, String>();
            Map<String, String[]> testGroupEmailParameterMap = new HashMap<String, String[]>();
            testParameterMapWithKey.put(":group", "apac");
            testParameterMapWithoutKey.put(":group", "no-key");

            testGroupEmailParameterMap.put("apac", new String[]{"test@test.com"});
            testGroupEmailParameterMap.put("hk", new String[]{"hk@test.com"});
            String[] defaultAddressArray = new String[]{"default@defailt.com"};
            String[] response1 = (String[]) internalEmailAddressMethod.invoke(underTest, testParameterMapWithKey, defaultAddressArray, testGroupEmailParameterMap);
            String[] response2 = (String[]) internalEmailAddressMethod.invoke(underTest, testParameterMapWithoutKey, defaultAddressArray, testGroupEmailParameterMap);
            assertTrue(response1.length > 0);
            assertTrue(response2.length > 0);
            assertEquals("test@test.com", response1[0]);
            assertEquals("default@defailt.com", response2[0]);


        } catch (NoSuchMethodException ignored) {
        }
    }


        @Test
        void testGettingGeneratingGroupMap() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Method underTestMethod;
            underTestMethod = underTest.getClass().getDeclaredMethod("getMapOfEmailAddress", String[].class);
            underTestMethod.setAccessible(true);
            String[] arrayFromCA = new String[] {"apac|test@gmail.com,apac@apac.com", "hk|hk@hk.com"};
            String[] arrayFromCA2 = new String[] {"apac&test@gmail.com,apac@apac.com", "hk&hk@hk.com"};

            Map<String, String[]> mapFromMethod = (Map<String, String[]>) underTestMethod.invoke(underTest, new Object[] {arrayFromCA});
            String[] val = mapFromMethod.get("apac");
            assertNotNull(val);
            assertEquals("test@gmail.com", val[0]);
            Map<String, String[]> mapFromMethod2 = (Map<String, String[]>) underTestMethod.invoke(underTest, new Object[] {arrayFromCA2});
            String[] val2 = mapFromMethod2.get("apac");
            assertNull(val2);

        }

    @Test
    void testPopulateEmailAttributesFromCAConfig() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] arrayFromCA = new String[] {"apac|test@gmail.com,apac@apac.com", "hk|hk@hk.com"};
        String[] defaultAddressArray = new String[]{"default@defailt.com"};
        String[] allowedFileTypesArray = new String[]{".pdf", ".gif"};
        Map<String, String[]> emailParams = new HashMap<>();

        Method underTestMethod;
    }

    @Test
    void testIsValidFileInEmailRequestInvalidFileType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {


        List<String> allowedFileTypesMockValues = new ArrayList<>();
        allowedFileTypesMockValues.add(".pdf");
        int thresholdFileSizeMockValue = 1;

        Field allowedFileTypes = underTest.getClass().getDeclaredField("allowedFileExtensions");
        allowedFileTypes.setAccessible(true);
        allowedFileTypes.set(underTest, allowedFileTypesMockValues);

        Field thresholdFileSize = underTest.getClass().getDeclaredField("thresholdFileSize");
        thresholdFileSize.setAccessible(true);
        thresholdFileSize.set(underTest, thresholdFileSizeMockValue);

        MockSlingHttpServletRequest mockRequest = context.request();
        mockRequest.addRequestParameter("file",fileBytesMaxAllowed, "application/pdf", "somefile.zip");

        Method underTestMethod;
        underTestMethod = underTest.getClass().getDeclaredMethod("isValidFileInEmailRequest", SlingHttpServletRequest.class);
        underTestMethod.setAccessible(true);

        assertFalse((boolean) underTestMethod.invoke(underTest, mockRequest));

    }

        @Test
        void testIsValidFileInEmailRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            for (int i = 0;  i < 1000000; i++) {
                fileBytesMaxAllowed[i] = 'a';
                fileBytesAbove[i] = 'a';
            }

        List<String> allowedFileTypesMockValues = new ArrayList<>();
        allowedFileTypesMockValues.add(".pdf");
        int thresholdFileSizeMockValue = 1;

        List<String> allowedFileContentTypesMockValue = new ArrayList<>();
        allowedFileContentTypesMockValue.add("application/pdf");

        Field allowedFileTypes = underTest.getClass().getDeclaredField("allowedFileExtensions");
        allowedFileTypes.setAccessible(true);
        allowedFileTypes.set(underTest, allowedFileTypesMockValues);

        Field thresholdFileSize = underTest.getClass().getDeclaredField("thresholdFileSize");
        thresholdFileSize.setAccessible(true);
        thresholdFileSize.set(underTest, thresholdFileSizeMockValue);

        Field allowedFileContentTypes = underTest.getClass().getDeclaredField("allowedFileContentTypes");
        allowedFileContentTypes.setAccessible(true);
        allowedFileContentTypes.set(underTest, allowedFileContentTypesMockValue);

        MockSlingHttpServletRequest mockRequest = context.request();
        mockRequest.addRequestParameter("file",fileBytesMaxAllowed, "application/pdf", "somefile.pdf");

        Method underTestMethod;
        underTestMethod = underTest.getClass().getDeclaredMethod("isValidFileInEmailRequest", SlingHttpServletRequest.class);
        underTestMethod.setAccessible(true);

        assertTrue((boolean) underTestMethod.invoke(underTest, mockRequest));

    }

    @Test
    void testIsValidFileInEmailRequestWithLargeFile() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        fileBytesAbove[1000000] = 'a';
        MockSlingHttpServletRequest mockRequest = context.request();
        mockRequest.addRequestParameter("file",fileBytesAbove, "application/pdf", "somefile.pdf");

        Method underTestMethod;
        underTestMethod = underTest.getClass().getDeclaredMethod("isValidFileInEmailRequest", SlingHttpServletRequest.class);
        underTestMethod.setAccessible(true);

        assertFalse((boolean) underTestMethod.invoke(underTest, mockRequest));

    }

    @Test
    void testDoPost() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {

        String[] allowedFileTypesArray = new String[]{".pdf", ".gif"};
        String[] allowedFileContentTypesMockValue = new String[]{"application/pdf"};
        String[] emails = new String[]{"test@techdata.com"};
        String[] groups = new String[]{"author"};


        when(mockRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(any())).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        when(page.adaptTo(ConfigurationBuilder.class)).thenReturn(configurationBuilder);
        Mockito.when(resource.adaptTo(Page.class)).thenReturn(page);
        when(page.adaptTo(ConfigurationBuilder.class)).thenReturn(configurationBuilder);

        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getContentType()).thenReturn("multipart/form-data");
        underTest.doPost(mockRequest, response);
    }


}