//===================================================================
// MSWorkflows.java
// 	Description:
// 		This is the primary class for executing the MS Office
// 		Automation Workflows.
//===================================================================

package com.socialvagrancy.msworkflows.ui;

public class MSWorkflows
{
	public MSWorkflows(ArgParser aparser)
	{
		try
		{
			Controller controller = new Controller("../log/msworkflows.log", 1, 102400, 3, "oAuth.properties");

			switch(aparser.get("command"))
			{
				case "send-email":
					System.err.println("Sending email...");
					break;
				default:
					System.err.println("Invalid command selected [" + aparser.get("command") + "]. Please use -h/--help to see a list of valid commands.");
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser();

		aparser.parse(args);

		MSWorkflows automation = new MSWorkflows(aparser);
	}
}
