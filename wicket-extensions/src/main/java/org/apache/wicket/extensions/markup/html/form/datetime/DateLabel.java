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
import java.time.format.FormatStyle;
import java.util.Date;

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Args;


/**
 * A label that is mapped to a {@link java.time.LocalDate} object and that uses
 * {@link java.time.format.DateTimeFormatter} to format values.
 * <p>
 * You can provide a date pattern in two of the constructors. When not provided,
 * {@link java.time.format.FormatStyle#SHORT} will be used.
 * </p>
 * <p>
 * A special option is applyTimeZoneDifference which is an option that says whether to correct for
 * the difference between the client's time zone and server's time zone. This is true by default.
 * </p>
 * 
 * @see java.time.LocalDate
 * @see java.time.format.DateTimeFormatter
 */
public class DateLabel extends Label implements IGenericComponent<Date, DateLabel>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @param model
	 *            The model
	 * @param datePattern
	 *            The pattern to use. Must be not null. See
	 *            {@link java.time.format.DateTimeFormatter} for available patterns.
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forDatePattern(String id, IModel<LocalDate> model, String datePattern)
	{
		return new DateLabel(id, model, new PatternDateConverter(datePattern, true));
	}

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @param datePattern
	 *            The pattern to use. Must be not null. See
	 *            {@link java.time.format.DateTimeFormatter} for available patterns.
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forDatePattern(String id, String datePattern)
	{
		return forDatePattern(id, null, datePattern);
	}

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @param model
	 *            The model
	 * @param dateStyle
	 *            style to use in case no pattern is provided.
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forDateStyle(String id, IModel<LocalDate> model, FormatStyle dateStyle)
	{
		return new DateLabel(id, model, new StyleDateConverter(dateStyle, null, true));
	}

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @param dateStyle
	 *            style to use in case no pattern is provided.
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forDateStyle(String id, FormatStyle dateStyle)
	{
		return forDateStyle(id, null, dateStyle);
	}

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forShortStyle(String id)
	{
		return forShortStyle(id, null);
	}

	/**
	 * Creates a new DateLabel defaulting to using a short date pattern
	 * 
	 * @param id
	 *            The id of the text field
	 * @param model
	 *            The model
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel forShortStyle(String id, IModel<LocalDate> model)
	{
		return new DateLabel(id, model, new StyleDateConverter(true));
	}

	/**
	 * Creates a new DateLabel using the provided converter.
	 * 
	 * @param id
	 *            The id of the text field
	 * @param converter
	 *            the date converter
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel withConverter(String id, DateConverter converter)
	{
		return withConverter(id, null, converter);
	}

	/**
	 * Creates a new DateLabel using the provided converter.
	 * 
	 * @param id
	 *            The id of the text field
	 * @param model
	 *            The model
	 * @param converter
	 *            the date converter
	 * @return new instance
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public static DateLabel withConverter(String id, IModel<LocalDate> model, DateConverter converter)
	{
		return new DateLabel(id, model, converter);
	}

	/** optionally prepend to label. */
	private String after;

	/** optionally append to label. */
	private String before;

	/**
	 * The converter for the Label
	 */
	private final DateConverter converter;

	/**
	 * Construct with a converter.
	 * 
	 * @param id
	 *            The component id
	 * @param converter
	 *            The converter to use
	 */
	public DateLabel(String id, DateConverter converter)
	{
		this(id, null, converter);
	}

	/**
	 * Construct with a converter.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param converter
	 *            The converter to use
	 */
	public DateLabel(String id, IModel<LocalDate> model, DateConverter converter)
	{
		super(id, model);
		this.converter = Args.notNull(converter, "converter");
	}

	/**
	 * @return after append to label or null
	 */
	public String getAfter()
	{
		return after;
	}

	/**
	 * @return before prepend to label or null
	 */
	public String getBefore()
	{
		return before;
	}

	/**
	 * Returns the specialized converter.
	 */
	@Override
	protected IConverter<?> createConverter(Class<?> type)
	{
		if (Date.class.isAssignableFrom(type))
		{
			return converter;
		}
		return null;
	}

	/**
	 * @param after
	 *            append to label
	 */
	public void setAfter(String after)
	{
		this.after = after;
	}

	/**
	 * @param before
	 *            prepend to label
	 */
	public void setBefore(String before)
	{
		this.before = before;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
	{
		String s = getDefaultModelObjectAsString();
		if (before != null)
		{
			s = before + s;
		}
		if (after != null)
		{
			s = s + after;
		}
		replaceComponentTagBody(markupStream, openTag, s);
	}
}
