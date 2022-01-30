package com.mysite.core.servlets;


import com.adobe.acs.commons.email.EmailService;
import com.day.cq.wcm.api.Page;
import com.mysite.core.config.FormConfigurations;
import com.mysite.core.config.ServiceEndPointsConfiguration;
import com.mysite.core.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                "service.description=Form Submission Servlet",
                "service.vendor=project.com",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourcetypes=project/components/tdpostform",

        }
)
public class FormServlet extends SlingAllMethodsServlet {
        private static final Logger LOG = LoggerFactory.getLogger(FormServlet.class);
        private static final String FILE_PARAM_NAME = "file";
        private static final String CONFIRMATION_EMAIL_BODY_PARAM_NAME = "confirmationEmailBody";
        private static final String CONFIRMATION_EMAIL_SUBJECT_PARAM_NAME = "confirmationSubject";
        private static final String INTERNAL_EMAIL_SUBJECT_PARAM_NAME = "internalEmailSubject";
        private static final int ONE_MB_IN_BYTES = 1000000;
        public static final String A_TO_Z_LOWERCASE = "a-z";
        @Reference

        private transient EmailService emailService;
        private String[] toEmailAddresses;
        private String submitterEmailFieldName = StringUtils.EMPTY;
        private String internalEmailTemplatePath = StringUtils.EMPTY;
        private String confirmationEmailTemplatePath = StringUtils.EMPTY;
        private Map<String, String[]> formSubmissionTargetGroups;
        private int thresholdFileSize = 10;
        private List<String> allowedFileExtensions = new ArrayList<>();
        private List<String> allowedFileContentTypes = new ArrayList<>();
        private String textFieldRegexExpr;

