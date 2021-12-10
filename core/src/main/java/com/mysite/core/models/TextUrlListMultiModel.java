package com.mysite.core.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Getter
@Setter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextUrlListMultiModel {

    @Inject
    private String text;

    @Inject
    private String url;

    @Inject
    @Named("nestedmodellist/.")
    private List<com.mysite.core.models.TextUrlNestedListModel> nestedListModels;
}
