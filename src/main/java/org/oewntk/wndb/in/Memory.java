/*
 * Copyright (c) 2021-2021. Bernard Bou.
 */

package org.oewntk.wndb.in;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Heap Memory utilities
 *
 * @author Bernard Bou
 */
public class Memory
{
	private Memory()
	{
	}

	public enum Unit
	{
		U(1), K(1024 * U.div), M(1024 * K.div), G(1024 * M.div);

		public final long div;

		Unit(final long div)
		{
			this.div = div;
		}
	}

	static DecimalFormat formatter()
	{
		final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		final DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter;
	}

	public static String heapInfo(final String tag, final Unit u)
	{
		final long max = Runtime.getRuntime().maxMemory();
		final long total = Runtime.getRuntime().totalMemory();
		final long free = Runtime.getRuntime().freeMemory();
		long used = total - free;
		final long future = max - total;
		long avail = free + future;

		used /= u.div;
		avail /= u.div;

		final DecimalFormat formatter = Memory.formatter();
		return String.format("[Heap] %s used: %s%s maxfree: %s%s", //
				tag, //
				formatter.format(used), u, //
				formatter.format(avail), u //
		);
	}

	public static String memoryInfo(final String tag, final Unit u)
	{
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;
		final long future = max - total;
		long avail = free + future;

		max /= u.div; // This will return Long.MAX_VALUE if there is no preset limit
		total /= u.div;
		free /= u.div;
		used /= u.div;
		avail /= u.div;

		final DecimalFormat formatter = Memory.formatter();
		return String.format("Memory [%s] max=%15s%s total=%10s%s used=%15s%s free=%15s%s avail=%15s%s", //
				tag, //
				formatter.format(max), u, //
				formatter.format(total), u, //
				formatter.format(used), u, //
				formatter.format(free), u, //
				formatter.format(avail), u);
	}
}
