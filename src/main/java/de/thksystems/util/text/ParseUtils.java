/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, http://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 */
package de.thksystems.util.text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Just about parsing text ...
 */
public final class ParseUtils {

    public static final BigDecimal VALUE_BIGDECIMAL_MINUSONE = new BigDecimal("-1");
    public static final BigDecimal VALUE_BIGDECIMAL_LONGMAX = new BigDecimal(Long.MAX_VALUE);

    public static final String INFINITE = "∞";

    private static final Pattern FILESIZE_PATTERN = Pattern.compile("^([0-9.]+)([ETGMK]B?)$", Pattern.CASE_INSENSITIVE);

    private static final String DURATION_PATTERN_STRING = "(-?[0-9.]+)(ms|s|m|h|d|w|M|y)?\\s*";
    private static final Pattern DURATION_PATTERN = Pattern.compile(DURATION_PATTERN_STRING);
    private static final Pattern DURATION_PATTERN_WHOLE = Pattern.compile("^(" + DURATION_PATTERN_STRING + ")+$");

    private static Map<String, Integer> fileSizePowMap = null;
    private static Map<String, BigDecimal> durationFactorMap = null;

    private ParseUtils() {
    }

    /**
     * Parse filesize given as a {@link String} (e.g. '0.003EB', '2.3GB', '5M', '30', '705.23kB') and return the size in bytes as {@link BigDecimal}.
     *
     * @param filesize Size as {@link String} (KB, MB, GB, TB, EB are supported as suffixes, case-insensitive, the 'B' may be omitted; If no suffix is given, the
     *                 filesize is interpreted as bytes; Negative values are not supported.)
     * @return size in bytes as {@link BigDecimal} (<code>null</code>, if an invalid 'filesize' is given.)
     */
    public static BigDecimal parseFileSize(String filesize) {
        if (filesize == null || StringUtils.isBlank(filesize)) {
            return null;
        }
        Matcher matcher = FILESIZE_PATTERN.matcher(filesize);
        if (matcher.find()) {
            String number = matcher.group(1);
            int pow = getFileSizePowMap().get(matcher.group(2).toUpperCase());
            BigDecimal bytes = new BigDecimal(number);
            bytes = bytes.multiply(BigDecimal.valueOf(1024).pow(pow));
            return bytes;
        }
        try {
            BigDecimal value = new BigDecimal(filesize);
            if (value.compareTo(VALUE_BIGDECIMAL_MINUSONE) <= 0) {
                return null;
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Lazy get fileSizePowMap.
     */
    private synchronized static Map<String, Integer> getFileSizePowMap() {
        if (fileSizePowMap == null) {
            fileSizePowMap = new HashMap<>();
            fileSizePowMap.put("EB", 5);
            fileSizePowMap.put("TB", 4);
            fileSizePowMap.put("GB", 3);
            fileSizePowMap.put("MB", 2);
            fileSizePowMap.put("KB", 1);
            fileSizePowMap.put("E", 5);
            fileSizePowMap.put("T", 4);
            fileSizePowMap.put("G", 3);
            fileSizePowMap.put("M", 2);
            fileSizePowMap.put("K", 1);
        }
        return fileSizePowMap;
    }

    /**
     * Parse duration.
     * <p>
     * Input-Pattern: [0-9]+(ms|s|m|h|d|w|M|y), e.g. "50ms", "133453m", "7d", "13M", "18.3s", "5m 3s", "2h 30m 15.4s", "∞"
     * <p>
     * Units:
     * <ul>
     * <li>ms -&gt; milliseconds</li>
     * <li>s -&gt; seconds</li>
     * <li>m -&gt; minutes</li>
     * <li>h -&gt; hours</li>
     * <li>d -&gt; days</li>
     * <li>w -&gt; weeks</li>
     * <li>M -&gt; months (30 days)</li>
     * <li>y -&gt; years (365 days)</li>
     * <li>∞ -&gt; {@link Long#MAX_VALUE}</li>
     * </ul>
     *
     * @return duration in milliseconds
     */
    public static BigDecimal parseDuration(String durationString) {
        if (durationString == null || StringUtils.isBlank(durationString)) {
            return null;
        }
        durationString = durationString.trim();
        if (INFINITE.equals(durationString)) {
            return VALUE_BIGDECIMAL_LONGMAX;
        }
        if (StringUtils.isNotEmpty(durationString) && DURATION_PATTERN_WHOLE.matcher(durationString).matches()) {
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal prevFactor = null;
            Matcher matcher = DURATION_PATTERN.matcher(durationString);
            while (matcher.find()) {
                BigDecimal value = new BigDecimal(matcher.group(1));
                BigDecimal factor = getDurationFactorMap().get(matcher.group(2));
                if (factor == null) {
                    throw new IllegalArgumentException("Invalid duration expression: " + durationString);
                }
                BigDecimal result = value.multiply(factor);
                if (prevFactor == null || prevFactor.compareTo(result) > 0) {
                    sum = sum.add(result);
                    prevFactor = factor;
                } else {
                    throw new IllegalArgumentException("Duration expression must not overlap: " + durationString);
                }
            }
            return sum;
        } else {
            throw new IllegalArgumentException("Invalid duration expression: " + durationString);
        }
    }

    /**
     * Lazy get durationFactorMap.
     */
    private synchronized static Map<String, BigDecimal> getDurationFactorMap() {
        if (durationFactorMap == null) {
            durationFactorMap = new HashMap<>();
            durationFactorMap.put(null, new BigDecimal(1L));
            durationFactorMap.put("ms", new BigDecimal(1L));
            durationFactorMap.put("s", new BigDecimal(1_000L));
            durationFactorMap.put("m", new BigDecimal(1_000L * 60L));
            durationFactorMap.put("h", new BigDecimal(1_000L * 60L * 60L));
            durationFactorMap.put("d", new BigDecimal(1_000L * 60L * 60L * 24L));
            durationFactorMap.put("w", new BigDecimal(1_000L * 60L * 60L * 24L * 7L));
            durationFactorMap.put("M", new BigDecimal(1_000L * 60L * 60L * 24L * 30L));
            durationFactorMap.put("y", new BigDecimal(1_000L * 60L * 60L * 24L * 365L));
        }
        return durationFactorMap;
    }

    /**
     * Returns the duration pattern used by {@link #parseDuration(String)}
     */
    public static String getDurationPatternString() {
        return DURATION_PATTERN_STRING;
    }

}