        @Override
        protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
                PrintWriter out = response.getWriter();
                ResourceResolver resourceResolver = request.getResourceResolver();
                Resource resource = resourceResolver.getResource(Constants.PROJECT_CONTENT_PAGE_ROOT);
                Page page = resource.adaptTo(Page.class);
                ServiceEndPointsConfiguration serviceEndPointsConfiguration =
                        page.adaptTo(ConfigurationBuilder.class).as(ServiceEndPointsConfiguration.class);
                out.print(serviceEndPointsConfiguration.downloadInvoiceEndpoint());
        }

        @Override
        protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
                Map<String, String> emailParams = new HashMap<>();
                ResourceResolver resourceResolver = request.getResourceResolver();
                Map<String, DataSource> attachments = new HashMap<>();
                try
                {
                        final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);

                        if (isMultipart) {
                                LOG.info("Received Multi-part form data for processing");
                                Resource resource = resourceResolver.getResource(Constants.PROJECT_CONTENT_PAGE_ROOT);
                                FormConfigurations formConfigurations = getCAConfigFormEmailObject(resource);
                                if(formConfigurations != null) {
                                        thresholdFileSize = formConfigurations.fileThresholdInMB();
                                        allowedFileExtensions = Arrays.asList(formConfigurations.allowedFileExtensions());
                                        allowedFileContentTypes = Arrays.asList(formConfigurations.allowedFileContentTypes());
                                        textFieldRegexExpr = formConfigurations.textFieldRegexString();
                                        // adding carriage-return/new-line/backslash chars to regex
                                        textFieldRegexExpr = textFieldRegexExpr.replace(A_TO_Z_LOWERCASE, EXTRA_REGEX_CHARS + A_TO_Z_LOWERCASE);
                                        prepareEmailRequestFromFormData(request.getRequestParameterMap(), attachments, emailParams);
                                        populateEmailAttributesFromCAConfig(formConfigurations, emailParams);
                                        if (isValidFileInEmailRequest(request)) {
                                                sendEmailWithFormData(toEmailAddresses, emailParams, submitterEmailFieldName,
                                                        internalEmailTemplatePath, confirmationEmailTemplatePath, attachments);
                                        } else {
                                                LOG.info("Ignored:: Wrong form/email request with invalid upload file size or extension..");
                                                sendEmailWithFormData(toEmailAddresses, emailParams, submitterEmailFieldName,
                                                        internalEmailTemplatePath, confirmationEmailTemplatePath, null);
                                        }
                                }
                        }
                }
                catch (IOException e) {
                        LOG.error("403, Exception occurred during form submission", e);
                }
        }

        private FormConfigurations getCAConfigFormEmailObject(Resource resource) {
                if(resource == null || resource.adaptTo(Page.class) == null) return null;
                Page page = resource.adaptTo(Page.class);
                return page.adaptTo(ConfigurationBuilder.class).as(FormConfigurations.class);
        }

        private boolean isValidFileInEmailRequest(SlingHttpServletRequest request) {
                RequestParameter requestParameter = request.getRequestParameter(FILE_PARAM_NAME);
                if(requestParameter == null || StringUtils.isEmpty(requestParameter.getFileName())) {
                        return Boolean.TRUE;
                }
                String fileExtension = requestParameter.getFileName().substring(requestParameter.getFileName().lastIndexOf("."));
                int thresholdFileSizeInBytes = thresholdFileSize * ONE_MB_IN_BYTES;
                // if incoming file less than threshold and not in allowed file types then ignore the request
                if(requestParameter.getSize() > thresholdFileSizeInBytes || !allowedFileExtensions.contains(fileExtension)
                        || !allowedFileContentTypes.contains(requestParameter.getContentType())) {
                        LOG.error("Skipped form and email processing as attachment file size or extension/content-type is invalid!!");
                        LOG.error("Incoming file size {}, extension {} and content type {}.",
                                thresholdFileSizeInBytes, fileExtension, requestParameter.getContentType());
                        return Boolean.FALSE;
                }
                return Boolean.TRUE;
        }

        private Map<String, String[]> getMapOfEmailAddress(String[] groupEmailArray)
        {
                Map<String, String[]> groupEmailMap = new HashMap<>();
                for(String eachGroup : groupEmailArray)
                {
                        String[] groupInfo = eachGroup.split(Constants.PIPE_REGEX_ESCAPED);
                        if (groupInfo.length > 1)
                        {
                                groupEmailMap.put(groupInfo[0], groupInfo[1].split(Constants.COMMA));
                        }else{
                                LOG.error("Error when creating Group Email for {}", eachGroup);
                        }
                }
                return groupEmailMap;

        }

        private void prepareEmailRequestFromFormData(Map<String, RequestParameter[]> params,
                                                     Map<String, DataSource> attachments, Map<String, String> emailParams) throws IOException {
                for (final Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
                        final String key = pairs.getKey();
                        final RequestParameter[] pArr = pairs.getValue();
                        StringBuilder value = new StringBuilder();

                        if (key.equals(FILE_PARAM_NAME))
                        {
                                handleFileParameterProcessing(pArr, attachments);
                        }else{
                                handleNonFileParameterProcessing(pArr, key, emailParams, value);
                        }

                }
        }

        private void handleNonFileParameterProcessing(RequestParameter[] pArr,
                                                      String key, Map<String, String> emailParams,
                                                      StringBuilder value) throws IOException {
                value.append(pArr[0].toString());
                if (pArr.length > 1)
                {
                        for (RequestParameter rp : pArr)
                        {
                                value.append(value.toString()).append(rp.toString()).append("|");
                        }
                }
                LOG.debug("key is {}, value is {}",key, value);
                emailParams.put(key, validateString(value.toString()));
        }

        public String validateString(String input) throws IOException {
                if(StringUtils.isEmpty(input)) return input;
                final Pattern pattern = Pattern.compile(textFieldRegexExpr);
                if (!pattern.matcher(input).find()) {
                        throw new IOException("Invalid form field, skipping the form and email submission.");
                }
                return input;
        }

        private void handleFileParameterProcessing(RequestParameter[] pArr, Map<String, DataSource> attachments) throws IOException {
                RequestParameter fileRequestParameter = pArr[0];

                LOG.debug("file input parameter found. Name is {} ", fileRequestParameter.getFileName());
                if (StringUtils.isNotEmpty(fileRequestParameter.getFileName()) && fileRequestParameter.getInputStream() != null )
                {
                        InputStream file = fileRequestParameter.getInputStream();
                        attachments.put(fileRequestParameter.getFileName(), new ByteArrayDataSource(file, fileRequestParameter.getContentType()));
                } else{
                        LOG.error("Error in determining file uploaded");
                }
        }

        private void populateEmailAttributesFromCAConfig(FormConfigurations formConfigurations, Map<String, String> emailParams) {
                        toEmailAddresses = formConfigurations.toEmails();
                submitterEmailFieldName = formConfigurations.submitterEmailFieldName();
                String confirmationEmailBody = formConfigurations.confirmationEmailBody();
                emailParams.put(CONFIRMATION_EMAIL_BODY_PARAM_NAME, confirmationEmailBody);
                internalEmailTemplatePath = formConfigurations.internalEmailTemplatePath();
                confirmationEmailTemplatePath = formConfigurations.confirmationEmailTemplatePath();
                String emailSubject = formConfigurations.emailSubject();
                emailParams.put(INTERNAL_EMAIL_SUBJECT_PARAM_NAME, emailSubject);
                String confirmationEmailSubject = formConfigurations.confirmationEmailSubject();
                formSubmissionTargetGroups = getMapOfEmailAddress(formConfigurations.formSubmissionTargetGroups());
                emailParams.put(CONFIRMATION_EMAIL_SUBJECT_PARAM_NAME, confirmationEmailSubject);
        }

        private void sendEmailWithFormData(String[] toEmailAddresses, Map<String, String> emailParams, String submitterEmailFieldName,
                                           String internalEmailTemplatePath, String confirmationEmailTemplatePath, Map<String, DataSource> attachments) {
                String submitterEmailAddress = emailParams.get(submitterEmailFieldName);
                String[] internalEmailAddresses = getInternalEmailAddressArray(emailParams, toEmailAddresses, formSubmissionTargetGroups);
                if (!internalEmailTemplatePath.isEmpty()) {
                        if (attachments!=null && attachments.size() > 0) {
                                emailService.sendEmail(internalEmailTemplatePath, emailParams, attachments, internalEmailAddresses);
                        } else {
                                LOG.info("No attachments. Sending without attachments");
                                emailService.sendEmail(internalEmailTemplatePath, emailParams, internalEmailAddresses);
                        }
                } else {
                        LOG.error("Cannot send form email. Internal email template path is not set.");
                }

                if (!confirmationEmailTemplatePath.isEmpty() && submitterEmailAddress != null && !submitterEmailAddress.isEmpty()) {
                        String[] submitterEmailAddressArray = submitterEmailAddress.split(Constants.COMMA);
                        emailService.sendEmail(confirmationEmailTemplatePath, emailParams, submitterEmailAddressArray);
                } else {
                        LOG.error("Cannot send confirmation email. Confirmation email template path, or submitter email, or submitter email field name is incorrect, or is not set.");
                }
        }

        private String[] getInternalEmailAddressArray(Map<String, String> emailParams, String[] toEmailAddresses, Map<String, String[]> formSubmissionTargetGroups) {

                String groupKey = emailParams.get(Constants.FORM_GROUP_KEY_FIELD);

                if (groupKey != null)
                {
                        String[] internalAddressArray = formSubmissionTargetGroups.get(groupKey);
                        if (internalAddressArray != null)
                        {
                                return internalAddressArray;
                        }else{
                                return toEmailAddresses;
                        }
                }else{
                        LOG.error("Group Key Not found");
                        return toEmailAddresses;
                }

        }

        // new line, carriage-returns, slashes
        private static final String EXTRA_REGEX_CHARS = "\r\n\\\\";
}
