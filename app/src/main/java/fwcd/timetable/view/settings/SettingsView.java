package fwcd.timetable.view.settings;

import java.util.function.BiFunction;
import java.util.function.Function;

import fwcd.fructose.Observable;
import fwcd.timetable.view.utils.FxUtils;
import fwcd.timetable.view.FxView;
import fwcd.timetable.viewmodel.TimeTableAppContext;
import fwcd.timetable.viewmodel.settings.TimeTableAppSettings;
import fwcd.timetable.viewmodel.settings.TimeTableAppSettings.Builder;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class SettingsView implements FxView {
	private final GridPane node;
	private final Observable<TimeTableAppSettings> model;
	
	public SettingsView(TimeTableAppContext context) {
		model = context.getSettings();
		
		int rowIndex = 1;
		node = new GridPane();
		
		node.addRow(rowIndex++, localizedPropertyLabel("language", context), boundTextField(TimeTableAppSettings::getLanguage, Builder::language));
		node.addRow(rowIndex++, localizedPropertyLabel("theme", context), boundTextField(TimeTableAppSettings::getTheme, Builder::theme));
		node.addRow(rowIndex++, localizedPropertyLabel("dateformat", context), boundTextField(TimeTableAppSettings::getDateFormat, Builder::dateFormat));
		node.addRow(rowIndex++, localizedPropertyLabel("timeformat", context), boundTextField(TimeTableAppSettings::getTimeFormat, Builder::timeFormat));
		node.addRow(rowIndex++, localizedPropertyLabel("datetimeformat", context), boundTextField(TimeTableAppSettings::getDateTimeFormat, Builder::dateTimeFormat));
		node.addRow(rowIndex++, localizedPropertyLabel("yearmonthformat", context), boundTextField(TimeTableAppSettings::getYearMonthFormat, Builder::yearMonthFormat));
		node.addRow(rowIndex++, localizedPropertyLabel("prettyprintjson", context), boundCheckBox(TimeTableAppSettings::shouldPrettyPrintJson, Builder::prettyPrintJson));
		node.addRow(rowIndex++, FxUtils.buttonOf(context.localized("reset"), context::resetSettings));
	}
	
	private TextField boundTextField(Function<TimeTableAppSettings, String> getter, BiFunction<Builder, String, Builder> setter) {
		TextField field = new TextField();
		FxUtils.bindBidirectionally(model, field.textProperty(), getter, newVal -> setter.apply(model.get().with(), newVal).build());
		return field;
	}
	
	private CheckBox boundCheckBox(Function<TimeTableAppSettings, Boolean> getter, BiFunction<Builder, Boolean, Builder> setter) {
		CheckBox box = new CheckBox();
		FxUtils.bindBidirectionally(model, box.selectedProperty(), getter, newVal -> setter.apply(model.get().with(), newVal).build());
		return box;
	}

	private Label localizedPropertyLabel(String unlocalized, TimeTableAppContext context) {
		return FxUtils.labelOf(context.localized(unlocalized), ": ");
	}
	
	@Override
	public Node getNode() { return node; }
}
