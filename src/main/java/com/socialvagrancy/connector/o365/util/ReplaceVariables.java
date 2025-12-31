//===================================================================
// ReplaceVariables.java
// 	Description:
//		This command replace the variables in a template file
//		with the proper values.
//===================================================================

package com.socialvagrancy.connector.o365.util;

import java.lang.StringBuffer;
import java.lang.StringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static List<String> processLoopsToList(String template, Map<String, List<String>> var_map) {
        List<String> results = new ArrayList<String>();

        Pattern loopPattern = Pattern.compile("\\{@loop:(\\w+)\\}(.*?)\\{@endloop\\}", Pattern.DOTALL);
        Matcher matcher = loopPattern.matcher(template);

        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            String varName = matcher.group(1);
            String loopContent = matcher.group(2);

            List<String> values = var_map.get(varName);
            StringBuilder replacement = new StringBuilder();

            if(values != null) {
                for(String value : values) {
                    replacement.append(loopContent.replace("{item}", value));
                    results.add(value);
                }
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
        }

        matcher.appendTail(sb);
        return results;
    }

    public static String processLoopsToText(String template, Map<String, List<String>> var_map) {
        Pattern loopPattern = Pattern.compile("\\{@loop:(\\w+)\\}(.*?)\\{@endloop\\}", Pattern.DOTALL);
        Matcher matcher = loopPattern.matcher(template);

        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            String varName = matcher.group(1);
            String loopContent = matcher.group(2);

            List<String> values = var_map.get(varName);
            StringBuilder replacement = new StringBuilder();

            if(values != null) {
                for(String value : values) {
                    replacement.append(loopContent.replace("{item}", value));
                }
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String processText(String template, Map<String, List<String>> var_map) throws Exception {
        template = processLoopsToText(template, var_map);
        return processVariables(template, var_map);
    }

    public static List<String> processTextToList(List<String> fields, Map<String, List<String>> var_map) throws Exception {
        List<String> results = new ArrayList<String>();
        List<String> return_val = null;

        for(String field : fields) {
            return_val = processLoopsToList(field, var_map);

            if(return_val.isEmpty()) {
                return_val = processVariablesToList(field, var_map);
            }

            if(!return_val.isEmpty()) {
                results.addAll(return_val);
            }
        }

        return results;
    }

    public static String processVariables(String template, Map<String, List<String>> var_map) throws Exception {
        Pattern varPattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = varPattern.matcher(template);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            String varName = matcher.group(1);
            List<String> values = var_map.get(varName);

            if(values != null && !values.isEmpty()) {
                if(values.size() == 1) {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(values.get(0)));
                } else {
                    throw new Exception("Variable {" + varName + "} is defined as a single variable, but multiple values are present. Use {@loop:" + varName + "}{item}{@endloop} notation for the template to process this field.");
                }
            } else {
                // Keep the original variable if not found in map
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        matcher.appendTail(sb);
        
        return sb.toString();
    }

    public static List<String> processVariablesToList(String template, Map<String, List<String>> var_map) throws Exception {
        List<String> results = new ArrayList<String>();

        Pattern varPattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = varPattern.matcher(template);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            String varName = matcher.group(1);
            List<String> values = var_map.get(varName);

            if(values != null && !values.isEmpty()) {
                if(values.size() == 1) {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(values.get(0)));
                    results.add(values.get(0));
                } else {
                    throw new Exception("Variable {" + varName + "} is defined as a single variable, but multiple values are present. Use {@loop:" + varName + "}{item}{@endloop} notation for the template to process this field.");
                }
            } else {
                // Keep the original variable if not found in map
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        matcher.appendTail(sb);
        
        return results;
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
