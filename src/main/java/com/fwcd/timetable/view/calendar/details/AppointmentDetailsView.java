package com.fwcd.timetable.view.calendar.details;

import com.fwcd.fructose.Option;
import com.fwcd.fructose.time.LocalDateTimeInterval;
import com.fwcd.timetable.model.calendar.AppointmentModel;
import com.fwcd.timetable.model.calendar.Location;
import com.fwcd.timetable.view.utils.FxUtils;
import com.fwcd.timetable.view.utils.FxView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import tornadofx.control.DateTimePicker;

public class AppointmentDetailsView implements FxView {
	private final VBox node;
	
	public AppointmentDetailsView(AppointmentModel model) {
		TextField title = new TextField();
		FxUtils.bindBidirectionally(model.getName(), title.textProperty());
		title.setFont(Font.font(14));
		
		TextField location = new TextField();
		FxUtils.bindBidirectionally(
			model.getLocation(),
			location.textProperty(),
			optLocation -> optLocation.map(Location::getLabel).orElse(""),
			newLocation -> Option.of(newLocation).filter(it -> !it.isEmpty()).map(Location::new)
		);
		location.setFont(Font.font(12));
		
		GridPane properties = new GridPane();
		
		DateTimePicker start = new DateTimePicker();
		FxUtils.bindBidirectionally(
			model.getDateTimeInterval(),
			start.dateTimeValueProperty(),
			interval -> interval.getStart(),
			dateTime -> new LocalDateTimeInterval(dateTime, model.getEnd())
		);
		properties.addRow(1, new Label("Start: "), start);
		
		DateTimePicker end = new DateTimePicker();
		FxUtils.bindBidirectionally(
			model.getDateTimeInterval(),
			end.dateTimeValueProperty(),
			interval -> interval.getEnd(),
			dateTime -> new LocalDateTimeInterval(model.getStart(), dateTime)
		);
		properties.addRow(2, new Label("End: "), end);
		
		TextField recurrence = new TextField();
		FxUtils.bindBidirectionally(model.getRecurrence().getRaw(), recurrence.textProperty());
		properties.addRow(3, new Label("Recurrence: "), recurrence);
		
		CheckBox ignoreDate = new CheckBox();
		FxUtils.bindBidirectionally(model.ignoresDate(), ignoreDate.selectedProperty());
		properties.addRow(4, new Label("Ignore Date: "), ignoreDate);
		
		CheckBox ignoreTime = new CheckBox();
		FxUtils.bindBidirectionally(model.ignoresTime(), ignoreTime.selectedProperty());
		properties.addRow(5, new Label("Ignore Time: "), ignoreTime);
		
		node = new VBox(
			title,
			location,
			properties
		);
		node.setPadding(new Insets(10, 10, 10, 10));
	}
	
	@Override
	public Node getNode() { return node; }
}
