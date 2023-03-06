//===================================================================
// ArgParser.java
// 	Description:
// 		Handles argument parsing for CLI tasks.
//===================================================================

package com.socialvagrancy.msworkflows.ui;

import java.util.HashMap;

public class ArgParser
{
	private HashMap<String, String> arg_map;
	private HashMap<String, String> short_cut_map;

	public ArgParser()
	{
		arg_map = new HashMap<String, String>();
		short_cut_map = new HashMap<String, String>();
	}

	public boolean getBoolean(String value) throws Exception
	{
		// If the key is in the map - true
		// if the key is not in the map - false
		// if the key is int he map, but there is a value - error

		if(arg_map.get(value) == null)
		{
			return false;
		}
		
		if(arg_map.get(value).equals(""))
		{
			return true;
		}
		
		throw new Exception("Unable to parse value " + arg_map.get(value));
	}

	public String get(String value) throws Exception
	{
		if(arg_map.get(value) == null)
		{
			throw new Exception("Required field [" + value + "] was not set. Please set with --" + value + " when executing the script.");
		}
		else if(arg_map.get(value).equals(""))
		{
			throw new Exception("Incorrect value selected for [" + value + "].");
		}
		else
		{
			return arg_map.get(value);
		}
	}

	public void parse(String[] args)
	{
		String flag;
		String value;

		for(int i=0; i<args.length; i++)
		{
			flag = null;
			value = "";

			// Check to see if this is the full flag name.
			// If only a "-" is used, an abbreviated flag
			// has been used.
			if(args[i].substring(0, 2).equals("--"))
			{
				flag = args[i].substring(2, args[i].length());

			}

			// Check to see if the next index is a flag.
			// if not, store string as values.
			// a while loop instead of an if statement allows
			// parsing options with white space.
			while(((i+1) < args.length) && (!args[i+1].substring(0, 2).equals("--")))
			{
				// Increment i first to save typing.
				// i needs to be incremented to skip over the
				// value during flag parsing as well.
				i++;
				value += args[i];
			}
			
			if(flag != null)
			{
				arg_map.put(flag, value);
			}	
		}
	}
}
