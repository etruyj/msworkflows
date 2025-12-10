//===================================================================
// SendEmail.java
// 	Description:
// 		This command sends a form email in the form of a
// 		template. This script will load an email template
// 		parse for required variables and then send the email
// 		to the targeted users.
//
// Created by etruyj
//===================================================================

package com.socialvagrancy.connector.o365.command;

import com.socialvagrancy.connector.o365.model.EmailTemplateModel;
import com.socialvagrancy.connector.o365.util.graph.Graph;
import com.socialvagrancy.utils.io.FileManager;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendEmail {
    private static final Logger log = LoggerFactory.getLogger(SendEmail.class);

	public static void fromTemplateWithPrompts(String template_path, Graph graph) throws Exception {
		log.info("Composing email based on template: " + template_path);

		Gson gson = new Gson();
		String template;
		EmailTemplateModel email;

	    template = FileManager.readFile(template_path);

		email = gson.fromJson(template, EmailTemplateModel.class);

		System.err.println("subject: " + email.subject());

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
