package net.morher.alarmus.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;

import org.junit.Test;

public class TimeUtilsTest {

    @Test
    public void testParseDuration() {
        Duration duration;

        duration = TimeUtils.parseDuration("1ms");
        assertThat(duration.getSeconds(), is(equalTo(0l)));
        assertThat(duration.getNano(), is(equalTo(1000000)));

        duration = TimeUtils.parseDuration("1.9s 2.3ms");
        assertThat(duration.getSeconds(), is(equalTo(1l)));
        assertThat(duration.getNano(), is(equalTo(902300000)));

        duration = TimeUtils.parseDuration("5h 123.17s 2.3us");
        assertThat(duration.getSeconds(), is(equalTo(123l + 5 * 60 * 60)));
        assertThat(duration.getNano(), is(equalTo(170002300)));

        duration = TimeUtils.parseDuration("5h 1.9s -1ns");
        assertThat(duration.getSeconds(), is(equalTo(1l + 5 * 60 * 60)));
        assertThat(duration.getNano(), is(equalTo(899999999)));
    }

}
