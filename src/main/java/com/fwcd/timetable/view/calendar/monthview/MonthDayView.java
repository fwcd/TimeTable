package com.fwcd.timetable.view.calendar.monthview;

import java.time.LocalDate;
import java.util.stream.Collectors;

import com.fwcd.timetable.view.utils.FxView;
import com.fwcd.timetable.view.utils.SubscriptionStack;
import com.fwcd.timetable.viewmodel.calendar.CalendarsViewModel;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MonthDayView implements FxView, AutoCloseable {
	private final BorderPane node;
	private final Label indexLabel;
	private final VBox content;
	
	private final LocalDate date;
	private final CalendarsViewModel calendars;
	
	private final SubscriptionStack subscriptions = new SubscriptionStack();
	
	public MonthDayView(CalendarsViewModel calendars, LocalDate date) {
		this.calendars = calendars;
		this.date = date;
		
		node = new BorderPane();
		
		indexLabel = new Label(Integer.toString(date.getDayOfMonth()));
		indexLabel.setFont(Font.font(null, FontWeight.BOLD, 14));
		BorderPane.setAlignment(indexLabel, Pos.TOP_CENTER);
		node.setTop(indexLabel);
		
		content = new VBox();
		node.setCenter(content);
		
		subscriptions.push(calendars.getChangeListeners().subscribe(it -> updateView()));
	}
	
	private void updateView() {
		content.getChildren().setAll(calendars.getSelectedCalendars().stream()
			.flatMap(it -> it.getAppointments().stream())
			.filter(it -> it.occursOn(date))
			.map(it -> new Label(it.getName().get()))
			.collect(Collectors.toList())
		);
	}
	
	@Override
	public void close() {
		subscriptions.unsubscribeAll();
	}
	
	@Override
	public Node getNode() { return node; }
}
