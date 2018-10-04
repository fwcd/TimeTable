package com.fwcd.timetable.view.utils;

import com.fwcd.timetable.model.calendar.CalendarEntryModel;

import javafx.scene.Node;
import javafx.scene.control.ListView;

public class CalendarEntryListView implements FxView {
	private final ListView<CalendarEntryModel> node;
	
	public CalendarEntryListView() {
		node = new ListView<>();
		node.setCellFactory(list -> new CalendarEntryListCell());
	}
	
	@Override
	public Node getNode() { return node; }
}
