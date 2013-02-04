/**
 *
 * This file is part of the SJWidget library.
 * (c) 2005-2012 Bjorn Roche
 * Development of this library has been supported by Indaba Media (http://www.indabamusic.com)
 * and XO Audio (http://www.xoaudio.com)
 *
 * for copyright and sharing permissions, please see the COPYING.txt file which you should
 * have recieved with this file.
 *
 */

package com.xowave.util;

import java.util.StringTokenizer;

public final class StringUtils
{
   /**creates up to max tokens and puts them in the array. if the number of
      tokens is greater than max, the last tokens are all returned in the final
      array element with delimeters at the start of the string removed.
      If max=0, the string will be completely tokenized.
      @deprecated String.split can be used instead of this.
      */
   public static String[] getTokenArray( String string, int max, String delims )
   {
      StringTokenizer mst = new StringTokenizer( string, delims );
      int count = mst.countTokens();
      String[] ret;
      if( count <= max || max==0 ) {
         ret = new String[count];
         for( int i=0; i<count; i++ )
              ret[i]=mst.nextToken();
      } else {
         int i;
         ret = new String[max];
         for( i=0; i<max-1; i++ )
              ret[i]=mst.nextToken();
         ret[max-1]=mst.nextToken("");
         //remove initial delimiter from start of ret[max-1]
         char[] c=ret[max-1].toCharArray();
         for( i=0; i<c.length; i++ )
            if( delims.indexOf( c[i] )==-1 )
                break;
         if( i!=c.length )
            ret[max-1]=ret[max-1].substring( i );
      }

      return ret;
   }
   public static boolean equals( String s1, String s2 ) {
	   if( s1==null && s2==null )
		   return true;
	   if( s1==null || s2==null)
		   return false;
	   return s1.equals(s2);
   }
   public static String stringToHTMLString(String string) {
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    boolean lastWasBlankChar = false;
	    int len = string.length();
	    char c;

	    for (int i = 0; i < len; i++)
	        {
	        c = string.charAt(i);
	        if (c == ' ') {
	            // blank gets extra work,
	            // this solves the problem you get if you replace all
	            // blanks with &nbsp;, if you do that you loss 
	            // word breaking
	            if (lastWasBlankChar) {
	                lastWasBlankChar = false;
	                sb.append("&nbsp;");
	                }
	            else {
	                lastWasBlankChar = true;
	                sb.append(' ');
	                }
	            }
	        else {
	            lastWasBlankChar = false;
	            //
	            // HTML Special Chars
	            if (c == '"')
	                sb.append("&quot;");
	            else if (c == '&')
	                sb.append("&amp;");
	            else if (c == '<')
	                sb.append("&lt;");
	            else if (c == '>')
	                sb.append("&gt;");
	            else if (c == '\n')
	                // Handle Newline
	                sb.append("&lt;br/&gt;");
	            else {
	                int ci = 0xffff & c;
	                if (ci < 160 )
	                    // nothing special only 7 Bit
	                    sb.append(c);
	                else {
	                    // Not 7 Bit use the unicode system
	                    sb.append("&#");
	                    sb.append(new Integer(ci).toString());
	                    sb.append(';');
	                    }
	                }
	            }
	        }
	    return sb.toString();
	}
}
