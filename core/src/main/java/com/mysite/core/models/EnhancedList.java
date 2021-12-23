package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Model(adaptables = SlingHttpServletRequest.class, adapters = List.class, resourceType = EnhancedList.RESOURCE_TYPE)
public class EnhancedList implements List {

    private static final Logger log = LoggerFactory.getLogger(EnhancedList.class);

    public static final String RESOURCE_TYPE = "techdata/components/enhancedlist";
    private static final String PAGE_PROPERTY_CQ_TAGS = "cq:tags";
    @Self
    private SlingHttpServletRequest request;

    @Self
    @Via(type = ResourceSuperType.class)
    List delegateList;

    @ValueMapValue(via="resource", injectionStrategy = InjectionStrategy.OPTIONAL)
    String linkItems;

    @ValueMapValue(via="resource", injectionStrategy = InjectionStrategy.OPTIONAL)
    String[] brandTags;

    @Override
    public Collection<ListItem> getListItems() {
        Collection<ListItem> listOfBrandItems = new ArrayList<>();
        Resource resource = request.getResource();
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        Collection<ListItem> brandListItems = delegateList.getListItems();
        java.util.List<String> brandTagsList = new ArrayList<>();
        if(brandTags != null){
            brandTagsList = convertArrayToList(brandTags);
        }
        for (ListItem brandListItem : brandListItems) {
            log.debug("Inside brandListItem for loop = {}",  brandListItem.getPath());
            Page page = pageManager.getPage(brandListItem.getPath());
            ValueMap pageMap = page.getProperties();
            if(pageMap.containsKey(PAGE_PROPERTY_CQ_TAGS)){
                String[] cqTags = pageMap.get(PAGE_PROPERTY_CQ_TAGS, String[].class);
                java.util.List<String> cqTagsList = convertArrayToList(cqTags);
                EnhancedListItem item = new EnhancedListItem();
                item.setTitle(page.getTitle());
                if(brandTags != null && linkItems.equals("true")){
                    boolean foundMatch = compareTagList(brandTagsList, cqTagsList);
                    if(foundMatch){
                        item.setLink(page.getPath());
                    }
                }else if(linkItems.equals("true")){
                    item.setLink(page.getPath());
                }
                listOfBrandItems.add(item);
            }
        }
        return listOfBrandItems;
    }

    public boolean compareTagList(java.util.List<String> brandTagsList, java.util.List<String> cqTagsList){
        return cqTagsList.containsAll(brandTagsList);
    }

    public java.util.List<String> convertArrayToList(String[] array){
        return Arrays.asList(array);
    }
}