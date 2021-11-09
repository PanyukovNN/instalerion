package org.union.common.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    DateTimeHelper.class,
    LocalDateTime.class,
})
@PowerMockIgnore("javax.management.*")
public class DateTimeHelperTest {

    private final DateTimeHelper helper = new DateTimeHelper();

    private final LocalDateTime now = LocalDateTime.of(2021, 11, 9, 12, 0);

    @Before
    public void setUp() {
        mockStatic(LocalDateTime.class);

        when(LocalDateTime.now(ZoneId.of("Europe/Moscow"))).thenReturn(this.now);
    }

    @Test
    public void getCurrentDateTime() {
        LocalDateTime returnedDateTime = helper.getCurrentDateTime();

        assertEquals(now, returnedDateTime);
    }

    @Test
    public void minuteFromNow() {

    }

    @Test
    public void isNight() {


    }

    @Test
    public void formatFrontDateTime() {
    }
}
