/*
 * Zmanim Java API
 * Copyright (C) 2004-2018 Eliyahu Hershfeld
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA,
 * or connect to: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package net.sourceforge.zmanim.util;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import net.sourceforge.zmanim.AstronomicalCalendar;

/**
 * A class used to format both non {@link java.util.Date} times generated by the Zmanim package as well as Dates. For
 * example the {@link net.sourceforge.zmanim.AstronomicalCalendar#getTemporalHour()} returns the length of the hour in
 * milliseconds. This class can format this time.
 * 
 * @author &copy; Eliyahu Hershfeld 2004 - 2018
 * @version 1.2
 */
public class ZmanimFormatter {
	private boolean prependZeroHours = false;

	private boolean useSeconds = false;

	private boolean useMillis = false;

	private static DecimalFormat minuteSecondNF = new DecimalFormat("00");

	private DecimalFormat hourNF;

	private static DecimalFormat milliNF = new DecimalFormat("000");

	private SimpleDateFormat dateFormat;

	private TimeZone timeZone = null; // TimeZone.getTimeZone("UTC");

	// private DecimalFormat decimalNF;

	/**
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Format using hours, minutes, seconds and milliseconds using the xsd:time format. This format will return
	 * 00.00.00.0 when formatting 0.
	 */
	public static final int SEXAGESIMAL_XSD_FORMAT = 0;

	private int timeFormat = SEXAGESIMAL_XSD_FORMAT;

	/**
	 * Format using standard decimal format with 5 positions after the decimal.
	 */
	public static final int DECIMAL_FORMAT = 1;

	/** Format using hours and minutes. */
	public static final int SEXAGESIMAL_FORMAT = 2;

	/** Format using hours, minutes and seconds. */
	public static final int SEXAGESIMAL_SECONDS_FORMAT = 3;

	/** Format using hours, minutes, seconds and milliseconds. */
	public static final int SEXAGESIMAL_MILLIS_FORMAT = 4;

	/** constant for milliseconds in a minute (60,000) */
	static final long MINUTE_MILLIS = 60 * 1000;

	/** constant for milliseconds in an hour (3,600,000) */
	public static final long HOUR_MILLIS = MINUTE_MILLIS * 60;

	/**
	 * Format using the XSD Duration format. This is in the format of PT1H6M7.869S (P for period (duration), T for time,
	 * H, M and S indicate hours, minutes and seconds.
	 */
	public static final int XSD_DURATION_FORMAT = 5;

	/**
	 * constructor that defaults to this will use the format "h:mm:ss" for dates and 00.00.00.0 for {@link Time}.
	 * @param timeZone the TimeZone Object
	 */
	public ZmanimFormatter(TimeZone timeZone) {
		this(0, new SimpleDateFormat("h:mm:ss"), timeZone);
	}

	// public ZmanimFormatter() {
	// this(0, new SimpleDateFormat("h:mm:ss"), TimeZone.getTimeZone("UTC"));
	// }

	/**
	 * ZmanimFormatter constructor using a formatter
	 * 
	 * @param format
	 *            int The formatting style to use. Using ZmanimFormatter.SEXAGESIMAL_SECONDS_FORMAT will format the time
	 *            time of 90*60*1000 + 1 as 1:30:00
	 * @param dateFormat the SimpleDateFormat Object
	 * @param timeZone the TimeZone Object
	 */
	public ZmanimFormatter(int format, SimpleDateFormat dateFormat, TimeZone timeZone) {
		setTimeZone(timeZone);
		String hourFormat = "0";
		if (prependZeroHours) {
			hourFormat = "00";
		}
		this.hourNF = new DecimalFormat(hourFormat);
		setTimeFormat(format);
		dateFormat.setTimeZone(timeZone);
		setDateFormat(dateFormat);
	}

