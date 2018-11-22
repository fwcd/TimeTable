package com.fwcd.timetable.plugin.git;

import java.util.Collections;
import java.util.List;

import com.fwcd.fructose.Observable;
import com.fwcd.fructose.Option;
import com.fwcd.timetable.model.git.GitRepositoryModel;
import com.fwcd.timetable.plugin.TimeTableAppPlugin;
import com.fwcd.timetable.view.NamedFxView;
import com.fwcd.timetable.view.git.GitView;
import com.fwcd.timetable.viewmodel.TimeTableAppApi;

/**
 * A plugin that allows the user to manage the
 * Git repository of the currently open file.
 */
public class GitPlugin implements TimeTableAppPlugin {
	private static final String NAME = "Git";
	private static final String DESCRIPTION = "Git integration for TimeTable";
	private final Observable<Option<GitRepositoryModel>> model = new Observable<>(Option.empty());
	private final GitView view = new GitView(model);
	private final List<NamedFxView> sidebarViews = Collections.singletonList(NamedFxView.of("Git", view));
	
	@Override
	public void initialize(TimeTableAppApi api) {
		api.getCurrentPath().listenAndFire(path -> model.set(path.flatMap(GitRepositoryModel::ofFileInRepo)));
	}
	
	@Override
	public String getName() { return NAME; }

	@Override
	public String getDescription() { return DESCRIPTION; }
	
	@Override
	public List<? extends NamedFxView> getSidebarViews() { return sidebarViews; }
	
	@Override
	public void close() {
		view.close();
		model.get().ifPresent(GitRepositoryModel::close);
	}
}
