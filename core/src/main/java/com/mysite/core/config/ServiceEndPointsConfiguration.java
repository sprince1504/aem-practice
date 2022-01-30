package com.mysite.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(
    label = "Service Endpoint Configuration",
    description = "Configuration can be made per website")
public @interface ServiceEndPointsConfiguration {

    @Property(label = "UI Service Domain", description = "UI Service Domain")
    String uiServiceDomain();

    @Property(label = "Catalog Endpoint", description = "Catalog Endpoint")
    String catalogEndpoint();

    @Property(label = "Authorization Page URL", description = "Authorization Page URL")
    String authorizationPageURL();

    @Property(label = "Login Endpoint", description = "Login Endpoint")
    String loginEndpoint();

    @Property(label = "Ping App Id", description = "Ping App Id")
    String pingAppId();

    @Property(label = "Active Cart Endpoint", description = "Active Cart Endpoint")
    String activeCartEndpoint();

    @Property(label = "My Configurations Endpoint", description = "My Configurations Endpoint")
    String myConfigurationsEndpoint();

    @Property(label = "My Orders Endpoint", description = "My Orders Endpoint")
    String myOrdersEndpoint();

    @Property(label = "My Order Status Endpoint", description = "My Order Status Endpoint")
    String myOrderStatusEndpoint();

    @Property(label = "My Quotes Endpoint", description = "My Quotes Endpoint")
    String myQuotesEndpoint();

    @Property(label = "My Deals Endpoint", description = "My Deals Endpoint")
    String myDealsEndpoint();

    @Property(label = "My Renewals Endpoint", description = "My Renewals Endpoint")
    String myRenewalsEndpoint();

    @Property(label = "Top Items Endpoint", description = "Top Items Endpoint")
    String topItemsEndpoint();

    @Property(label = "Create Quote Endpoint", description = "Create Quote Endpoint")
    String createQuoteEndpoint();

    @Property(label = "Replace Cart Endpoint", description = "Replace Cart Endpoint")
    String replaceCartEndpoint();

    @Property(label = "Cart Details Endpoint", description = "Cart Details Endpoint")
    String cartDetailsEndpoint();

    @Property(label = "Pricing Conditions Endpoint", description = "Pricing Conditions Endpoint")
    String pricingConditionsEndPoint();

    @Property(label = "Order Levels for Quote Details Endpoint", description = "Order Levels for Quote Details Endpoint")
    String orderLevelsForQuotePreviewEndpoint();

    @Property(label = "Estimated Id List Endpoint", description = "Estimated Id List Endpoint")
    String estimatedIdListEndpoint();

    @Property(label = "Estimated Id Details Endpoint", description = "Estimated Id Details Endpoint")
    String estimatedIdDetailsEndpoint();

    @Property(label = "Saved Carts Endpoint", description = "Saved Carts Endpoint")
    String savedCartsEndpoint();

    @Property(label = "Quote Grid Endpoint", description = "Quote Grid Endpoint")
    String quoteGridEndpoint();

    @Property(label = "Order Grid Endpoint", description = "Order Grid Endpoint")
    String orderGridEndpoint();

    @Property(label = "Quote Detail Endpoint", description = "Quote Detail Endpoint")
    String quoteDetailEndpoint();

    @Property(label = "Vendor connection Endpoint", description = "Vendor Connection Endpoint ")
    String vendorConnectionEndpoint();

    @Property(label = "Set Vendor connection Endpoint", description = "Vendor Connection Endpoint ")
    String setVendorConnectionEndpoint();

    @Property(label = "Vendor Disconnect Endpoint", description = "Vendor Disconnect Endpoint ")
    String vendorDisconnectEndpoint();

    @Property(label = "Quotes Preview Endpoint", description = "Quotes Preview Endpoint ")
    String quotesPreviewEndPoint();

    @Property(label = "Quick Quotes Endpoint", description = "Quick Quotes Endpoint ")
    String quickQuoteEndpoint();

    @Property(label = "Quotes Preview Pricing Endpoint", description = "Quotes Preview Pricing Endpoint ")
    String quotesPreviewPricingEndPoint();

    @Property(label = "Account Address Endpoint", description = "Account Address Endpoint")
    String accountAdressEndPoint();

    @Property(label = "Punch Out Endpoint", description = "Punch Out Endpoint")
    String puchOutEndpoint();

    @Property(label = "Configurations Endpoint", description = "Configurations Endpoint ")
    String configurationsEndpoint();

    @Property(label = "Order Detail Endpoint", description = "Order Detail Endpoint")
    String orderDetailEndpoint();

    @Property(label = "Download All Invoices Endpoint", description = "Download All Invoices Endpoint")
    String downloadAllInvoicesEndpoint();

    @Property(label = "Download Individual Invoice Endpoint", description = "Download Individual Invoice Endpoint")
    String downloadInvoiceEndpoint();

    @Property(label = "Get Deals For Endpoint", description = "Get Deals For Endpoint")
    String dealsForEndpoint();

    @Property(label = "Get Action Items Endpoint", description = "Get Action Items Endpoint")
    String actionItemsEndpoint();

    @Property(label = "Get Quote Details XLS Endpoint", description = "Get Quote Details XLS Endpoint")
    String quoteDetailsXLSEndpoint();

    @Property(label = "Download Order Details XLS Endpoint", description = "Download Order Details XLS Endpoint")
    String downloadOrderDetailsEndpoint();

    @Property(label = "Renewals Grid Endpoint", description = "Renewals Grid Endpoint")
    String renewalsGridEndpoint();

     @Property(label = "Renewal Details Line Item Endpoint", description = "Renewal Details Line Item Endpoint")
    String renewalDetailLineItemEndpoint();

}
