package ru.pixelmongo.pixelmongo.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.pixelmongo.pixelmongo.services.TimeService;

@Service("timeService")
public class TimeServiceImpl implements TimeService{

    @Autowired
    private TimeZone timeZone;

    private long lastUpdate = 0;

    private Date monthStart, weekStart, dayStart;

    private Calendar getCalendarForNow() {
        return GregorianCalendar.getInstance(timeZone);
    }

    private void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private Date calcMonthStartTime() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date calcWeekStartTime() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private Date calcDayStartTime() {
        Calendar calendar = getCalendarForNow();
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    private void checkForUpdate() {
        long now = System.currentTimeMillis();
        if(lastUpdate+10000 < now) {
            lastUpdate = now;
            monthStart = calcMonthStartTime();
            weekStart = calcWeekStartTime();
            dayStart = calcDayStartTime();
        }
    }

    @Override
    public Date getDayStart() {
        checkForUpdate();
        return dayStart;
    }

    @Override
    public Date getWeekStart() {
        checkForUpdate();
        return weekStart;
    }

    @Override
    public Date getMonthStart() {
        checkForUpdate();
        return monthStart;
    }



}
