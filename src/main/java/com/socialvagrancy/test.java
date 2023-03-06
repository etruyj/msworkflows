//===================================================================
// Just Testing Functionality
//===================================================================

package com.socialvagrancy.msworkflows;

import com.socialvagrancy.msworkflows.util.graph.Graph;
//import com.socialvagrancy.msworkflows.util.Graph;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class test
{
	public static void main(String[] args)
	{
		final Properties oauth_properties = new Properties();

		try
		{
			InputStream input = new FileInputStream("src/main/resources/msworkflows/oAuth.properties");
			oauth_properties.load(input);

//			oauth_properties.load(test.class.getResourceAsStream("oAuth.properties"));
		}
		catch(Exception e)
		{
			System.err.println("Failed to fetch properties");
			System.err.println(e.getMessage());
		}

		initializeGraph(oauth_properties);
	}

	public static void initializeGraph(Properties properties)
	{
		try
		{
			System.err.println(properties.getProperty("app.clientId"));

			Graph.initializeGraphForUserAuth(properties, 
				challenge -> System.out.println(challenge.getMessage()));
		
			System.out.println("Initialized");
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
}
