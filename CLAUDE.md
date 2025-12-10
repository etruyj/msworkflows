# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**msworkflows** (o365-connector) is a Java CLI tool for automating Microsoft Office 365 workflows via MS Graph API. It sends templated emails and creates meeting invitations with variable substitution. The tool uses device code authentication to interact with MS Graph.

**Main Class**: `com.socialvagrancy.connector.o365.ui.MSWorkflows`

## Build and Development Commands

### Maven Commands
```bash
# Build the project
mvn clean package

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=AppTest

# Install dependencies
mvn install

# Compile only
mvn compile
```

### Running the Application
```bash
java -cp target/o365-connector-1.1.0.jar com.socialvagrancy.connector.o365.ui.MSWorkflows --command <command> --template <path>
```

## Architecture

### Core Flow (UI Layer → Controller → Command → Graph)

1. **Entry Point**: `MSWorkflows.java` - Parses arguments and delegates to Controller
2. **Controller**: `Controller.java` - Initializes Graph connection and routes commands
3. **Command Layer**: `TemplateEmail.java`, `TemplateMeeting.java` - Load templates, gather user input for variables, and execute actions
4. **Graph Layer**: `Graph.java` - Wraps Microsoft Graph SDK for API calls

### Key Components

**UI Package** (`com.socialvagrancy.connector.o365.ui`):
- `MSWorkflows` - Main entry point with switch statement for commands
- `Controller` - Initializes logging and Graph authentication, routes to command classes
- `ArgParser` - Custom CLI argument parser using HashMap (only supports `--` flags)

**Command Package** (`com.socialvagrancy.connector.o365.command`):
- `TemplateEmail` - Loads email JSON template, prompts for variables via Scanner, sends via Graph
- `TemplateMeeting` - Loads meeting JSON template, prompts for variables, creates calendar event
- `CheckAvailability` - Unimplemented placeholder

**Model Package** (`com.socialvagrancy.connector.o365.model`):
- `EmailTemplateModel` - POJO for email with to/cc/bcc recipients, subject, body, variables
- `MeetingTemplateModel` - POJO for meeting with subject, body, start/end time, room, attendees, variables

**Util Package** (`com.socialvagrancy.connector.o365.util`):
- `ReplaceVariables` - Static methods for substituting `{variable}` placeholders with user input
- `graph.Graph` - Microsoft Graph SDK wrapper (device code auth for user, client secret for app-only)

### Template System

Templates are JSON files with variable placeholders marked with `{}`. Variables are listed in a `variables` array and the user is prompted to provide values via stdin (Scanner).

**Email Template Fields**:
- `to_recipient`, `cc_recipient`, `bcc_recipient` (Arrays)
- `subject`, `body` (Strings)
- `variables` (Array of variable names without braces)

**Meeting Template Fields**:
- `subject`, `body`, `start_time`, `end_time`, `meeting_room` (Strings)
- `attendees` (Array)
- `variables` (Array)

### Authentication

The application requires `oAuth.properties` in `../resources/` directory relative to the JAR. This file must contain:
- `app.clientId` - Azure AD application client ID
- `app.authTenant` - Azure AD tenant ID
- `app.graphUserScopes` - Comma-separated Graph API scopes

Device code authentication flow is used - the user is prompted to visit a URL and enter a code.

### Dependencies

- **Microsoft Graph SDK** (v5.22.0) - MS Graph API client
- **Azure Identity** (v1.5.0) - Authentication
- **Gson** (v2.9.1) - JSON parsing
- **socialvagrancy.utils** (v1.8.0) - Custom utilities (Logger, FileManager)

## Commands

Available commands (via `--command` flag):
- `check-availability` - Unimplemented (requires `--user`)
- `create-meeting` - Create meeting from template (requires `--template`)
- `send-email` - Send email from template (requires `--template`)

## Important Notes

- Logging is configured to write to `../log/msworkflows.log` with rotation (102400 bytes, 3 files)
- Timezone is hardcoded to "Eastern Standard Time" in meeting creation (line 71 of TemplateMeeting.java)
- Graph.java contains both user authentication (`_userClient`) and app-only authentication (`_appClient`) paths, though app-only is not currently used by commands
- Variable replacement expects braces in templates but strips them when matching against the variables array
- Java 18 is required (specified in pom.xml)
