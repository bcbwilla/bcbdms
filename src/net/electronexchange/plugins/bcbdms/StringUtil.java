package net.electronexchange.plugins.bcbdms;
/*
 * Some simple utility functions to help with the death message
 */
public class StringUtil {
	
	public static boolean isVowel(char c){
		  return "AEIOUaeiou".indexOf(c) != -1;
		}
	
	public static boolean startsWithVowel(String s){
		return isVowel(s.charAt(0));
	}
	
}
