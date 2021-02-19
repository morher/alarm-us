package net.morher.alarmus.utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeUtils {
    private static final Pattern partFormat = Pattern.compile("^\\s*(\\-?)(\\d*\\.?\\d*)([a-zA-Z]*)$");
    private static final Map<String, TimeUnit> timeUnits = createTimeUnitMap();

    private static Map<String, TimeUnit> createTimeUnitMap() {
        HashMap<String, TimeUnit> timeUnits = new HashMap<>();
        timeUnits.put("ns", TimeUnit.NANOSECONDS);
        timeUnits.put("us", TimeUnit.MICROSECONDS);
        timeUnits.put("ms", TimeUnit.MILLISECONDS);
        timeUnits.put("s", TimeUnit.SECONDS);
        timeUnits.put("m", TimeUnit.MINUTES);
        timeUnits.put("h", TimeUnit.HOURS);
        return timeUnits;
    }

    public static @Nullable Duration parseDuration(@Nullable String durationString) {
        if (durationString != null) {
            Duration duration = Duration.ZERO;
            for (String part : durationString.split("\\s+")) {
                duration = duration.plus(parseDurationPart(part));
            }
            return duration;
        }
        return null;
    }

    private static Duration parseDurationPart(@NotNull String durationPart) {
        Matcher matcher = partFormat.matcher(durationPart);
        if (matcher.matches()) {
            long sign = "-".equals(matcher.group(1)) ? -1l : 1;
            double duration = Double.parseDouble(matcher.group(2));
            TimeUnit unit = parseTimeUnit(matcher.group(3));
            double durationNs = duration * unit.toNanos(1l);

            return Duration.ofNanos(sign * (long) durationNs);
        }
        throw new IllegalArgumentException("Could not parse duration for '" + durationPart + "'");
    }

    private static TimeUnit parseTimeUnit(String timeUnitStr) {
        TimeUnit unit = timeUnits.get(timeUnitStr.toLowerCase());
        if (unit == null) {
            throw new IllegalArgumentException("Unknown time unit '" + timeUnitStr + "'");
        }
        return unit;
    }
}
