package com.fwcd.timetable.view.calendar.weekview;

import java.time.format.DateTimeFormatter;

import com.fwcd.fructose.time.LocalDateTimeInterval;
import com.fwcd.timetable.model.calendar.AppointmentModel;
import com.fwcd.timetable.model.calendar.CalendarModel;
import com.fwcd.timetable.view.calendar.popover.AppointmentDetailsView;
import com.fwcd.timetable.view.utils.FxUtils;
import com.fwcd.timetable.view.utils.FxView;
import com.fwcd.timetable.viewmodel.TimeTableAppContext;

import org.controlsfx.control.PopOver;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppointmentView implements FxView {
	private final Pane node;
	
	public AppointmentView(WeekDayTimeLayouter layouter, TimeTableAppContext context, CalendarModel calendar, AppointmentModel model) {
		Color fgColor = Color.BLACK;
		
		node = new VBox();
		node.setMinWidth(0);
		calendar.getColor().listenAndFire(it -> node.setBackground(new Background(new BackgroundFill(brightColor(FxUtils.toFxColor(it)), new CornerRadii(3), Insets.EMPTY))));
		node.getStyleClass().add("appointment");
		
		Label nameLabel = new Label();
		nameLabel.setFont(Font.font(null, FontWeight.BOLD, 12));
		nameLabel.setTextFill(fgColor);
		nameLabel.setWrapText(true);
		model.getName().listenAndFire(nameLabel::setText);
		node.getChildren().add(nameLabel);
		
		Label timeLabel = new Label();
		timeLabel.setFont(Font.font(11));
		timeLabel.setTextFill(fgColor);
		model.getDateTimeInterval().listenAndFire(it -> timeLabel.setText(formatTimeInterval(it, context.getTimeFormatter().get())));
		node.getChildren().add(timeLabel);
		
		PopOver popOver = FxUtils.newPopOver(new AppointmentDetailsView(calendar, context, model));
		node.setOnMouseClicked(e -> {
			FxUtils.showIndependentPopOver(popOver, node);
			e.consume();
		});
	}
	
	private String formatTimeInterval(LocalDateTimeInterval interval, DateTimeFormatter formatter) {
		return formatter.format(interval.getStart()) + " - " + formatter.format(interval.getEnd());
	}

	private Color brightColor(Color calColor) {
		double amount = 0.5;
		double r = calColor.getRed() + amount;
		double g = calColor.getGreen() + amount;
		double b = calColor.getBlue() + amount;
		double f = max(1.0, r, g, b); // Only scale if r, g or b is > 1.0
		return new Color(r / f, g / f, b / f, calColor.getOpacity());
	}
	
	private double max(double a, double b, double c, double d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	@Override
	public Pane getNode() { return node; }
}
