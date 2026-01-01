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
import com.socialvagrancy.connector.o365.util.ReplaceVariables;
import com.socialvagrancy.connector.o365.util.graph.Graph;
import com.socialvagrancy.utils.io.FileManager;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		Scanner cin = new Scanner(System.in);

	    template = FileManager.readFile(template_path);

		email = gson.fromJson(template, EmailTemplateModel.class);

		// Prompt for sender email address
		System.out.print("Sender email address: ");
		String senderEmail = cin.nextLine();

		Map<String, List<String>> var_map = userInputVariablesAsList(email.getVariables());

		email.setToRecipients(ReplaceVariables.processTextToList(email.getToRecipients(), var_map));
		email.setCcRecipients(ReplaceVariables.processTextToList(email.getCcRecipients(), var_map));
		email.setBccRecipients(ReplaceVariables.processTextToList(email.getBccRecipients(), var_map));
		email.setBody(ReplaceVariables.processText(email.getBody(), var_map));

		if(email != null)
		{
			graph.sendEmail(senderEmail, email.getSubject(), email.getBody(), email.getToRecipients(), email.getCcRecipients(), email.getBccRecipients());

		    log.info("Successfully sent email.");
        }
	}

	public static void fromTemplateWithoutPrompts(String senderEmail, String template_path, Map<String, List<String>> var_map, Graph graph) throws Exception {
		log.info("Composing email based on template: " + template_path);

		Gson gson = new Gson();
		String template;
		EmailTemplateModel email;

	    template = FileManager.readFile(template_path);

		email = gson.fromJson(template, EmailTemplateModel.class);

		email.setToRecipients(ReplaceVariables.processTextToList(email.getToRecipients(), var_map));
		email.setCcRecipients(ReplaceVariables.processTextToList(email.getCcRecipients(), var_map));
		email.setBccRecipients(ReplaceVariables.processTextToList(email.getBccRecipients(), var_map));
		email.setBody(ReplaceVariables.processText(email.getBody(), var_map));

		if(email != null) {
		    log.info("Sending email {} to {} from {}", email.getSubject(), email.getToRecipients(), senderEmail);
            graph.sendEmail(senderEmail, email.getSubject(), email.getBody(), email.getToRecipients(), email.getCcRecipients(), email.getBccRecipients());
		    log.info("Email {} was sent to {} from {}", email.getSubject(), email.getToRecipients(), senderEmail);
        }
	}

	//=======================================
	// Private Functions
	//=======================================

	private static Map<String, List<String>> userInputVariablesAsList(List<String> var_list) {
	    Map<String, List<String>> var_map = new HashMap<String, List<String>>();
        Scanner cin = new Scanner(System.in);
		String choice;

		for(int i=0; i<var_list.size(); i++) {
			System.out.print(var_list.get(i) + " (comma-separated for lists): ");
			choice = cin.nextLine();

			// Split by comma to support list values
			List<String> values = new ArrayList<String>();
			if(choice.contains(",")) {
				String[] parts = choice.split(",");
				for(String part : parts) {
					values.add(part.trim());
				}
			} else {
				values.add(choice);
			}

			var_map.put(var_list.get(i), values);
		}

		return var_map;
	}
}
