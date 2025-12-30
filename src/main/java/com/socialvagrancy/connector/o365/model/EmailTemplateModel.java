//===================================================================
// EmailTemplateModel.java
// 	Description:
// 		This variable holds the email template information
// 		to send an email.
//===================================================================

package com.socialvagrancy.connector.o365.model;

import java.util.ArrayList;
import java.util.List;

public class EmailTemplateModel
{
	List<String> to_recipient;
	List<String> cc_recipient;
	List<String> bcc_recipient;
	String subject;
	String body;
	List<String> attachments;
	List<String> variables;

	public EmailTemplateModel()
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

	public List<String> getToRecipients() { return to_recipient; }
	public List<String> getCcRecipients() { return cc_recipient; }
	public List<String> getBccRecipients() { return bcc_recipient; }
	public String getSubject() { return subject; }
	public String getBody() { return body; }
	public List<String> getAttachments() { return attachments; }
	public List<String> getVariables() { return variables; }
	public int getToRecipientCount() { return to_recipient.size(); }

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
	public void setToRecipients(List<String> r) { to_recipient = r; }
	public void setCcRecipients(List<String> r) { cc_recipient = r; }
	public void setBccRecipients(List<String> r) { bcc_recipient = r; }
	public void updateToRecipient(int index, String email) 
	{ 
		to_recipient.remove(index);
		to_recipient.add(index, email);
	}
}
