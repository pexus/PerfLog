package testutils;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * (C) Copyright IBM Corp. 2005 - All Rights Reserved.
 * DISCLAIMER:
 * The following source code is sample code created by IBM Corporation.
 * This sample code is not part of any standard IBM product and is provided
 * to you solely for the purpose of assisting you in the development of your
 * applications. The code is provided 'AS IS', without warranty or condition
 * of any kind. IBM shall not be liable for any damages arising out of your
 * use of the sample code, even if IBM has been advised of the possibility of
 * such damages.
 *
 * Author: Pradeep Nambiar  nambiar1@us.ibm.com
 *
 */
/**
 * Insert the type's description here.
 * Creation date: (8/17/2000 10:38:53 AM)
 * @author: Pradeep Nambiarr
 */
public class HtmlUtils {
	public static void  printResponse(
		HttpServletRequest req,
		HttpServletResponse resp,
		String line)
		throws IOException {
	
		resp.getWriter().println(line);
	
	}


	public static void printResponseEpilogue(
		HttpServlet servlet,
		HttpServletRequest req,
		HttpServletResponse resp)
		throws IOException {
		resp.getWriter().println(HtmlUtils.getLink("/"+servlet.getServletContext().getServletContextName(),"Return to Demo"));
		resp.getWriter().println(HtmlUtils.getBodyClose());
		resp.getWriter().println(HtmlUtils.getPreFormatOpen());
	
	}


	public static void printResponseLine(
		HttpServletRequest req,
		HttpServletResponse resp,
		String line)
		throws IOException {
	
		resp.getWriter().println(line);
		resp.getWriter().println(HtmlUtils.getBreak());
	
	}


	public static void  printResponsePrologue(
		HttpServletRequest req,
		HttpServletResponse resp)
		throws IOException {
	
		resp.getWriter().println(HtmlUtils.getBodyBegin(""));
	
	}


	private static String bgColor = "#ffffff";
	private static String textColor = "#000000";
	private static String linkColor = "#0000ff";
	private static String vLinkColor = "#cc00cc";
	private static String aLinkColor = "#00ff00";
	private static String tableBorder = "1";
	/**
	 * Insert the method's description here.
	 * Creation date: (8/17/2000 10:43:26 AM)
	 */
	public static String getBodyBegin(String heading) {
		return "<HTML><HEAD><TITLE>"
			+ heading
			+ "</TITLE></HEAD><BODY bgcolor=\""
			+ bgColor
			+ "\"  text=\""
			+ textColor
			+ "\" link=\""
			+ linkColor
			+ "f\" vlink=\""
			+ vLinkColor
			+ "\" alink=\""
			+ aLinkColor
			+ "\">";
	}
	public static String getBreak() {
			return "<br></br>";
		}
	public static String getBodyClose() {
		return "</BODY></HTML>";
	}
	public static String getBoldClose() {
		return "</B>";
	}
	public static String getBoldOpen() {
		return "<B>";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/8/00 9:15:11 AM)
	 */
	public static String getCellAndRowClose() {
		// 2/18/00, dmw
		// these need to be "reverse" of what they are in CellAndRowOpen
		return "</TD></TR>"; //$NON-NLS-1$
	}
	public static String getCellAndRowOpen() {
		return "<TR><TD>"; //$NON-NLS-1$
	}
	public static String getCellAndRowOpen(String alignment) {
		return "<TR><TD align=" + alignment + ">"; //$NON-NLS-2$//$NON-NLS-1$
	}
	public static String getErrorColorClose() {
		return "</font>";
	}
	public static String getErrorColorOpen() {
		return "<font color=\"#ff0000\">";
	}
	public static String getColorOpen(String color) {
		return "<font color=\"" + color + "\">";
	}
	public static String getColorClose() {
		return "</font>";
	}
	public static String getHeading1(String heading, String alignment) {
		if (alignment == null)
			alignment = "CENTER";
		return "<"
			+ alignment
			+ "><H1>"
			+ heading
			+ "</H1></"
			+ alignment
			+ ">";
	}
	public static String getHeading2(String heading, String alignment) {
		if (alignment == null)
			alignment = "CENTER";
		return "<"
			+ alignment
			+ "><H2>"
			+ heading
			+ "</H3></"
			+ alignment
			+ ">";
	}
	public static String getHeading3(String heading, String alignment) {
		if (alignment == null)
			alignment = "CENTER";
		return "<"
			+ alignment
			+ "><H3>"
			+ heading
			+ "</H3></"
			+ alignment
			+ ">";
	}
	public static String getHRule() {
		return "<HR>";
	}
	public static String getLink(String link, String linkLabel) {
		return "<A HREF = \"" + link + "\">" + linkLabel + "</A>";
	}
	public static String getPreFormatClose() {
		return "</PRE>";
	}
	public static String getPreFormatOpen() {
		return "<PRE>";
	}
	public static String getTableClose() {
		return "</TABLE>";
	}
	public static String getTableOpen(String alignment) {
		if (alignment == null)
			alignment = "CENTER";
		return "<" + alignment + "><TABLE border=\"" + tableBorder + "\">";
	}
}

