//===================================================================
// EmailTemplate.java
// 	Description:
// 		This variable holds the email template information 
// 		to send an email.
//===================================================================

package com.socialvagrancy.msworkflows.structure;

import java.util.ArrayList;

public class EmailTemplate
{
	ArrayList<String> to_recipient;
	ArrayList<String> cc_recipient;
	ArrayList<String> bcc_recipient;
	String subject;
	String body;
	ArrayList<String> attachments;
	ArrayList<String> variables;

	public EmailTemplate()
	{
		to_recipient = new ArrayList<String>();
		cc_recipient = new ArrayList<String>();
		bcc_recipient = new ArrayList<String>();
		attachments = new ArrayList<String>();
		variables = new ArrayList<String>();
	}

	//=======================================
	// Getters
	//=======================================
	
	public ArrayList<String> toRecipients() { return to_recipient; }
	public ArrayList<String> ccRecipients() { return cc_recipient; }
	public ArrayList<String> bccRecipients() { return bcc_recipient; }
	public String subject() { return subject; }
	public String body() { return body; }
	public ArrayList<String> attachments() { return attachments; }
	public ArrayList<String> variables() { return variables; }
	public int toRecipientCount() { return to_recipient.size(); }

	//=======================================
	// Setters
	//=======================================
	
	public void addAttachment(String path) { attachments.add(path); }
	public void addToRecipient(String email) { to_recipient.add(email); }	
	public void addCcRecipient(String email) { cc_recipient.add(email); }
	public void addBccRecipient(String email) { bcc_recipient.add(email); }
	public void addVariable(String v) { variables.add(v); }
	public void setBody(String b) { body = b; }
	public void setSubject(String s) { subject = s; }
	public void setToRecipients(ArrayList<String> r) { to_recipient = r; }
	public void setCcRecipients(ArrayList<String> r) { cc_recipient = r; }
	public void setBccRecipients(ArrayList<String> r) { bcc_recipient = r; }
	public void updateToRecipient(int index, String email) 
	{ 
		to_recipient.remove(index);
		to_recipient.add(index, email);
	}
}
