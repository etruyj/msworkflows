//===================================================================
// Controller.java
// 	Descritption:
// 		This class links the ui/cli elements of the program 
// 		with the underlying functional code. All classes requiring
// 		declaration are declared here and then passed to static
// 		functions via function calls.
//===================================================================

package com.socialvagrancy.msworkflows.ui;

import com.socialvagrancy.msworkflows.command.TemplateEmail;
import com.socialvagrancy.msworkflows.command.TemplateMeeting;
import com.socialvagrancy.msworkflows.util.graph.Graph;
import com.socialvagrancy.utils.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Controller
{
	private Logger log;
	private Graph graph;

	public Controller(String log_location, int log_level, int log_size, int log_count, String authentication_file) throws Exception
	{
		log = new Logger(log_location, log_size, log_count, log_level);

		log.debug("Properties file: " + authentication_file);

		final Properties oAuthProperties = new Properties();
		graph = new Graph();
		
		try
		{
			oAuthProperties.load(new FileInputStream("../resources/" + authentication_file));
		}
		catch(IOException e)
		{
			log.error(e.getMessage());
			throw new Exception("Unable to load OAuth configuration. Verify required information is listed in the " + authentication_file + ".");
		}

		try
		{
			graph.initializeGraphForUserAuth(oAuthProperties,
					challenge -> System.out.println(challenge.getMessage()));
		}
		catch(Exception e)
		{
			log.error("Error initializing MS Graph.");
			log.error(e.getMessage());
			throw new Exception("Unable to connect to MS Office API.");
		}

		log.debug("Successfully connected to MS Graph");
	}

	public void createMeeting(String template_path)
	{
		try
		{
			TemplateMeeting.loadAndSend(template_path, graph, log);
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			System.err.println(e.getMessage());
		}
	}

	public void sendEmail(String template_path)
	{
		try
		{
			TemplateEmail.loadAndSend(template_path, graph, log);
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			System.err.println(e.getMessage());
		}
	}
}