	/**
	 * Sets the format to use for formatting.
	 * 
	 * @param format
	 *            int the format constant to use.
	 */
	public void setTimeFormat(int format) {
		this.timeFormat = format;
		switch (format) {
		case SEXAGESIMAL_XSD_FORMAT:
			setSettings(true, true, true);
			break;
		case SEXAGESIMAL_FORMAT:
			setSettings(false, false, false);
			break;
		case SEXAGESIMAL_SECONDS_FORMAT:
			setSettings(false, true, false);
			break;
		case SEXAGESIMAL_MILLIS_FORMAT:
			setSettings(false, true, true);
			break;
		// case DECIMAL_FORMAT:
		// default:
		}
	}

	/**
	 * Sets the SimpleDateFormat Object
	 * @param simpleDateFormat the SimpleDateFormat Object to set
	 */
	public void setDateFormat(SimpleDateFormat simpleDateFormat) {
		this.dateFormat = simpleDateFormat;
	}

	/**
	 * returns the SimpleDateFormat Object
	 * @return the SimpleDateFormat Object
	 */
	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	private void setSettings(boolean prependZeroHours, boolean useSeconds, boolean useMillis) {
		this.prependZeroHours = prependZeroHours;
		this.useSeconds = useSeconds;
		this.useMillis = useMillis;
	}

	/**
	 * A method that formats milliseconds into a time format.
	 * 
	 * @param milliseconds
	 *            The time in milliseconds.
	 * @return String The formatted <code>String</code>
	 */
	public String format(double milliseconds) {
		return format((int) milliseconds);
	}

	/**
	 * A method that formats milliseconds into a time format.
	 * 
	 * @param millis
	 *            The time in milliseconds.
	 * @return String The formatted <code>String</code>
	 */
	public String format(int millis) {
		return format(new Time(millis));
	}

