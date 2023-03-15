# msworkflows
This is a really simple interface to test interaction with MS Graph. The plan is to integrate it with other scripts to automate email and meeting invitations. As this is a simple POC at this point, a scanner is used to take input.

MainClass: com.socialvagrancy.msworkflows.ui.MSWorkflows

Required: socialvagrancy.utils version 1.8.0 - can be downloaded from github and installed into Maven.

## Arguments:
--command: The task to be performed [ create-meeting | send-email ] 

--template: What template to use for the email/meeting.

## Templates:
Templates are JSON objects that hold the basic format for the email.

### Email Fields
subject: String 

to_recipient: Array<String> 
  
body: String 
  
variables: Array<String>

### Meeting Fields
subject: String 
  
body: String 
  
start_time: String 
  
end_time: String 
  
meeting_room: String 
  
attendees: Array<String> 
  
variables: Array<String>

### Variables
In text variables should be enclosed with {} to signify a variable. The variable name should be listed in the JSON without the braces. For example, an email to_recipient would be listed as "{contact:email}", and under variables we'll mark that as a variable to parse with "contact:email".

## Connecting to MS Office
Graph needs to be registered with MS Office. Instructions can be found here: https://learn.microsoft.com/en-us/azure/active-directory-b2c/microsoft-graph-get-started?tabs=app-reg-ga

