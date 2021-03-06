package fwcd.timetable.model.calendar.recurrence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import fwcd.fructose.Option;
import fwcd.timetable.model.language.Language;

public class ExcludingRecurrence implements Recurrence {
	private static final long serialVersionUID = -2092341529610795565L;
	private final Recurrence base;
	private final Set<? extends LocalDate> excluded;
	
	public ExcludingRecurrence(Recurrence base, Set<? extends LocalDate> excluded) {
		this.base = base;
		this.excluded = excluded;
	}
	
	@Override
	public boolean matches(LocalDate date) {
		return excluded.stream().noneMatch(it -> it.equals(date)) && base.matches(date);
	}
	
	@Override
	public String describeWith(Language language, DateTimeFormatter dateFormatter) {
		return base.describeWith(language, dateFormatter)
			+ " " + language.localize("exceptfor")
			+ excluded.stream().map(dateFormatter::format).reduce((a, b) -> a + ", " + b).map(it -> " " + it).orElse("");
	}
	
	@Override
	public Set<? extends LocalDate> getExcludes() {
		return excluded;
	}
	
	@Override
	public Option<LocalDate> getEnd() {
		return base.getEnd();
	}
}