	/**
	 * A method that formats {@link Time}objects.
	 * 
	 * @param time
	 *            The time <code>Object</code> to be formatted.
	 * @return String The formatted <code>String</code>
	 */
	public String format(Time time) {
		if (this.timeFormat == XSD_DURATION_FORMAT) {
			return formatXSDDurationTime(time);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(this.hourNF.format(time.getHours()));
		sb.append(":");
		sb.append(minuteSecondNF.format(time.getMinutes()));
		if (this.useSeconds) {
			sb.append(":");
			sb.append(minuteSecondNF.format(time.getSeconds()));
		}
		if (this.useMillis) {
			sb.append(".");
			sb.append(milliNF.format(time.getMilliseconds()));
		}
		return sb.toString();
	}

	/**
	 * Formats a date using this classe's {@link #getDateFormat() date format}.
	 * 
	 * @param dateTime
	 *            the date to format
	 * @param calendar
	 *            the {@link java.util.Calendar Calendar} used to help format based on the Calendar's DST and other
	 *            settings.
	 * @return the formatted String
	 */
	public String formatDateTime(Date dateTime, Calendar calendar) {
		this.dateFormat.setCalendar(calendar);
		if (this.dateFormat.toPattern().equals("yyyy-MM-dd'T'HH:mm:ss")) {
			return getXSDateTime(dateTime, calendar);
		} else {
			return this.dateFormat.format(dateTime);
		}

	}

	/**
	 * The date:date-time function returns the current date and time as a date/time string. The date/time string that's
	 * returned must be a string in the format defined as the lexical representation of xs:dateTime in <a
	 * href="http://www.w3.org/TR/xmlschema11-2/#dateTime">[3.3.8 dateTime]</a> of <a
	 * href="http://www.w3.org/TR/xmlschema11-2/">[XML Schema 1.1 Part 2: Datatypes]</a>. The date/time format is
	 * basically CCYY-MM-DDThh:mm:ss, although implementers should consult <a
	 * href="http://www.w3.org/TR/xmlschema11-2/">[XML Schema 1.1 Part 2: Datatypes]</a> and <a
	 * href="http://www.iso.ch/markete/8601.pdf">[ISO 8601]</a> for details. The date/time string format must include a
	 * time zone, either a Z to indicate Coordinated Universal Time or a + or - followed by the difference between the
	 * difference from UTC represented as hh:mm.
	 * @param dateTime the Date Object
	 * @param calendar Calendar Object
	 * @return the XSD dateTime
	 */
	public String getXSDateTime(Date dateTime, Calendar calendar) {
		String xsdDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";
		/*
		 * if (xmlDateFormat == null || xmlDateFormat.trim().equals("")) { xmlDateFormat = xsdDateTimeFormat; }
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat(xsdDateTimeFormat);
		dateFormat.setTimeZone(getTimeZone());

		StringBuffer sb = new StringBuffer(dateFormat.format(dateTime));
		// Must also include offset from UTF.
		int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);// Get the offset (in milliseconds)
		// If there is no offset, we have "Coordinated Universal Time"
		if (offset == 0)
			sb.append("Z");
		else {
			// Convert milliseconds to hours and minutes
			int hrs = offset / (60 * 60 * 1000);
			// In a few cases, the time zone may be +/-hh:30.
			int min = offset % (60 * 60 * 1000);
			char posneg = hrs < 0 ? '-' : '+';
			sb.append(posneg + formatDigits(hrs) + ':' + formatDigits(min));
		}
		return sb.toString();
	}

	/**
	 * Represent the hours and minutes with two-digit strings.
	 * 
	 * @param digits
	 *            hours or minutes.
	 * @return two-digit String representation of hrs or minutes.
	 */
	private static String formatDigits(int digits) {
		String dd = String.valueOf(Math.abs(digits));
		return dd.length() == 1 ? '0' + dd : dd;
	}

	/**
	 * This returns the xml representation of an xsd:duration object.
	 * 
	 * @param millis
	 *            the duration in milliseconds
	 * @return the xsd:duration formatted String
	 */
	public String formatXSDDurationTime(long millis) {
		return formatXSDDurationTime(new Time(millis));
	}

	/**
	 * This returns the xml representation of an xsd:duration object.
	 * 
	 * @param time
	 *            the duration as a Time object
	 * @return the xsd:duration formatted String
	 */
	public String formatXSDDurationTime(Time time) {
		StringBuffer duration = new StringBuffer();
		if (time.getHours() != 0 || time.getMinutes() != 0 || time.getSeconds() != 0 || time.getMilliseconds() != 0) {
			duration.append("P");
			duration.append("T");

			if (time.getHours() != 0)
				duration.append(time.getHours() + "H");

			if (time.getMinutes() != 0)
				duration.append(time.getMinutes() + "M");

			if (time.getSeconds() != 0 || time.getMilliseconds() != 0) {
				duration.append(time.getSeconds() + "." + milliNF.format(time.getMilliseconds()));
				duration.append("S");
			}
			if (duration.length() == 1) // zero seconds
				duration.append("T0S");
			if (time.isNegative())
				duration.insert(0, "-");
		}
		return duration.toString();
	}

	/**
	 * A method that returns an XML formatted <code>String</code> representing the serialized <code>Object</code>. The
	 * format used is:
	 * 
	 * <pre>
	 *  &lt;AstronomicalTimes date=&quot;1969-02-08&quot; type=&quot;net.sourceforge.zmanim.AstronomicalCalendar algorithm=&quot;US Naval Almanac Algorithm&quot; location=&quot;Lakewood, NJ&quot; latitude=&quot;40.095965&quot; longitude=&quot;-74.22213&quot; elevation=&quot;31.0&quot; timeZoneName=&quot;Eastern Standard Time&quot; timeZoneID=&quot;America/New_York&quot; timeZoneOffset=&quot;-5&quot;&gt;
	 *     &lt;Sunrise&gt;2007-02-18T06:45:27-05:00&lt;/Sunrise&gt;
	 *     &lt;TemporalHour&gt;PT54M17.529S&lt;/TemporalHour&gt;
	 *     ...
	 *   &lt;/AstronomicalTimes&gt;
	 * </pre>
	 * 
	 * Note that the output uses the <a href="http://www.w3.org/TR/xmlschema11-2/#dateTime">xsd:dateTime</a> format for
	 * times such as sunrise, and <a href="http://www.w3.org/TR/xmlschema11-2/#duration">xsd:duration</a> format for
	 * times that are a duration such as the length of a
	 * {@link net.sourceforge.zmanim.AstronomicalCalendar#getTemporalHour() temporal hour}. The output of this method is
	 * returned by the {@link #toString() toString}.
	 * 
	 * @param astronomicalCalendar the AstronomicalCalendar Object
	 * 
	 * @return The XML formatted <code>String</code>. The format will be:
	 * 
	 *         <pre>
	 *  &lt;AstronomicalTimes date=&quot;1969-02-08&quot; type=&quot;net.sourceforge.zmanim.AstronomicalCalendar algorithm=&quot;US Naval Almanac Algorithm&quot; location=&quot;Lakewood, NJ&quot; latitude=&quot;40.095965&quot; longitude=&quot;-74.22213&quot; elevation=&quot;31.0&quot; timeZoneName=&quot;Eastern Standard Time&quot; timeZoneID=&quot;America/New_York&quot; timeZoneOffset=&quot;-5&quot;&gt;
	 *     &lt;Sunrise&gt;2007-02-18T06:45:27-05:00&lt;/Sunrise&gt;
	 *     &lt;TemporalHour&gt;PT54M17.529S&lt;/TemporalHour&gt;
	 *     ...
	 *  &lt;/AstronomicalTimes&gt;
	 * </pre>
	 * 
	 *         TODO: add proper schema, and support for nulls. XSD duration (for solar hours), should probably return
	 *         nil and not P
	 */
	public static String toXML(AstronomicalCalendar astronomicalCalendar) {
		ZmanimFormatter formatter = new ZmanimFormatter(ZmanimFormatter.XSD_DURATION_FORMAT, new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss"), astronomicalCalendar.getGeoLocation().getTimeZone());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(astronomicalCalendar.getGeoLocation().getTimeZone());

		StringBuffer sb = new StringBuffer("<");
		if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.AstronomicalCalendar")) {
			sb.append("AstronomicalTimes");
			// TODO: use proper schema ref, and maybe build a real schema.
			// output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
			// output += xsi:schemaLocation="http://www.kosherjava.com/zmanim astronomical.xsd"
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ComplexZmanimCalendar")) {
			sb.append("Zmanim");
			// TODO: use proper schema ref, and maybe build a real schema.
			// output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
			// output += xsi:schemaLocation="http://www.kosherjava.com/zmanim zmanim.xsd"
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ZmanimCalendar")) {
			sb.append("BasicZmanim");
			// TODO: use proper schema ref, and maybe build a real schema.
			// output += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
			// output += xsi:schemaLocation="http://www.kosherjava.com/zmanim basicZmanim.xsd"
		}
		sb.append(" date=\"").append(df.format(astronomicalCalendar.getCalendar().getTime())).append("\"");
		sb.append(" type=\"").append(astronomicalCalendar.getClass().getName()).append("\"");
		sb.append(" algorithm=\"").append(astronomicalCalendar.getAstronomicalCalculator().getCalculatorName()).append("\"");
		sb.append(" location=\"").append(astronomicalCalendar.getGeoLocation().getLocationName()).append("\"");
		sb.append(" latitude=\"").append(astronomicalCalendar.getGeoLocation().getLatitude()).append("\"");
		sb.append(" longitude=\"").append(astronomicalCalendar.getGeoLocation().getLongitude()).append("\"");
		sb.append(" elevation=\"").append(astronomicalCalendar.getGeoLocation().getElevation()).append("\"");
		sb.append(" timeZoneName=\"").append(astronomicalCalendar.getGeoLocation().getTimeZone().getDisplayName()).append("\"");
		sb.append(" timeZoneID=\"").append(astronomicalCalendar.getGeoLocation().getTimeZone().getID()).append("\"");
		sb.append(" timeZoneOffset=\"")
				.append((astronomicalCalendar.getGeoLocation().getTimeZone().getOffset(astronomicalCalendar.getCalendar().getTimeInMillis()) / ((double) HOUR_MILLIS)))
				.append("\"");

		sb.append(">\n");

		Method[] theMethods = astronomicalCalendar.getClass().getMethods();
		String tagName = "";
		Object value = null;
		List<Zman> dateList = new ArrayList<Zman>();
		List<Zman> durationList = new ArrayList<Zman>();
		List<String> otherList = new ArrayList<String>();
		for (int i = 0; i < theMethods.length; i++) {
			if (includeMethod(theMethods[i])) {
				tagName = theMethods[i].getName().substring(3);
				// String returnType = theMethods[i].getReturnType().getName();
				try {
					value = theMethods[i].invoke(astronomicalCalendar, (Object[]) null);
					if (value == null) {// TODO: Consider using reflection to determine the return type, not the value
						otherList.add("<" + tagName + ">N/A</" + tagName + ">");
						// TODO: instead of N/A, consider return proper xs:nil.
						// otherList.add("<" + tagName + " xs:nil=\"true\" />");
					} else if (value instanceof Date) {
						dateList.add(new Zman((Date) value, tagName));
					} else if (value instanceof Long || value instanceof Integer) {// shaah zmanis
						if (((Long) value).longValue() == Long.MIN_VALUE) {
							otherList.add("<" + tagName + ">N/A</" + tagName + ">");
							// TODO: instead of N/A, consider return proper xs:nil.
							// otherList.add("<" + tagName + " xs:nil=\"true\" />");
						} else {
							durationList.add(new Zman((int) ((Long) value).longValue(), tagName));
						}
					} else { // will probably never enter this block, but is present to be future proof
						otherList.add("<" + tagName + ">" + value + "</" + tagName + ">");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Zman zman;
		Collections.sort(dateList, Zman.DATE_ORDER);

		for (int i = 0; i < dateList.size(); i++) {
			zman = (Zman) dateList.get(i);
			sb.append("\t<").append(zman.getZmanLabel()).append(">");
			sb.append(formatter.formatDateTime(zman.getZman(), astronomicalCalendar.getCalendar()));
			sb.append("</").append(zman.getZmanLabel()).append(">\n");
		}
		Collections.sort(durationList, Zman.DURATION_ORDER);
		for (int i = 0; i < durationList.size(); i++) {
			zman = (Zman) durationList.get(i);
			sb.append("\t<" + zman.getZmanLabel()).append(">");
			sb.append(formatter.format((int) zman.getDuration())).append("</").append(zman.getZmanLabel())
					.append(">\n");
		}

		for (int i = 0; i < otherList.size(); i++) {// will probably never enter this block
			sb.append("\t").append(otherList.get(i)).append("\n");
		}

		if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.AstronomicalCalendar")) {
			sb.append("</AstronomicalTimes>");
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ComplexZmanimCalendar")) {
			sb.append("</Zmanim>");
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ZmanimCalendar")) {
			sb.append("</BasicZmanim>");
		}
		return sb.toString();
	}
	
	/**
	 * A method that returns a JSON formatted <code>String</code> representing the serialized <code>Object</code>. The
	 * format used is:
	 * <pre>
	 * {
	 *    &quot;metadata&quot;:{
	 *      &quot;date&quot;:&quot;1969-02-08&quot;,
	 *      &quot;type&quot;:&quot;net.sourceforge.zmanim.AstronomicalCalendar&quot;,
	 *      &quot;algorithm&quot;:&quot;US Naval Almanac Algorithm&quot;,
	 *      &quot;location&quot;:&quot;Lakewood, NJ&quot;,
	 *      &quot;latitude&quot;:&quot;40.095965&quot;,
	 *      &quot;longitude&quot;:&quot;-74.22213&quot;,
	 *      &quot;elevation:&quot;31.0&quot;,
	 *      &quot;timeZoneName&quot;:&quot;Eastern Standard Time&quot;,
	 *      &quot;timeZoneID&quot;:&quot;America/New_York&quot;,
	 *      &quot;timeZoneOffset&quot;:&quot;-5&quot;},
	 *    &quot;AstronomicalTimes&quot;:{
	 *     &quot;Sunrise&quot;:&quot;2007-02-18T06:45:27-05:00&quot;,
	 *     &quot;TemporalHour&quot;:&quot;PT54M17.529S&quot;
	 *     ...
	 *     }
	 * }
	 * </pre>
	 * 
	 * Note that the output uses the <a href="http://www.w3.org/TR/xmlschema11-2/#dateTime">xsd:dateTime</a> format for
	 * times such as sunrise, and <a href="http://www.w3.org/TR/xmlschema11-2/#duration">xsd:duration</a> format for
	 * times that are a duration such as the length of a
	 * {@link net.sourceforge.zmanim.AstronomicalCalendar#getTemporalHour() temporal hour}.
	 * 
	 * @param astronomicalCalendar the AstronomicalCalendar Object
	 * 
	 * @return The JSON formatted <code>String</code>. The format will be:
	 * <pre>
	 * {
	 *    &quot;metadata&quot;:{
	 *      &quot;date&quot;:&quot;1969-02-08&quot;,
	 *      &quot;type&quot;:&quot;net.sourceforge.zmanim.AstronomicalCalendar&quot;,
	 *      &quot;algorithm&quot;:&quot;US Naval Almanac Algorithm&quot;,
	 *      &quot;location&quot;:&quot;Lakewood, NJ&quot;,
	 *      &quot;latitude&quot;:&quot;40.095965&quot;,
	 *      &quot;longitude&quot;:&quot;-74.22213&quot;,
	 *      &quot;elevation:&quot;31.0&quot;,
	 *      &quot;timeZoneName&quot;:&quot;Eastern Standard Time&quot;,
	 *      &quot;timeZoneID&quot;:&quot;America/New_York&quot;,
	 *      &quot;timeZoneOffset&quot;:&quot;-5&quot;},
	 *    &quot;AstronomicalTimes&quot;:{
	 *     &quot;Sunrise&quot;:&quot;2007-02-18T06:45:27-05:00&quot;,
	 *     &quot;TemporalHour&quot;:&quot;PT54M17.529S&quot;
	 *     ...
	 *     }
	 * }
	 * </pre>
	 */
	public static String toJSON(AstronomicalCalendar astronomicalCalendar) {
		ZmanimFormatter formatter = new ZmanimFormatter(ZmanimFormatter.XSD_DURATION_FORMAT, new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss"), astronomicalCalendar.getGeoLocation().getTimeZone());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(astronomicalCalendar.getGeoLocation().getTimeZone());

		StringBuffer sb = new StringBuffer("{\n\"metadata\":{\n");
		sb.append("\t\"date\":\"").append(df.format(astronomicalCalendar.getCalendar().getTime())).append("\",\n");
		sb.append("\t\"type\":\"").append(astronomicalCalendar.getClass().getName()).append("\",\n");
		sb.append("\t\"algorithm\":\"").append(astronomicalCalendar.getAstronomicalCalculator().getCalculatorName()).append("\",\n");
		sb.append("\t\"location\":\"").append(astronomicalCalendar.getGeoLocation().getLocationName()).append("\",\n");
		sb.append("\t\"latitude\":\"").append(astronomicalCalendar.getGeoLocation().getLatitude()).append("\",\n");
		sb.append("\t\"longitude\":\"").append(astronomicalCalendar.getGeoLocation().getLongitude()).append("\",\n");
		sb.append("\t\"elevation\":\"").append(astronomicalCalendar.getGeoLocation().getElevation()).append("\",\n");
		sb.append("\t\"timeZoneName\":\"").append(astronomicalCalendar.getGeoLocation().getTimeZone().getDisplayName()).append("\",\n");
		sb.append("\t\"timeZoneID\":\"").append(astronomicalCalendar.getGeoLocation().getTimeZone().getID()).append("\",\n");
		sb.append("\t\"timeZoneOffset\":\"")
				.append((astronomicalCalendar.getGeoLocation().getTimeZone().getOffset(astronomicalCalendar.getCalendar().getTimeInMillis()) / ((double) HOUR_MILLIS)))
				.append("\"");

		sb.append("},\n\"");
		
		if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.AstronomicalCalendar")) {
			sb.append("AstronomicalTimes");
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ComplexZmanimCalendar")) {
			sb.append("Zmanim");
		} else if (astronomicalCalendar.getClass().getName().equals("net.sourceforge.zmanim.ZmanimCalendar")) {
			sb.append("BasicZmanim");
		}
		sb.append("\":{\n");
		Method[] theMethods = astronomicalCalendar.getClass().getMethods();
		String tagName = "";
		Object value = null;
		List<Zman> dateList = new ArrayList<Zman>();
		List<Zman> durationList = new ArrayList<Zman>();
		List<String> otherList = new ArrayList<String>();
		for (int i = 0; i < theMethods.length; i++) {
			if (includeMethod(theMethods[i])) {
				tagName = theMethods[i].getName().substring(3);
				// String returnType = theMethods[i].getReturnType().getName();
				try {
					value = theMethods[i].invoke(astronomicalCalendar, (Object[]) null);
					if (value == null) {// TODO: Consider using reflection to determine the return type, not the value
						otherList.add("\"" + tagName + "\":\"N/A\",");
					} else if (value instanceof Date) {
						dateList.add(new Zman((Date) value, tagName));
					} else if (value instanceof Long || value instanceof Integer) {// shaah zmanis
						if (((Long) value).longValue() == Long.MIN_VALUE) {
							otherList.add("\"" + tagName + "\":\"N/A\"");
						} else {
							durationList.add(new Zman((int) ((Long) value).longValue(), tagName));
						}
					} else { // will probably never enter this block, but is present to be future proof
						otherList.add("\"" + tagName + "\":\"" + value + "\",");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Zman zman;
		Collections.sort(dateList, Zman.DATE_ORDER);
		for (int i = 0; i < dateList.size(); i++) {
			zman = (Zman) dateList.get(i);
			sb.append("\t\"").append(zman.getZmanLabel()).append("\":\"");
			sb.append(formatter.formatDateTime(zman.getZman(), astronomicalCalendar.getCalendar()));
			sb.append("\",\n");
		}
		Collections.sort(durationList, Zman.DURATION_ORDER);
		for (int i = 0; i < durationList.size(); i++) {
			zman = (Zman) durationList.get(i);
			sb.append("\t\"" + zman.getZmanLabel()).append("\":\"");
			sb.append(formatter.format((int) zman.getDuration())).append("\",\n");
		}

		for (int i = 0; i < otherList.size(); i++) {// will probably never enter this block
			sb.append("\t").append(otherList.get(i)).append("\n");
		}
		sb.setLength(sb.length() - 2);
		sb.append("}\n}");
		return sb.toString();
	}


	/**
	 * Determines if a method should be output by the {@link #toXML(AstronomicalCalendar)}
	 * 
	 * @param method the method in question
	 * @return if the method should be included in serialization
	 */
	private static boolean includeMethod(Method method) {
		List<String> methodWhiteList = new ArrayList<String>();
		// methodWhiteList.add("getName");

		List<String> methodBlackList = new ArrayList<String>();
		// methodBlackList.add("getGregorianChange");

		if (methodWhiteList.contains(method.getName()))
			return true;
		if (methodBlackList.contains(method.getName()))
			return false;

		if (method.getParameterTypes().length > 0)
			return false; // Skip get methods with parameters since we do not know what value to pass
		if (!method.getName().startsWith("get"))
			return false;

		if (method.getReturnType().getName().endsWith("Date") || method.getReturnType().getName().endsWith("long")) {
			return true;
		}
		return false;
	}
}
