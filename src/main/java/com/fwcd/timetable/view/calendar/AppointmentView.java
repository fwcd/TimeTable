package com.fwcd.timetable.view.calendar;

import com.fwcd.timetable.model.calendar.AppointmentModel;
import com.fwcd.timetable.view.utils.FxView;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class AppointmentView implements FxView {
	private final Pane node;
	
	public AppointmentView(AppointmentModel model) {
		node = new Pane();
		node.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(3), Insets.EMPTY)));
		
		Label label = new Label();
		label.setText(model.getName());
		node.getChildren().add(label);
	}
	
	@Override
	public Node getNode() { return node; }
}
