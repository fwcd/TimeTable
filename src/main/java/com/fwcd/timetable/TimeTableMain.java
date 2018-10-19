package com.fwcd.timetable;

import com.fwcd.timetable.model.utils.ObservableUtils;
import com.fwcd.timetable.view.TimeTableAppView;
import com.fwcd.timetable.view.utils.FxParentView;
import com.fwcd.timetable.viewmodel.TimeTableAppContext;
import com.fwcd.timetable.viewmodel.TimeTableAppViewModel;
import com.fwcd.timetable.viewmodel.utils.FileSaveState;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TimeTableMain extends Application {
	private static final String NAME = "TimeTable";
	private static final String VERSION = "0.1";
	private static final String BASE_TITLE = NAME + " " + VERSION;
	
	private final TimeTableAppContext context = new TimeTableAppContext();
	private final TimeTableAppViewModel viewModel = new TimeTableAppViewModel();
	
	@Override
	public void start(Stage stage) {
		FxParentView appView = new TimeTableAppView(context, viewModel);
		Scene scene = new Scene(appView.getNode(), 800, 500);
		FileSaveState state = context.getFileSaveState();
		
		ObservableUtils.dualListenAndFire(state.getCurrentFileName(), state.hasUnsavedData(), (fileName, unsaved) -> {
			StringBuilder title = new StringBuilder(BASE_TITLE);
			
			fileName.ifPresent(it -> title.append(" - ").append(it));
			if (unsaved) {
				title.append('*');
			}
			
			stage.setTitle(title.toString());
		});
		
		stage.setScene(scene);
		scene.getStylesheets().add("/css/styles.css");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
