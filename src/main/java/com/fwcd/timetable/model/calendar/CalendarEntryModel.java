package com.fwcd.timetable.model.calendar;

import com.fwcd.fructose.Observable;

public interface CalendarEntryModel {
	String getType();
	
	Observable<String> getName();
}
