//===================================================================
// TemplateEmail.java
// 	Description:
// 		This command sends a form email in the form of a
// 		template. This script will load an email template
// 		parse for required variables and then send the email
// 		to the targeted users.
//===================================================================

package com.socialvagrancy.msworkflows.command;

import com.socialvagrancy.msworkflows.structure.EmailTemplate;
import com.socialvagrancy.msworkflows.util.graph.Graph;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TemplateEmail
{
	public static void loadAndSend(String template_path, Graph graph, Logger log) throws Exception
	{
		log.info("Composing email based on template: " + template_path);

		Gson gson = new Gson();
		String template;
		EmailTemplate email;

		try
		{
			template = FileManager.readFile(template_path);
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			throw new Exception("Unable to read template file.");
		}

		try
		{
			email = gson.fromJson(template, EmailTemplate.class);

			System.err.println("subject: " + email.subject());
		}
		catch(JsonParseException e)
		{
			log.error(e.getMessage());
			throw new Exception("Unable to parse template file.");
		}

		HashMap<String, String> var_map = userInputVariables(email.variables());

		email.setToRecipients(replaceEmailAddresses(email.toRecipients(), var_map));
		email.setCcRecipients(replaceEmailAddresses(email.ccRecipients(), var_map));
		email.setBccRecipients(replaceEmailAddresses(email.bccRecipients(), var_map));
		email.setBody(replaceVariables(email.body(), var_map));

		if(email != null)
		{
			graph.sendEmail(email.subject(), email.body(), email.toRecipients(), email.ccRecipients(), email.bccRecipients());
	
		}
	}

	//=======================================
	// Private Functions
	//=======================================
	
	private static ArrayList<String> replaceEmailAddresses(ArrayList<String> address_list, HashMap<String, String> var_map)
	{
		// Build out the email list and replace them with variables.

		ArrayList<String> email_list = new ArrayList<String>();

		for(int i=0; i<address_list.size(); i++)
		{
			email_list.add(var_map.get(address_list.get(i).substring(1, address_list.get(i).length()-1)));
		}

		return email_list;
	}

	private static String replaceVariables(String text, HashMap<String, String> var_map)
	{
		for(HashMap.Entry<String, String> variable : var_map.entrySet())
		{
			text = text.replace("{" + variable.getKey() + "}", variable.getValue());
		}	

		return text;
	}
	
	private static HashMap<String, String> userInputVariables(ArrayList<String> var_list)
	{
		HashMap<String, String> var_map = new HashMap<String, String>();
		Scanner cin = new Scanner(System.in);
		String choice;

		for(int i=0; i<var_list.size(); i++)
		{
			System.out.print(var_list.get(i) + ": ");
			choice = cin.nextLine();

			var_map.put(var_list.get(i), choice);
		}

		return var_map;
	}	
}
