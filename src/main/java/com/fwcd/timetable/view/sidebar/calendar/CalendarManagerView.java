package com.fwcd.timetable.view.sidebar.calendar;

import com.fwcd.fructose.structs.ObservableList;
import com.fwcd.timetable.model.TimeTableAppModel;
import com.fwcd.timetable.model.calendar.CalendarModel;
import com.fwcd.timetable.view.TimeTableAppContext;
import com.fwcd.timetable.view.utils.FxUtils;
import com.fwcd.timetable.view.utils.FxView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class CalendarManagerView implements FxView {
	private final BorderPane node;
	
	private final TimeTableAppModel model;
	private final ListView<CalendarModel> calendarList;
	
	public CalendarManagerView(TimeTableAppContext context, TimeTableAppModel model) {
		this.model = model;
		
		calendarList = new ListView<>();
		calendarList.setEditable(true);
		calendarList.setCellFactory(list -> new CalendarManagerListCell());
		model.getCalendarCrate().getCalendars().listenAndFire(calendarList.getItems()::setAll);
		
		HBox controls = new HBox(
			FxUtils.buttonOf(context.localized("newcalendar"), this::createCalendar)
		);
		BorderPane.setMargin(controls, new Insets(4, 4, 4, 4));
		
		node = new BorderPane();
		node.setTop(controls);
		node.setCenter(calendarList);
	}
	
	private void createCalendar() {
		ObservableList<CalendarModel> calendars = model.getCalendarCrate().getCalendars();
		calendars.add(new CalendarModel(""));
		calendarList.edit(calendars.size() - 1);
	}
	
	@Override
	public Node getNode() { return node; }
}
