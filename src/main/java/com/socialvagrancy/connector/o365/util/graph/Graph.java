//===================================================================
// Graph.java
// 	Description:
// 		Modification of the Microsoft Graph Java SDK tutorial
// 		provided by microsoft. Designed to handle the SDK calls
// 		to perform the desired functions.
//
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
//
//===================================================================

package com.socialvagrancy.connector.o365.util.graph;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.EventMessage;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Location;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.messages.MessagesRequestBuilder;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import com.microsoft.graph.users.UsersRequestBuilder;
import com.microsoft.kiota.RequestOption;
import com.microsoft.kiota.RequestInformation;
// </ImportSnippet>

public class Graph {
    // <UserAuthConfigSnippet>
    private Properties _properties;
    private DeviceCodeCredential _deviceCodeCredential;
    private GraphServiceClient _userClient;

    //===========================================
    // Constructor & Initialization
    // 	The initializations effectively prep
    // 	graph.
    //===========================================
    public void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String authTenantId = properties.getProperty("app.authTenant");
        final List<String> graphUserScopes = Arrays
            .asList(properties.getProperty("app.graphUserScopes").split(","));

	System.err.println("Client: " + clientId);

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
            .clientId(clientId)
            .tenantId(authTenantId)
            .challengeConsumer(challenge)
            .build();

        final AzureIdentityAuthenticationProvider authProvider =
            new AzureIdentityAuthenticationProvider(_deviceCodeCredential, null, graphUserScopes.toArray(new String[0]));

        _userClient = new GraphServiceClient(authProvider);

