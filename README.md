# O365 Connector
This is a simple interface to provide programatic access to Microsoft Office 365 via the MS Graph API/SDK. There are two entry points for the this script, shell and programatic. Shell access can be found in ui/O365Shell.java. Programatic access can be done through command/O365Controller.java.

This version of the code updates the send email functionality to streamline that process. The instructions in this version of the README will only relate to the email functionality.

Requirements:  
    - [socialvagrancy.utils](https://github.com/etruyj/sv_utils) version 2.8.0   
    - [Microsoft Entra Admin Center](https://entra.microsoft.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade/quickStartType~/null/sourceType/Microsoft_AAD_IAM) Admin Access

## Configuration

Before running the application, you need to configure OAuth authentication. The script assumes it is being executed from within a directory structure, such as the bin/ directory of the script. Authentication with MS Graph is loaded from a files called oAuth.properties. The script expects to find that file in resources/ (../resources/ relative to the execution of the script). A sample oAuth.properties file is located in the src/main/resources/msworkflows/. A copy can be created in the ../resources directory and the information can be provided from your O365 administrator.

1. Copy the example configuration file:
   ```bash
   cp src/main/resources/msworkflows/oAuth.properties.example ../resources/oAuth.properties
   ```

2. Edit `oAuth.properties` and fill in your Azure AD application details:
   - `app.clientId` - Your Azure AD application client ID (required)
   - `app.authTenant` - Your Azure AD tenant ID for authentication (required)
   - `app.clientSecret` - Client secret (only if using app-only authentication)
   - `app.tenantId` - Tenant ID (only if using app-only authentication)
   - `app.graphUserScopes` - Comma-separated list of Microsoft Graph scopes

## Authentication

The script requires web authentication in order to receive a token from MS Graph. A URL and an auth code will be pasted to the shell. Copy that URL to a web browser and follow the prompts to enter the auth code. Once those steps are complete, the script have the ability to interact with the associated MS O365 account.

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

