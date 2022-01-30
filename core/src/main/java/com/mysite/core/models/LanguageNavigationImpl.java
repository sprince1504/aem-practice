package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, resourceType = { LanguageNavigationImpl.RESOURCE_TYPE })
public class LanguageNavigationImpl {

	public static final String RESOURCE_TYPE = "project/components/languagenavigation";
	public static final String NAVIGATION_ROOT =  "navigationToot";
	public static final String STRUCTURE_DEPTH =  "structureDepth";
	public static final String PATH_SEPERATOR =  "/";
	public static final String DEFAULT_NAVIGATION_ROOT =  "/content/project";
	

	@Self
	private SlingHttpServletRequest request;

	@ScriptVariable
	private Page currentPage;

	@ScriptVariable
	private ValueMap properties;

	@ScriptVariable
	private Style currentStyle;

	private int structureDepth;
	
	Resource rootPage;

	@Inject
	List<NavigationItem> items;

	@PostConstruct
	private void initModel() {
		String navDefaultCurrentStyle = this.currentStyle.get(NAVIGATION_ROOT, String.class);
		String navigationRoot = this.properties.get(NAVIGATION_ROOT, navDefaultCurrentStyle);
		int depthDefaultToCurrStyle = this.currentStyle.get(STRUCTURE_DEPTH, 1);
		this.structureDepth = this.properties.get(STRUCTURE_DEPTH, depthDefaultToCurrStyle);
		rootPage = request.getResourceResolver().getResource(DEFAULT_NAVIGATION_ROOT);
		if(StringUtils.isNotEmpty(navigationRoot)) {
			rootPage = request.getResourceResolver().getResource(navigationRoot);
		}
	}

	public String getLocalePageTitle() {
		return currentPage.getAbsoluteParent(this.structureDepth).getTitle().toUpperCase();
	}

	public List<NavigationItem> getLanguageItems() {
		List<NavigationItem> languageItems = items;
		while(!languageItems.isEmpty()) {
			languageItems=getNavItems(languageItems);
		}
		
       return languageItems;
	}
	
	public List<NavigationItem> getCountriesListItems() {
		List<NavigationItem> countriesList = new ArrayList<>();

		for (NavigationItem item : items) {
			if (item.getPath().contains(PATH_SEPERATOR + currentPage.getAbsoluteParent(this.structureDepth-2).getName() + PATH_SEPERATOR)) {				
				countriesList.addAll(item.getChildren());				
			}
		}

		return countriesList;
	}
	

	public List<NavigationItem> getNavItems(List<NavigationItem> navitems) {
		List<NavigationItem> navItems = new ArrayList<>();

		for (NavigationItem item : navitems) {
			if (item.getPath().contains(PATH_SEPERATOR + currentPage.getAbsoluteParent(this.structureDepth).getName() + PATH_SEPERATOR)) {				
				navItems.addAll(item.getChildren());				
			}
		}

		return navItems;
	}

}
