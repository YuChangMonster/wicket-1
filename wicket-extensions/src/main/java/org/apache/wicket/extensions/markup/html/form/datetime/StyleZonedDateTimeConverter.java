/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.extensions.markup.html.form.datetime;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Date converter that uses javax.time and can be configured to take the time zone difference between
 * clients and server into account, and that is configured for a certain date style. The pattern
 * will always be locale specific.
 * <p>
 * This converter is especially suited on a per-component base.
 * </p>
 * 
 * @see org.apache.wicket.extensions.markup.html.form.DateTextField
 * @see java.time.ZonedDateTime
 * @see DateTimeFormatter
 * @see java.time.ZoneId
 * 
 * @author eelcohillenius
 */
public class StyleZonedDateTimeConverter extends ZonedDateTimeConverter
{
	private static final long serialVersionUID = 1L;

	/**
	 * Date style to use. See {@link DateTimeFormatter#ofLocalizedDate(FormatStyle)}.
	 */
	private final FormatStyle dateStyle;

	private final FormatStyle timeStyle;

	/**
	 * Construct. The dateStyle 'S-' (which is the same as {@link DateTimeFormatter#ofLocalizedDate(FormatStyle)}) will
	 * be used for constructing the date format for the current locale. </p> When
	 * applyTimeZoneDifference is true, the current time is applied on the parsed date, and the date
	 * will be corrected for the time zone difference between the server and the client. For
	 * instance, if I'm in Seattle and the server I'm working on is in Amsterdam, the server is 9
	 * hours ahead. So, if I'm inputting say 12/24 at a couple of hours before midnight, at the
	 * server it is already 12/25. If this boolean is true, it will be transformed to 12/25, while
	 * the client sees 12/24. </p>
	 * 
	 * @param applyTimeZoneDifference
	 *            whether to apply the difference in time zones between client and server
	 */
	public StyleZonedDateTimeConverter(boolean applyTimeZoneDifference)
	{
		this(FormatStyle.SHORT, null, applyTimeZoneDifference);
	}

	/**
	 * Construct. The provided pattern will be used as the base format (but they will be localized
	 * for the current locale) and if null, {@link DateTimeFormatter#ofLocalizedDate(FormatStyle)} will be used. </p>
	 * When applyTimeZoneDifference is true, the current time is applied on the parsed date, and the
	 * date will be corrected for the time zone difference between the server and the client. For
	 * instance, if I'm in Seattle and the server I'm working on is in Amsterdam, the server is 9
	 * hours ahead. So, if I'm inputting say 12/24 at a couple of hours before midnight, at the
	 * server it is already 12/25. If this boolean is true, it will be transformed to 12/25, while
	 * the client sees 12/24. </p>
	 * 
	 * @param dateStyle
	 *            Date style to use. See {@link DateTimeFormatter#ofLocalizedDate(FormatStyle)}.
	 * @param timeStyle
	 *            Time style to use. See {@link DateTimeFormatter#ofLocalizedTime(FormatStyle)}
	 * @param applyTimeZoneDifference
	 *            whether to apply the difference in time zones between client and server
	 * @throws IllegalArgumentException
	 *             in case dateStyle is null
	 */
	public StyleZonedDateTimeConverter(FormatStyle dateStyle, FormatStyle timeStyle, boolean applyTimeZoneDifference)
	{
		super(applyTimeZoneDifference);
		this.dateStyle = dateStyle;
		this.timeStyle = timeStyle;
	}

	public StyleZonedDateTimeConverter(String dateTimeStyle, boolean applyTimeZoneDifference)
	{
		this(parseFormatStyle(dateTimeStyle.charAt(0)), parseFormatStyle(dateTimeStyle.charAt(1)), applyTimeZoneDifference);
	}

	/**
	 * Gets the optional date pattern.
	 * 
	 * @return datePattern
	 */
	@Override
	public final String getPattern(Locale locale)
	{
		String localizedDateTimePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(dateStyle, timeStyle, IsoChronology.INSTANCE, locale);
		return localizedDateTimePattern;
	}

	/**
	 * @return formatter The formatter for the current conversion
	 */
	@Override
	public DateTimeFormatter getFormat(Locale locale)
	{
		DateTimeFormatter df = null;
		if (dateStyle == null && timeStyle == null) {
			return df;
		}
		if (timeStyle == null)
		{
			df = DateTimeFormatter.ofLocalizedDate(dateStyle);
		}
		else if (dateStyle == null)
		{
			df = DateTimeFormatter.ofLocalizedTime(timeStyle);
		}
		else
		{
			df = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle);
		}
		return df.withLocale(locale);
	}

	public static FormatStyle parseFormatStyle(char style)
	{
		return DateField.parseFormatStyle(style);
	}

	@Override
	public ZonedDateTime convertToObject(String value, DateTimeFormatter format, Locale locale) {
		if (format == null) {
			return null;
		}
		try
		{
			if (timeStyle == null)
			{
				LocalDate d = LocalDate.parse(value, format);
				return ZonedDateTime.of(d.atStartOfDay(), getTimeZone());
			}
			else if (dateStyle == null)
			{
				// not sure how we can get ZonedDateTime from time
				return null;
			}
			return super.convertToObject(value, format, locale);
		}
		catch (RuntimeException e)
		{
			throw newConversionException(e, locale);
		}
	}
}
