//===================================================================
// MeetingTemplate.java
// 	Description:
// 		This email holds the structure for a meeting
// 		invitation.
//===================================================================

package com.socialvagrancy.msworkflows.structure;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MeetingTemplate
{
	private String subject;
	private String body;
	private String start_time;
	private String end_time;
	private String timezone;
	private String meeting_room;
	private ArrayList<String> attendees;
	private ArrayList<String> variables;

	public MeetingTemplate()
	{
		attendees = new ArrayList<String>();
	}

	//=======================================
	// Getters
	//=======================================
	public ArrayList<String> attendees() { return attendees; }
	public String body() { return body; }
	public String end() { return end_time; }
	public String room() { return meeting_room; }
	public String subject() { return subject; }
	public String start() { return start_time; }
	public String timezone()
	{
		// If timezone isn't set, use system default.
		if(timezone == null || timezone.equals("{local}"))
		{
			ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

			return now.getZone().getDisplayName(TextStyle.FULL, Locale.getDefault());
		}
		else
		{
			return timezone;
		}
	}
	public ArrayList<String> variables() { return variables; }

	//=======================================
	// Setters
	//=======================================
	public void addAttendee(String name) { attendees.add(name); }
	public void setAttendees(ArrayList<String> list) { attendees = list; }
	public void setBody(String text) { body = text; }
	public void setEnd(String time) { end_time = time; }
	public void setMeetingRoom(String room) { meeting_room = room; }
	public void setSubject(String text) { subject = text; }
	public void setStart(String time) { start_time = time; }
	public void setTimezone(String zone) { timezone = zone; }
	public void updateAttendee(int index, String name)
	{
		attendees.remove(index);
		attendees.add(index, name);
	}
}
