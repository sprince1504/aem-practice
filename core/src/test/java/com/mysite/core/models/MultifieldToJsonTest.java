package com.mysite.core.models;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class MultifieldToJsonTest {
    @InjectMocks
    MultifieldToJson multifieldToJson;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    private Resource resval;
    @Mock
    private Resource child;
    @Mock
    Node resNode;
    @Mock
    PropertyIterator resProp;
    @Mock
    Property property;
    @Mock
    Value value;
    List<String> propertiesToIgnore;
    @Mock
    Iterator<Resource> children;

    @BeforeEach
    void setUp() {
        propertiesToIgnore = new ArrayList<>();
        propertiesToIgnore.add("jcr:primaryType");
        propertiesToIgnore.add("sling:resourceType");
        propertiesToIgnore.add("jcr:lastModifiedBy");
        propertiesToIgnore.add("jcr:lastModified");
        propertiesToIgnore.add("jcr:createdBy");
        propertiesToIgnore.add("jcr:created");
    }

    @Test
    void testNullgetJsonMulti() throws RepositoryException {
        when(request.getResource()).thenReturn(resval);
        lenient().when(resval.getChild("en")).thenReturn(child);
        multifieldToJson.getJsonMulti();
    }

    @Test
    void testNotNullgetJsonMulti() throws RepositoryException {
        when(request.getResource()).thenReturn(resval);
        Resource resource = mock(Resource.class);
        when(resval.getChild(null)).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(resNode);
        when(resNode.getProperties()).thenReturn(resProp);
        when(resProp.hasNext()).thenReturn(true,false);
        when(resProp.nextProperty()).thenReturn(property);
        when(property.getName()).thenReturn("data");
        when(property.getValue()).thenReturn(value);
        when(resource.hasChildren()).thenReturn(true,false);
        when(resource.listChildren()).thenReturn(children);
        when(children.hasNext()).thenReturn(true,false);
        when(children.next()).thenReturn(resource);
        when(resource.getName()).thenReturn("item");
        multifieldToJson.getJsonMulti();
    }

    @Test
    void testInit(){
        multifieldToJson.init();
    }
}
