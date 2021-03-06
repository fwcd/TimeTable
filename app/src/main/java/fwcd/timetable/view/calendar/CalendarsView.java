package fwcd.timetable.view.calendar;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import fwcd.timetable.model.calendar.AppointmentModel;
import fwcd.timetable.model.calendar.CalendarGsonConfigurator;
import fwcd.timetable.model.json.GsonUtils;
import fwcd.timetable.view.FxView;
import fwcd.timetable.view.calendar.listview.CalendarListView;
import fwcd.timetable.view.calendar.monthview.MonthView;
import fwcd.timetable.view.calendar.tableview.CalendarTableView;
import fwcd.timetable.view.calendar.weekview.WeekView;
import fwcd.timetable.view.utils.NavigableTabPane;
import fwcd.timetable.viewmodel.TimeTableAppContext;
import fwcd.timetable.viewmodel.calendar.CalendarCrateViewModel;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;

public class CalendarsView implements FxView {
	private static final Gson CLIPBOARD_GSON = GsonUtils.DEFAULT_CONFIGURATOR.andThen(new CalendarGsonConfigurator()).create();
	private final Node node;
	private final WeekView weekView;
	private final MonthView monthView;
	private final CalendarListView listView;
	private final CalendarTableView tableView;
	
	public CalendarsView(TimeTableAppContext context, CalendarCrateViewModel viewModel) {
		weekView = new WeekView(context, viewModel);
		monthView = new MonthView(context, viewModel);
		listView = new CalendarListView(context, viewModel);
		tableView = new CalendarTableView(context, viewModel);
		
		NavigableTabPane tabPane = new NavigableTabPane();
		tabPane.addTab(context.localized("week"), weekView);
		tabPane.addTab(context.localized("month"), monthView);
		tabPane.addTab(context.localized("list"), listView);
		tabPane.addTab(context.localized("table"), tableView);
		node = tabPane.getNode();
		node.setOnDragOver(e -> {
			e.acceptTransferModes(TransferMode.COPY);
			e.consume();
		});
		node.setOnDragDropped(e -> {
			try {
				String raw = e.getDragboard().getString();
				if (raw == null) {
					e.setDropCompleted(false);
				} else {
					AppointmentModel appointment = CLIPBOARD_GSON.fromJson(raw, AppointmentModel.class);
					Set<Integer> selectedCalendarIds = viewModel.getSelectedCalendarIds();

					if (!selectedCalendarIds.isEmpty()) {
						int calendarId = selectedCalendarIds.iterator().next();
						viewModel.add(appointment.with().calendarId(calendarId).build());
					}

					e.setDropCompleted(true);
				}
			} catch (JsonParseException f) {
				// Ignore any dragboard contents that are not a valid
				// JSON appointment
				e.setDropCompleted(false);
			} catch (NullPointerException f) {
				f.printStackTrace();
				e.setDropCompleted(false);
			}
		});
	}
	
	@Override
	public Node getNode() { return node; }
}
