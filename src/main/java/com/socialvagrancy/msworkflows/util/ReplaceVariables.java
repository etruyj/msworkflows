//===================================================================
// ReplaceVariables.java
// 	Description:
//		This command replace the variables in a template file
//		with the proper values.
//===================================================================

package com.socialvagrancy.msworkflows.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ReplaceVariables
{
	public static ArrayList<String> inArrayList(ArrayList<String> address_list, HashMap<String, String> var_map)
	{
		// Build out the email list and replace them with variables.

		ArrayList<String> email_list = new ArrayList<String>();

		for(int i=0; i<address_list.size(); i++)
		{
			email_list.add(var_map.get(address_list.get(i).substring(1, address_list.get(i).length()-1)));
		}

		return email_list;
	}

	public static String inText(String text, HashMap<String, String> var_map)
	{
		for(HashMap.Entry<String, String> variable : var_map.entrySet())
		{
			text = text.replace("{" + variable.getKey() + "}", variable.getValue());
		}	

		return text;
	}
	
	public static HashMap<String, String> userInputVariables(ArrayList<String> var_list)
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
