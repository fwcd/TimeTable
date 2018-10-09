package com.fwcd.timetable.view.calendar.weekview;

import java.time.LocalTime;

import com.fwcd.fructose.Option;
import com.fwcd.fructose.structs.ObservableList;
import com.fwcd.timetable.model.calendar.CalendarModel;
import com.fwcd.timetable.view.TimeTableAppContext;
import com.fwcd.timetable.view.utils.FxNavigableView;
import com.fwcd.timetable.view.utils.FxUtils;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class WeekView implements FxNavigableView {
	private final Node navigatorNode;
	private final ScrollPane contentNode;
	private final WeekDayTimeLayouter timeLayouter = new WeekDayTimeLayouter();
	
	public WeekView(TimeTableAppContext context, ObservableList<CalendarModel> calendars) {
		WeekContentView content = new WeekContentView(timeLayouter, calendars);
		contentNode = new ScrollPane(content.getNode());
		navigatorNode = new WeekNavigatorView(context, content).getNode();
		
		FxUtils.setVerticalScrollSpeed(contentNode, 2);
		contentNode.setFitToWidth(true);
		
		// Automatically scroll to the current time indicator
		double vmax = contentNode.getVmax();
		double vmin = contentNode.getVmin();
		double normalizedValue = timeLayouter.toPixelY(LocalTime.now()) / timeLayouter.toPixelY(LocalTime.MAX);
		contentNode.setVvalue((normalizedValue * (vmax - vmin)) + vmin);
	}
	
	@Override
	public Node getContent() { return contentNode; }
	
	@Override
	public Option<Node> getNavigationBar() { return Option.of(navigatorNode); }
}
