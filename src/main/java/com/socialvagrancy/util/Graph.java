//===================================================================
// Graph.java
// 	Description:
// 		Implements the MS Graph SDK
//===================================================================

package com.socialvagrancy.msworkflows.util;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.UserCollectionPage;

import okhttp3.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class Graph
{
	private static Properties _properties;
	private static DeviceCodeCredential _deviceCodeCredential;
	private static GraphServiceClient<Request> _userClient;

	public static void initialize(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception
	{
		if(properties == null)
		{
			throw new Exception("Properties must be set.");
		}

		_properties = properties;

		final String clientId= properties.getProperty("app.clientId");
		final String authTenantId = properties.getProperty("app.authTenant");
		final List<String> graphUserScopes = Arrays.asList(properties.getProperty("app.graphUserScopes").split(","));
	
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
	}
}
