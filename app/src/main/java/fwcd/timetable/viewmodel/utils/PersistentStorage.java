package fwcd.timetable.viewmodel.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonParseException;

import fwcd.fructose.Observable;
import fwcd.fructose.Option;
import fwcd.timetable.model.json.GsonConfigurator;

public class PersistentStorage {
	private final GsonConfigurator gsonFactory;
	private final Path folderPath;
	private final Map<String, TypedObservable<?>> observables = new HashMap<>();
	private boolean autoSaveEnabled = false;
	
	private static class TypedObservable<T> {
		Observable<Object> observable;
		Class<?> type;
		
		@SuppressWarnings("unchecked")
		TypedObservable(Observable<T> observable, Class<T> type) {
			this.observable = (Observable<Object>) observable;
			this.type = type;
		}
	}
	
	public PersistentStorage(Path folderPath, GsonConfigurator gsonFactory) {
		this.folderPath = folderPath;
		this.gsonFactory = gsonFactory;
	}
	
	public void loadFromDisk() {
		for (String key : observables.keySet()) {
			TypedObservable<?> value = observables.get(key);
			read(resolve(key), value.type).ifPresent(value.observable::set);
		}
	}
	
	public void addAutoSaveHooks() {
		if (!autoSaveEnabled) {
			autoSaveEnabled = true;
			for (String key : observables.keySet()) {
				observables.get(key).observable.listenAndFire(it -> save(it, resolve(key)));
			}
		}
	}
	
	public <T> void add(String key, Observable<T> observable, Class<T> type) {
		observables.put(key, new TypedObservable<>(observable, type));
	}
	
	private void save(Object value, Path path) {
		try {
			Files.createDirectories(folderPath);
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				gsonFactory.create().toJson(value, writer);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private <T> Option<T> read(Path path, Class<T> objClass) {
		if (Files.exists(path)) {
			try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				return Option.of(gsonFactory.create().fromJson(reader, objClass));
			} catch (JsonParseException | IOException e) {
				e.printStackTrace();
			}
		}
		return Option.empty();
	}
	
	private Path resolve(String key) {
		String fileName;
		if (key.endsWith(".json")) {
			fileName = key;
		} else {
			fileName = key + ".json";
		}
		return folderPath.resolve(fileName);
	}
}
