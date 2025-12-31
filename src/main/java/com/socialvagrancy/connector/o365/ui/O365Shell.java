//===================================================================
// O365Shell.java
// 	Description:
// 		This is the primary class for executing the MS Office
// 		Automation Workflows.
//===================================================================

package com.socialvagrancy.connector.o365.ui;

import com.socialvagrancy.connector.o365.command.O365Controller;

public class O365Shell {
	public O365Shell(ArgParser aparser) {
		try	{
			O365Controller controller = new O365Controller("../log/msworkflows.log", 1, 102400, 3, "../resources/oAuth.properties");

			switch(aparser.get("command")) {
				case "check-availability":
					System.out.println("Checking availability for [" + aparser.get("user") + "]...");
					controller.checkAvailability(aparser.get("user"));
					break;
				case "create-meeting":
					System.out.println("Creating meeting...");
					controller.createMeeting(aparser.get("template"));
					break;
				case "send-email":
					System.out.println("Sending email...");
					controller.sendEmailWithPrompts(aparser.get("template"));
					break;
				default:
					System.err.println("Invalid command selected [" + aparser.get("command") + "]. Please use -h/--help to see a list of valid commands.");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		ArgParser aparser = new ArgParser();

		aparser.parse(args);

		O365Shell conn = new O365Shell(aparser);
	}
}
