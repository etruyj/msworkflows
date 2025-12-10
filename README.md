# O365 Connector
This is a simple interface to provide programatic access to Microsoft Office 365 via the MS Graph API/SDK. There are two entry points for the this script, shell and programatic. Shell access can be found in ui/O365Shell.java. Programatic access can be done through command/O365Controller.java.

Requirements:  
    - [socialvagrancy.utils](https://github.com/etruyj/sv_utils) version 2.8.0   
    - [Microsoft Entra Admin Center](https://entra.microsoft.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade/quickStartType~/null/sourceType/Microsoft_AAD_IAM) Admin Access

## Configuration

Before running the application, you need to configure OAuth authentication:

1. Copy the example configuration file:
   ```bash
   cp src/main/resources/msworkflows/oAuth.properties.example src/main/resources/msworkflows/oAuth.properties
   ```

2. Edit `oAuth.properties` and fill in your Azure AD application details:
   - `app.clientId` - Your Azure AD application client ID (required)
   - `app.authTenant` - Your Azure AD tenant ID for authentication (required)
   - `app.clientSecret` - Client secret (only if using app-only authentication)
   - `app.tenantId` - Tenant ID (only if using app-only authentication)
   - `app.graphUserScopes` - Comma-separated list of Microsoft Graph scopes

## Arguments:
--command: The task to be performed [ create-meeting | send-email ] 

--template: What template to use for the email/meeting.

## Templates:
Templates are JSON objects that hold the basic format for the different objects.

### Email
The email template has 4 different fields: to_recipients (List), subject, body, and variables. Any of the first three fields can have defined variables in the form of {VARIABLE}. Listing each of the variables without the curly braces in the variables section will have the script prompt for those values when executed from the shell. For example, if the to_recipients is [ "{to_user}" ], variables should include "to_user". This will inform the script to prompt for that value, which will then be replaced in the email body.

```json
{
    "subject": "String",
    "to_recipient": ["String"],
    "body": "String",
    "variables": ["String"]
}
```

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

