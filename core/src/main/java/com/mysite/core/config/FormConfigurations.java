package com.mysite.core.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(
    label = "Form Configurations",
    description = "Form and Email Configurations")
public @interface FormConfigurations {

    @Property(label = "Email Subject", description = "Email Subject")
    String emailSubject();

    @Property(label = "From Email", description = "From Email")
    String fromEmail();

    @Property(label = "Internal Emails", description = "Internal Emails")
    String[] toEmails();

    @Property(label = "Catalog Endpoint", description = "Catalog Endpoint")
    String catalogEndpoint();

    @Property(label = "Name of Email address field", description = "Name of Email address field")
    String submitterEmailFieldName();

    @Property(label = "Confirmation Email Body", description = "Confirmation Email Body")
    String confirmationEmailBody() default StringUtils.EMPTY;

    @Property(label = "Path of Email template for internal email", description = "Path of Email template for internal email")
    String internalEmailTemplatePath() default StringUtils.EMPTY;

    @Property(label = "Path of Email template for confirmation email sent to submitter", description = "Path of Email template for confirmation email sent to submitter")
    String confirmationEmailTemplatePath() default StringUtils.EMPTY;

    @Property(label = "Confirmation Email Subject", description = "Confirmation Email Subject")
    String confirmationEmailSubject() default StringUtils.EMPTY;

    @Property(label = "Allowed File Extensions", description = "Specify comma separated list, like .pdf,.zip")
    String[] allowedFileExtensions() default {".pdf"};

    @Property(label = "Allowed File Content types", description = "Specify comma separated list, like application/pdf, application/zip")
    String[] allowedFileContentTypes() default {"application/pdf"};

    @Property(label = "File Threshold in MB", description = "Specify upload file threshold size for form")
    int fileThresholdInMB() default 10;

    @Property(label = "Target Groups Array", description = "Target Groups Array")
    String[] formSubmissionTargetGroups() default StringUtils.EMPTY;

    @Property(label = "Text field regex field", description = "Java String regex to whitelist incoming form characters.")
    String textFieldRegexString() default "^[-a-zA-Z0-9.,;_@=%: /()!$£*+{}?|]+$";

}
