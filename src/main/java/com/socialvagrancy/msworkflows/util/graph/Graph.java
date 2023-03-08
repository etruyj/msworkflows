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

package com.socialvagrancy.msworkflows.util.graph;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
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
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.UserCollectionPage;

import okhttp3.Request;
// </ImportSnippet>

public class Graph {
    // <UserAuthConfigSnippet>
    private Properties _properties;
    private DeviceCodeCredential _deviceCodeCredential;
    private GraphServiceClient<Request> _userClient;

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

        final TokenCredentialAuthProvider authProvider =
            new TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential);

        _userClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider)
            .buildClient();

	User me = _userClient.me().buildRequest().get();
    }
    // </UserAuthConfigSnippet>

    //===========================================
    // Graph Functions
    //===========================================

    public void createEvent(String subject, String invite_body, String start_time, String end_time, String timezone, String meeting_room, ArrayList<String> attendees)
    {
	    LinkedList<Option> requestOptions = new LinkedList<Option>();
	    requestOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timezone + "\""));

	    //===================================
	    // Event Info
	    //===================================
	    Event event = new Event();
	    event.subject = subject;
	    
	    ItemBody body = new ItemBody();
	    body.contentType = BodyType.HTML;
	    body.content = invite_body;

	    DateTimeTimeZone start = new DateTimeTimeZone();
	    start.dateTime =  start_time;
	    start.timeZone = timezone;
	    event.start = start;

	    DateTimeTimeZone end = new DateTimeTimeZone();
	    end.dateTime = end_time;
	    end.timeZone = timezone;
	    event.end = end;

	    Location location = new Location();
	    location.displayName = meeting_room;
	    event.location = location;

	    LinkedList<Attendee> attendee_list = new LinkedList<Attendee>();
	    Attendee attendee;
	    EmailAddress email_address;

	    for(int i=0; i<attendees.size(); i++)
	    {
		    attendee = new Attendee();
		    email_address = new EmailAddress();
		    email_address.address = attendees.get(i);
		    attendee.emailAddress = email_address;
		    attendee.type = AttendeeType.REQUIRED;
		    attendee_list.add(attendee);
	    }

	    event.attendees = attendee_list;

	    event.allowNewTimeProposals = true;

	    //===================================
	    // Create Event On Calendar
	    //===================================

	    _userClient.me().events()
		    .buildRequest(requestOptions)
		    .post(event);
    }

    public void createEventMessage(String subject, String invite_body, String start_time, String end_time, String timezone, String meeting_room, ArrayList<String> attendees)
    {
	    LinkedList<Option> requestOptions = new LinkedList<Option>();
	    requestOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timezone + "\""));

	    //===================================
	    // Event Info
	    //===================================
	    Event event = new Event();
	    event.subject = subject;
	    
	    ItemBody body = new ItemBody();
	    body.contentType = BodyType.HTML;
	    System.err.println("BODY: " + invite_body);
	    body.content = invite_body;
	    event.body = body;

	    DateTimeTimeZone start = new DateTimeTimeZone();
	    start.dateTime =  start_time;
	    start.timeZone = timezone;
	    event.start = start;

	    DateTimeTimeZone end = new DateTimeTimeZone();
	    end.dateTime = end_time;
	    end.timeZone = timezone;
	    event.end = end;

	    Location location = new Location();
	    location.displayName = meeting_room;
	    event.location = location;

	    LinkedList<Attendee> attendee_list = new LinkedList<Attendee>();
	    Attendee attendee;
	    EmailAddress email_address;

	    for(int i=0; i<attendees.size(); i++)
	    {
		    attendee = new Attendee();
		    email_address = new EmailAddress();
		    email_address.address = attendees.get(i);
		    attendee.emailAddress = email_address;
		    attendee.type = AttendeeType.REQUIRED;
		    attendee_list.add(attendee);
	    }

	    event.attendees = attendee_list;

	    event.allowNewTimeProposals = true;

	    //===================================
	    // Send Event Invite
	    //===================================

	    _userClient.me().events()
		    .buildRequest(requestOptions)
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
            .buildRequest()
            .select("displayName,mail,userPrincipalName")
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
			.buildRequest()
			.select("subject,body,organizer,attendees")
			.get();
	}

    // <GetInboxSnippet>
    public MessageCollectionPage getInbox() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
            .mailFolders("inbox")
            .messages()
            .buildRequest()
            .select("from,isRead,receivedDateTime,subject")
            .top(25)
            .orderBy("receivedDateTime DESC")
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
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        final Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = new EmailAddress();
        toRecipient.emailAddress.address = recipient;
        message.toRecipients = List.of(toRecipient);

        // Send the message
        _userClient.me()
            .sendMail(UserSendMailParameterSet.newBuilder()
                .withMessage(message)
                .build())
            .buildRequest()
            .post();
    }

    public void sendEmail(String subject, String body, ArrayList<String> to_list, ArrayList<String> cc_list, ArrayList<String> bcc_list) throws Exception
    {
	    if(_userClient == null)
	    {
		    throw new Exception("Graph has not been initialized for user auth");
	    }

	    //===================================
	    // Message Body
	    //===================================
	    Message message = new Message();
	    message.subject = subject;
	    
	    message.body = new ItemBody();
	    message.body.content = body;
	    message.body.contentType = BodyType.TEXT;

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
		email_address.address = to_list.get(i);
		recipient.emailAddress = email_address;
		to_recipient_list.add(recipient);

	    }

	    message.toRecipients = to_recipient_list;

	    //===================================
	    // CC Recipients
	    //===================================
	    LinkedList<Recipient> cc_recipient_list = new LinkedList<Recipient>();
	   
	    for(int i=0; i<cc_list.size(); i++)
	    {
		    recipient = new Recipient();
		    email_address = new EmailAddress();
		    email_address.address = cc_list.get(i);
		    recipient.emailAddress = email_address;
		    cc_recipient_list.add(recipient);
	    } 

	    message.ccRecipients = cc_recipient_list;

	    //===================================
	    // BCC Recipients
	    //===================================
	    LinkedList<Recipient> bcc_recipient_list = new LinkedList<Recipient>();

	    for(int i=0; i<bcc_list.size(); i++)
	    {
		    recipient = new Recipient();
		    email_address = new EmailAddress();
		    email_address.address = bcc_list.get(i);
		    recipient.emailAddress = email_address;
		    bcc_recipient_list.add(recipient);
	    }

	    message.bccRecipients = bcc_recipient_list;

	    //===================================
	    // Attachments
	    //===================================
	    
	    //===================================
	    // Send Mail
	    //===================================
	    
	    
	    _userClient.me()
		.sendMail(UserSendMailParameterSet
				.newBuilder()
				.withMessage(message)
				.withSaveToSentItems(true)
				.build())
		.buildRequest()
		.post();
    }
    // </SendMailSnippet>

    // <AppOnyAuthConfigSnippet>
    private ClientSecretCredential _clientSecretCredential;
    private GraphServiceClient<Request> _appClient;

    private void ensureGraphForAppOnlyAuth() throws Exception {
        // Ensure _properties isn't null
        if (_properties == null) {
            throw new Exception("Properties cannot be null");
        }

        if (_clientSecretCredential == null) {
            final String clientId = _properties.getProperty("app.clientId");
            final String tenantId = _properties.getProperty("app.tenantId");
            final String clientSecret = _properties.getProperty("app.clientSecret");

            _clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();
        }

        if (_appClient == null) {
            final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(
                    List.of("https://graph.microsoft.com/.default"), _clientSecretCredential);

            _appClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
        }
    }
    // </AppOnyAuthConfigSnippet>

    // <GetUsersSnippet>
    public UserCollectionPage getUsers() throws Exception {
        ensureGraphForAppOnlyAuth();

        return _appClient.users()
            .buildRequest()
            .select("displayName,id,mail")
            .top(25)
            .orderBy("displayName")
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
