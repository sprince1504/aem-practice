package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EnhancedListTest {
    private final AemContext aemContext = new AemContext();
    EnhancedList enhancedList;
    @Mock
    Resource resource;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    PageManager pageManager;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    private List delegateList;
    @Mock
    private Collection<ListItem> cfListItems;
    @Mock
    private ListItem cfListItem;
    java.util.List<String> brandTagsList = new ArrayList<>();
    @Mock
    private Iterator<ListItem> iteratorMock;
    @Mock
    private Page page;
    @Mock
    private ValueMap pageMap;
    String[] cqTags = new String[]{"techdata","aem"};
    String linkItems = "true";
    @BeforeEach
    void setUp() {
        enhancedList = new EnhancedList();
        Field requestField = null;
        try {
            requestField = enhancedList.getClass().getDeclaredField("request");
            requestField.setAccessible(true);
            requestField.set(enhancedList, request);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        Field delegateListField = null;
        try {
            delegateListField = enhancedList.getClass().getDeclaredField("delegateList");
            delegateListField.setAccessible(true);
            delegateListField.set(enhancedList, delegateList);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        brandTagsList.add("techdata");

        Field listItemsField = null;
        try {
            listItemsField = enhancedList.getClass().getDeclaredField("linkItems");
            listItemsField.setAccessible(true);
            listItemsField.set(enhancedList, linkItems);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }
    @Test
    void testgetListItems(){
        Mockito.lenient().when(request.getResource()).thenReturn(resource);
        Mockito.lenient().when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Mockito.lenient().when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        Mockito.lenient().when(delegateList.getListItems()).thenReturn(cfListItems);
        Mockito.lenient().when(cfListItems.size()).thenReturn(1);
        Mockito.lenient().when(cfListItems.iterator()).thenReturn(iteratorMock);
        Mockito.lenient().when(iteratorMock.hasNext()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        Mockito.lenient().when(iteratorMock.next()).thenReturn(cfListItem);
        Mockito.lenient().when(cfListItem.getPath()).thenReturn("path/to/list/item");
        Mockito.lenient().when(pageManager.getPage("path/to/list/item")).thenReturn(page);
        Mockito.lenient().when(page.getProperties()).thenReturn(pageMap);
        Mockito.lenient().when(pageMap.containsKey("cq:tags")).thenReturn(Boolean.TRUE);
        Mockito.lenient().when(pageMap.get("cq:tags",String[].class)).thenReturn(cqTags);
        //assertTrue(linkItems.equals("true"));
        assertEquals(1, enhancedList.getListItems().size());
    }
    @Test
    void testgetcompareTagList(){
        enhancedList.compareTagList(brandTagsList, brandTagsList);
    }
    @Test
    void testgetconvertArrayToList(){
        enhancedList.convertArrayToList(cqTags);
    }
}
