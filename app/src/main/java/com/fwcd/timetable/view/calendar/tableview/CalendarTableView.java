package com.fwcd.timetable.view.calendar.tableview;

import java.util.stream.Collectors;

import com.fwcd.timetable.model.calendar.AppointmentModel;
import com.fwcd.timetable.view.utils.FxNavigableView;
import com.fwcd.timetable.viewmodel.TimeTableAppContext;
import com.fwcd.timetable.viewmodel.calendar.CalendarsViewModel;

import javafx.scene.Node;
import javafx.scene.control.TableView;

public class CalendarTableView implements FxNavigableView {
	private final TableView<AppointmentModel> node;
	
	public CalendarTableView(TimeTableAppContext context, CalendarsViewModel calendars) {
		node = new TableView<>();
		calendars.getChangeListeners().add(it -> updateEntries(calendars));
	}
	
	private void updateEntries(CalendarsViewModel calendars) {
		node.getItems().setAll(calendars.getSelectedCalendars()
			.stream()
			.flatMap(it -> it.getAppointments().stream())
			.collect(Collectors.toList())
		);
	}

	@Override
	public Node getContentNode() { return node; }
}