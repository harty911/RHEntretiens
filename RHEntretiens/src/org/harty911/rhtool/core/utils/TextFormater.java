package org.harty911.rhtool.core.utils;

import java.util.Deque;
import java.util.LinkedList;

public class TextFormater {

	public static String toRichHTML( String s) {
		
		StringBuilder sb = new StringBuilder();
		Deque<String> blocks = new LinkedList<String>();
		boolean newLine=true;
		boolean bold = false;
		boolean blank =true;
		boolean underline = false;
		
		// Convertion iteration
		for( char c : s.toCharArray() ) {
			// closing block
			if( c=='\n') {
				// close styles
				if( bold) 		sb.append( "</b>");
				if( underline) 	sb.append( "</u>");
				bold = false;
				underline = false;
				// close block (if exists)
				if( !blocks.isEmpty())
					sb.append( blocks.pop());

				// more than one linebreak ...
				if( newLine)
					sb.append("<br>");
				// new line will start next step
				newLine=true;
				
				continue;
			}

			// is a newline starting
			if( newLine) {
				newLine=false;
				switch( c) {
					case '#':
						sb.append("<h4>");
						blocks.push("</h4>\n");
						continue;
					case '-':
						sb.append("<li>");
						blocks.push("</li>\n");
						continue;
					default:
						sb.append("<p>");
						blocks.push("</p>\n");
						break;
				}
			}
			
			// manage styles 
			if( c=='*') {
				if( bold) {
					sb.append( "</b>");
					bold = false;
				} else {
					sb.append("<b>");
					bold = true;
				}
				continue;
			}
			if( c=='_') {
				if( underline) {
					sb.append( "</u>");
					underline = false;
				} else {
					sb.append("<u>");
					underline = true;
				}
				continue;
			}
				
			// manage multi space
			if( c == ' ') {
				if( blank)
					sb.append("&nbsp;");
				blank = true;
			}
			else {
				blank = false;
			}
			
			// convert chars
			sb.append( toHTML(c));
		}
		// close pending spans and blocks
		if( bold) 		sb.append( "</b>");
		if( underline) 	sb.append( "</u>");
		while( !blocks.isEmpty())
			sb.append( blocks.pop());


		return sb.toString();
	}

	private static String toHTML(char c) {
        switch(c) {
    		case 13: return "";	// ignore carriage return
        	case '<': return "&lt;";
        	case '>': return "&gt;";
        	case '&': return "&amp;";
        	case '"': return "&quot;";
        	// We need Tab support here, because we print StackTraces as HTML
        	case '\t': return "&nbsp;&nbsp;&nbsp;&nbsp;";  
        	default:
        		if( c < 128 ) {
        			return String.valueOf(c);
        		} else if(c < 256){
        			return "&#"+((int)c)+";";
        		}    
        }
		return "";
	}
}
