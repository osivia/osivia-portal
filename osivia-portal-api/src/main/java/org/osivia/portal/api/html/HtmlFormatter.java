package org.osivia.portal.api.html;

import java.text.NumberFormat;
import java.util.Locale;

import org.osivia.portal.api.internationalization.Bundle;

/**
 * HTML common formatters
 * 
 * @author Jean-Sébastien
 *
 */

public class HtmlFormatter {

	/** File size units. */
	private static final String[] UNITS = { "BYTE", "KILOBYTE", "MEGABYTE", "GIGABYTE", "TERABYTE" };
	/** Unit factor. */
	private static final double UNIT_FACTOR = 1024;

	/**
	 * format file size
	 * 
	 * @param locale
	 * @param bundle
	 * @param size
	 * @return
	 */
	public static String formatSize(Locale locale, Bundle bundle, long size) {

		StringBuffer res = new StringBuffer();

		if (size > 0) {
			// Factor
			int factor = Double.valueOf(Math.log10(size) / Math.log10(UNIT_FACTOR)).intValue();
			// Factorized size
			double factorizedSize = size / Math.pow(UNIT_FACTOR, factor);
			// Unit
			String unit = bundle.getString(UNITS[factor]);
			// Number format
			NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
			numberFormat.setMaximumFractionDigits(1);

			res.append(numberFormat.format(factorizedSize));
			res.append("&nbsp;");
			res.append(unit);
		} else {
			res.append("0&nbsp;");
			res.append(bundle.getString(UNITS[0]));
		}

		return res.toString();
	}

}