	User me = _userClient.me().get();
    }
    // </UserAuthConfigSnippet>

    // <AppOnlyAuthConfigSnippet>
    private TokenCredential _appCredential;
    private GraphServiceClient _appClient;

    public void initializeGraphForAppOnlyAuth(Properties properties) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final String clientSecret = properties.getProperty("app.clientSecret");
        final String certificatePath = properties.getProperty("app.certificatePath");
        final String certificatePassword = properties.getProperty("app.certificatePassword");

        // Determine which authentication method to use based on available properties
        if (certificatePath != null && !certificatePath.isEmpty()) {
            // Use certificate-based authentication
            System.out.println("Initializing with certificate authentication...");
            ClientCertificateCredentialBuilder certBuilder = new ClientCertificateCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId);

            // Check if certificate is PFX or PEM based on file extension
            if (certificatePath.toLowerCase().endsWith(".pfx") || certificatePath.toLowerCase().endsWith(".p12")) {
                // PFX/PKCS12 certificate
                certBuilder.pfxCertificate(certificatePath, certificatePassword != null ? certificatePassword : "");
            } else {
                // PEM certificate
                certBuilder.pemCertificate(certificatePath);
            }

            _appCredential = certBuilder.build();
        } else if (clientSecret != null && !clientSecret.isEmpty()) {
            // Use client secret authentication
            System.out.println("Initializing with client secret authentication...");
            _appCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();
        } else {
            throw new Exception("Either app.clientSecret or app.certificatePath must be configured in oAuth.properties");
        }

        final AzureIdentityAuthenticationProvider authProvider =
            new AzureIdentityAuthenticationProvider(_appCredential, null, "https://graph.microsoft.com/.default");

        _appClient = new GraphServiceClient(authProvider);
    }
    // </AppOnlyAuthConfigSnippet>

    //===========================================
    // Graph Functions
    //===========================================

    public void createEvent(String subject, String invite_body, String start_time, String end_time, String timezone, String meeting_room, ArrayList<String> attendees)
    {
	    //===================================
	    // Event Info
	    //===================================
	    Event event = new Event();
	    event.setSubject(subject);

	    ItemBody body = new ItemBody();
	    body.setContentType(BodyType.Html);
	    body.setContent(invite_body);

	    DateTimeTimeZone start = new DateTimeTimeZone();
	    start.setDateTime(start_time);
	    start.setTimeZone(timezone);
	    event.setStart(start);

	    DateTimeTimeZone end = new DateTimeTimeZone();
	    end.setDateTime(end_time);
	    end.setTimeZone(timezone);
	    event.setEnd(end);

	    Location location = new Location();
	    location.setDisplayName(meeting_room);
	    event.setLocation(location);

	    LinkedList<Attendee> attendee_list = new LinkedList<Attendee>();
	    Attendee attendee;
	    EmailAddress email_address;

	    for(int i=0; i<attendees.size(); i++)
	    {
		    attendee = new Attendee();
		    email_address = new EmailAddress();
		    email_address.setAddress(attendees.get(i));
		    attendee.setEmailAddress(email_address);
		    attendee.setType(AttendeeType.Required);
		    attendee_list.add(attendee);
	    }

	    event.setAttendees(attendee_list);

	    event.setAllowNewTimeProposals(true);

	    //===================================
	    // Create Event On Calendar
	    //===================================

	    _userClient.me().events()
		    .post(event);
    }

    public void createEventMessage(String subject, String invite_body, String start_time, String end_time, String timezone, String meeting_room, ArrayList<String> attendees)
    {
	    //===================================
	    // Event Info
	    //===================================
	    Event event = new Event();
	    event.setSubject(subject);

	    ItemBody body = new ItemBody();
	    body.setContentType(BodyType.Html);
	    System.err.println("BODY: " + invite_body);
	    body.setContent(invite_body);
	    event.setBody(body);

	    DateTimeTimeZone start = new DateTimeTimeZone();
	    start.setDateTime(start_time);
	    start.setTimeZone(timezone);
	    event.setStart(start);

	    DateTimeTimeZone end = new DateTimeTimeZone();
	    end.setDateTime(end_time);
	    end.setTimeZone(timezone);
	    event.setEnd(end);

	    Location location = new Location();
	    location.setDisplayName(meeting_room);
	    event.setLocation(location);

	    LinkedList<Attendee> attendee_list = new LinkedList<Attendee>();
	    Attendee attendee;
	    EmailAddress email_address;

	    for(int i=0; i<attendees.size(); i++)
	    {
		    attendee = new Attendee();
		    email_address = new EmailAddress();
		    email_address.setAddress(attendees.get(i));
		    attendee.setEmailAddress(email_address);
		    attendee.setType(AttendeeType.Required);
		    attendee_list.add(attendee);
	    }

	    event.setAttendees(attendee_list);

	    event.setAllowNewTimeProposals(true);

	    //===================================
	    // Send Event Invite
	    //===================================

	    _userClient.me().events()
		    .post(event);
    }

    // <GetUserTokenSnippet>
    public String getUserToken() throws Exception {
        // Ensure credential isn't null
        if (_deviceCodeCredential == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        final String[] graphUserScopes = _properties.getProperty("app.graphUserScopes").split(",");

        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphUserScopes);

        final AccessToken token = _deviceCodeCredential.getToken(context).block();
        return token.getToken();
    }
    // </GetUserTokenSnippet>

    // <GetUserSnippet>
    public User getUser() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
            .get();
    }
    // </GetUserSnippet>

	public void getEvents() throws Exception {
		if(_userClient == null)
		{
			throw new Exception("Graph has not been initialized for user auth");
		}

		_userClient.me()
			.events()
			.get();
	}

    // <GetInboxSnippet>
    public void getInbox() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        _userClient.me()
            .mailFolders()
            .byMailFolderId("inbox")
            .messages()
            .get();
    }
    // </GetInboxSnippet>

    // <SendMailSnippet>
    public void sendBasicMail(String subject, String body, String recipient) throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        // Create a new message
        final Message message = new Message();
        message.setSubject(subject);
        ItemBody messageBody = new ItemBody();
        messageBody.setContent(body);
        messageBody.setContentType(BodyType.Text);
        message.setBody(messageBody);

        final Recipient toRecipient = new Recipient();
        EmailAddress recipientAddress = new EmailAddress();
        recipientAddress.setAddress(recipient);
        toRecipient.setEmailAddress(recipientAddress);
        message.setToRecipients(List.of(toRecipient));

        // Send the message
        SendMailPostRequestBody requestBody = new SendMailPostRequestBody();
        requestBody.setMessage(message);
        requestBody.setSaveToSentItems(false);

        _userClient.me()
            .sendMail()
            .post(requestBody);
    }

    public void sendEmail(String senderEmail, String subject, String body, List<String> to_list, List<String> cc_list, List<String> bcc_list) throws Exception
    {
	    if(_appClient == null)
	    {
		    throw new Exception("Graph has not been initialized for app-only auth");
	    }

	    //===================================
	    // Message Body
	    //===================================
	    Message message = new Message();
	    message.setSubject(subject);

	    ItemBody messageBody = new ItemBody();
	    messageBody.setContent(body);
	    messageBody.setContentType(BodyType.Text);
	    message.setBody(messageBody);

	    //===================================
	    // Message Headers
	    //===================================
	    // To Recipients
	    //===================================
	    LinkedList<Recipient> to_recipient_list = new LinkedList<Recipient>();
	    Recipient recipient;
	    EmailAddress email_address;

	    for(int i=0; i<to_list.size(); i++)
	    {
	    	recipient = new Recipient();
		email_address = new EmailAddress();
		email_address.setAddress(to_list.get(i));
		recipient.setEmailAddress(email_address);
		to_recipient_list.add(recipient);

	    }

	    message.setToRecipients(to_recipient_list);

	    //===================================
	    // CC Recipients
	    //===================================
	    LinkedList<Recipient> cc_recipient_list = new LinkedList<Recipient>();

	    for(int i=0; i<cc_list.size(); i++)
	    {
		    recipient = new Recipient();
		    email_address = new EmailAddress();
		    email_address.setAddress(cc_list.get(i));
		    recipient.setEmailAddress(email_address);
		    cc_recipient_list.add(recipient);
	    }

	    message.setCcRecipients(cc_recipient_list);

	    //===================================
	    // BCC Recipients
	    //===================================
	    LinkedList<Recipient> bcc_recipient_list = new LinkedList<Recipient>();

	    for(int i=0; i<bcc_list.size(); i++)
	    {
		    recipient = new Recipient();
		    email_address = new EmailAddress();
		    email_address.setAddress(bcc_list.get(i));
		    recipient.setEmailAddress(email_address);
		    bcc_recipient_list.add(recipient);
	    }

	    message.setBccRecipients(bcc_recipient_list);

	    //===================================
	    // Attachments
	    //===================================

	    //===================================
	    // Send Mail
	    //===================================

	    SendMailPostRequestBody requestBody = new SendMailPostRequestBody();
	    requestBody.setMessage(message);
	    requestBody.setSaveToSentItems(true);

	    _appClient.users()
		.byUserId(senderEmail)
		.sendMail()
		.post(requestBody);
    }
    // </SendMailSnippet>

    // <AppOnyAuthConfigSnippet>
    private void ensureGraphForAppOnlyAuth() throws Exception {
        // Ensure _properties isn't null
        if (_properties == null) {
            throw new Exception("Properties cannot be null");
        }

        if (_appCredential == null) {
            // Call the public initialization method if not already initialized
            initializeGraphForAppOnlyAuth(_properties);
        }
    }
    // </AppOnyAuthConfigSnippet>

    // <GetUsersSnippet>
    public void getUsers() throws Exception {
        ensureGraphForAppOnlyAuth();

        _appClient.users()
            .get();
    }
    // </GetUsersSnippet>

    // <MakeGraphCallSnippet>
    public void makeGraphCall() {
        // INSERT YOUR CODE HERE
        // Note: if using _appClient, be sure to call ensureGraphForAppOnlyAuth
        // before using it.
        // ensureGraphForAppOnlyAuth();
    }
    // </MakeGraphCallSnippet>

}
