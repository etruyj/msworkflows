//===================================================================
// TemplateMeeting.java
// 	Description:
// 		This class creates a meeting from a template meeting
// 		invite.
//===================================================================

package com.socialvagrancy.connector.o365.command;

import com.socialvagrancy.connector.o365.model.MeetingTemplateModel;
import com.socialvagrancy.connector.o365.util.ReplaceVariables;
import com.socialvagrancy.connector.o365.util.graph.Graph;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TemplateMeeting
{
	public static boolean loadAndSend(String template, Graph graph, Logger log) throws Exception
	{
		log.info("Building meeting invitation from template: " + template);

		Gson gson = new Gson();
		MeetingTemplateModel meeting;
		String meeting_json;

		try
		{
			meeting_json = FileManager.readFile(template);
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			throw new Exception("Unable to open meeting template " + template);
		}

		try
		{
			meeting = gson.fromJson(meeting_json, MeetingTemplateModel.class);
		}
		catch(JsonParseException e)
		{
			log.error(e.getMessage());
			throw new Exception("Unable to parse meeting template " + template);
		}

		if(meeting != null)
		{
			// Fill in the template.
			log.info("Querying user for variable definitions.");
			HashMap<String, String> var_map = ReplaceVariables.userInputVariables(meeting.variables());

			meeting.setSubject(ReplaceVariables.inText(meeting.subject(), var_map));
			meeting.setBody(ReplaceVariables.inText(meeting.body(), var_map));
			meeting.setStart(ReplaceVariables.inText(meeting.start(), var_map));
			meeting.setEnd(ReplaceVariables.inText(meeting.end(), var_map));

			meeting.setAttendees(ReplaceVariables.inArrayList(meeting.attendees(), var_map));

			try
			{
				log.info("Creating meeting invite: " + meeting.subject());

				log.debug("BODY: " + meeting.body());
				graph.createEventMessage(meeting.subject(), meeting.body(), meeting.start(), meeting.end(), "Eastern Standard Time", meeting.room(), meeting.attendees());

				return true;
			}
			catch(Exception e)
			{
				log.error(e.getMessage());
				throw new Exception("Failed to send email.");
			}
		}
		
		log.error("No meeting information was found in the template.");
		return false;

	}

}
