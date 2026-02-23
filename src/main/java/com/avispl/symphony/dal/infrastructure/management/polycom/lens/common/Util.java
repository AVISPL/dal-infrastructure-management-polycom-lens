/** Copyright (c) 2026 AVI-SPL, Inc. All Rights Reserved. */
package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for this adapter. This class includes helper methods to extract and convert properties.
 * <p>This class is non-instantiable and provides only static utility methods.</p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Util {
	private static final Log LOG = LogFactory.getLog(Util.class);

	private Util() {
	}

	/**
	 * Returns the elapsed uptime between the current system time and the given timestamp in milliseconds.
	 * <p>
	 * The input timestamp represents the start time in milliseconds (typically from {@link System#currentTimeMillis()}).
	 * The returned string represents the absolute duration in the format:
	 * "X d Y hr Z min W sec", omitting any zero-value units except seconds.
	 *
	 * @param uptime the start time in milliseconds (e.g., 1717581000000)
	 * @return a formatted duration string like "2 d 3 hr 15 min 42 sec", or null if parsing fails
	 */
	public static String mapToUptime(long uptime) {
		try {

			long uptimeSecond = (System.currentTimeMillis() - uptime) / 1000;
			long seconds = uptimeSecond % 60;
			long minutes = uptimeSecond % 3600 / 60;
			long hours = uptimeSecond % 86400 / 3600;
			long days = uptimeSecond / 86400;
			StringBuilder rs = new StringBuilder();
			if (days > 0) {
				rs.append(days).append(" d ");
			}
			if (hours > 0) {
				rs.append(hours).append(" hr ");
			}
			if (minutes > 0) {
				rs.append(minutes).append(" min ");
			}
			rs.append(seconds).append(" sec");

			return rs.toString().trim();
		} catch (Exception e) {
			LOG.error("Failed to mapToUptime with uptime: " + uptime, e);
			return PolyLensConstant.NOT_AVAILABLE;
		}
	}

	/**
	 * Returns the elapsed uptime in **whole minutes** between the current system time and the given timestamp in milliseconds.
	 * <p>
	 * The input timestamp represents the start time in milliseconds (typically from {@link System#currentTimeMillis()}).
	 * The returned string is the total number of minutes that have elapsed, excluding seconds.
	 *
	 * @param uptime the start time in milliseconds (e.g., 1717581000000)
	 * @return a string representing the total number of elapsed minutes (e.g., "125"), or null if parsing fails
	 */
	public static String mapToUptimeMin(long uptime) {
		try {
			long uptimeSecond = (System.currentTimeMillis() - uptime) / 1000;
			long minutes = uptimeSecond / 60;

			return String.valueOf(minutes);
		} catch (Exception e) {
			LOG.error("Failed to mapToUptimeMin with uptime: " + uptime, e);
			return PolyLensConstant.NOT_AVAILABLE;
		}
	}
}
