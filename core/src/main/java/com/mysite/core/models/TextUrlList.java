package com.mysite.core.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import com.adobe.cq.commerce.core.components.models.product.Product;

@Getter
@Setter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextUrlList {

    @Inject
    @Named("smallmodellist/.")
    private List<TextUrlListMultiModel> smallheaderlist;

}
