package fwcd.timetable.view.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import fwcd.fructose.ListenableValue;
import fwcd.fructose.Observable;
import fwcd.fructose.ReadOnlyObservable;
import fwcd.fructose.draw.DrawColor;
import fwcd.fructose.io.DelegatePrintStream;
import fwcd.fructose.structs.ReadOnlyObservableList;
import fwcd.timetable.view.FxView;

import org.controlsfx.control.PopOver;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public final class FxUtils {
	public static final Modifier CTRL_OR_CMD_DOWN = isMacOS() ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN;

	private FxUtils() {}
	
	public static boolean isMacOS() {
		return System.getProperty("os.name").contains("Mac");
	}
	
	public static void setVerticalScrollSpeed(ScrollPane pane, int multiplier) {
		// Source: https://stackoverflow.com/questions/32739269/how-do-i-change-the-amount-by-which-scrollpane-scrolls
		Node content = pane.getContent();
		content.setOnScroll(event -> {
			double dy = event.getDeltaY() * multiplier;
			double width = content.getBoundsInLocal().getWidth();
			double vvalue = pane.getVvalue();
			pane.setVvalue(vvalue - (dy / width));
		});
	}
	
	public static Button buttonOf(String label, Runnable action) {
		Button button = new Button(label);
		button.setOnAction(e -> action.run());
		return button;
	}
	
	/**
	 * Creates a button with the provided action and an unidirectional
	 * data binding supplying the label.
	 */
	public static Button buttonOf(ReadOnlyObservable<String> label, Runnable action) {
		Button button = new Button();
		label.listenAndFire(button::setText);
		button.setOnAction(e -> action.run());
		return button;
	}
	
	public static Menu menuOf(ReadOnlyObservable<String> name, MenuItem... items) {
		return menuOf(name, Arrays.stream(items));
	}
	
	public static Menu menuOf(ReadOnlyObservable<String> name, Stream<? extends MenuItem> items) {
		Menu menu = new Menu();
		name.listenAndFire(menu::setText);
		items.forEach(menu.getItems()::add);
		return menu;
	}

	public static MenuItem menuItemOf(ReadOnlyObservable<String> text, Runnable action) {
		MenuItem item = new MenuItem();
		item.setOnAction(e -> action.run());
		text.listenAndFire(item::setText);
		return item;
	}

	public static MenuItem menuItemOf(ReadOnlyObservable<String> text, Runnable action, KeyCombination accelerator) {
		MenuItem item = menuItemOf(text, action);
		item.setAccelerator(accelerator);
		return item;
	}
	
	public static MenuItem menuItemOf(String name, Runnable action) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(e -> action.run());
		return item;
	}
	
	public static MenuItem menuItemOf(String name, Runnable action, KeyCombination accelerator) {
		MenuItem item = menuItemOf(name, action);
		item.setAccelerator(accelerator);
		return item;
	}
	
	public static Label labelOf(ReadOnlyObservable<String> text) {
		return labelOf(text, "");
	}
	
	/**
	 * Creates a label with an unidirectional data binding
	 * to the provided {@link ReadOnlyObservable}.
	 */
	public static Label labelOf(ReadOnlyObservable<String> text, String appended) {
		Label label = new Label();
		text.listenAndFire(it -> label.setText(it + appended));
		return label;
	}
	
	private static class Flag {
		boolean value = false;
	}
	
	/**
	 * Creates a text field with a bidirectional data binding
	 * to the provided {@link Observable}.
	 */
	public static TextField textFieldOf(Observable<String> text) {
		TextField textField = new TextField();
		bindBidirectionally(text, textField.textProperty());
		return textField;
	}
	
	public static <T> void bindBidirectionally(ListenableValue<T> fructoseObservable, Property<T> fxProperty) {
		Flag updating = new Flag();
		fructoseObservable.listenAndFire(it -> {
			if (!updating.value) {
				updating.value = true;
				fxProperty.setValue(it);
				updating.value = false;
			}
		});
		fxProperty.addListener((obs, old, newValue) -> {
			if (!updating.value) {
				updating.value = true;
				fructoseObservable.set(newValue);
				updating.value = false;
			}
		});
	}
	
	public static <T, R> void bindBidirectionally(ListenableValue<T> fructoseObservable, Property<R> fxProperty, Function<T, R> mapper, Function<R, T> inverseMapper) {
		Flag updating = new Flag();
		fructoseObservable.listenAndFire(it -> {
			if (!updating.value) {
				updating.value = true;
				fxProperty.setValue(mapper.apply(it));
				updating.value = false;
			}
		});
		fxProperty.addListener((obs, old, newValue) -> {
			if (!updating.value) {
				updating.value = true;
				fructoseObservable.set(inverseMapper.apply(newValue));
				updating.value = false;
			}
		});
	}
	
	public static <T> ComboBox<T> comboBoxOf(List<T> values) {
		ComboBox<T> box = new ComboBox<>(FXCollections.observableList(values));
		return box;
	}
	
	public static <T> ComboBox<T> comboBoxOfObservable(ReadOnlyObservable<List<T>> values) {
		ComboBox<T> box = new ComboBox<>();
		values.listenAndFire(box.getItems()::setAll);
		return box;
	}
	
	public static <T> ComboBox<T> comboBoxOfObservable(ReadOnlyObservableList<T> values) {
		ComboBox<T> box = new ComboBox<>();
		values.listenAndFire(box.getItems()::setAll);
		return box;
	}
	
	public static Tab tabOf(ReadOnlyObservable<String> name, FxView content) {
		return tabOf(name, content.getNode());
	}
	
	public static Tab tabOf(String name, FxView content) {
		return tabOf(name, content.getNode());
	}
	
	public static Tab tabOf(ReadOnlyObservable<String> name, Node content) {
		Tab tab = new Tab();
		name.listenAndFire(tab::setText);
		tab.setClosable(false);
		tab.setContent(content);
		return tab;
	}
	
	public static Tab tabOf(String name, Node content) {
		Tab tab = new Tab(name);
		tab.setClosable(false);
		tab.setContent(content);
		return tab;
	}

	public static Color toFxColor(DrawColor drawColor) {
		return new Color(
			(double) drawColor.getR() / 255D,
			(double) drawColor.getG() / 255D,
			(double) drawColor.getB() / 255D,
			(double) drawColor.getAlpha() / 255D
		);
	}

	public static DrawColor toDrawColor(Color fxColor) {
		return new DrawColor(
			(int) (fxColor.getRed() * 255D),
			(int) (fxColor.getGreen() * 255D),
			(int) (fxColor.getBlue() * 255D),
			(int) (fxColor.getOpacity() * 255D)
		);
	}
	
	public static void showExceptionAlert(String title, Throwable e) {
		Alert alert = new Alert(AlertType.ERROR);
		StringBuilder msg = new StringBuilder(e.getMessage() + System.lineSeparator());
		
		e.printStackTrace(new DelegatePrintStream(msg::append));
		msg.delete(600, msg.length());
		
		alert.setTitle(title);
		alert.setHeaderText(e.getClass().getName());
		alert.setContentText(msg.toString());
		
		alert.showAndWait();
	}

	public static void showIndependentPopOver(PopOver popOver, Node node) {
		Bounds bounds = node.localToScreen(node.getBoundsInLocal());
		double x = bounds.getMaxX();
		double y = bounds.getMaxY() - bounds.getHeight();
		popOver.show(node.getScene().getWindow(), x, y);
	}

	public static void enableHideOnEscape(PopOver popOver) {
		popOver.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				popOver.hide();
			}
		});
	}
	
	public static PopOver newPopOver(FxView content) {
		return newPopOver(content.getNode());
	}
	
	public static PopOver newPopOver(Node content) {
		PopOver popOver = new PopOver(content);
		enableHideOnEscape(popOver);
		return popOver;
	}
	
	public static void setDateFormat(DatePicker picker, String format) {
		setDateFormat(picker, DateTimeFormatter.ofPattern(format));
	}
	
	public static void setDateFormat(DatePicker picker, DateTimeFormatter format) {
		picker.setConverter(new StringConverter<LocalDate>() {
			private final DateTimeFormatter formatter = format;
			
			@Override
			public String toString(LocalDate object) {
				if (object == null) {
					return null;
				} else {
					return formatter.format(object);
				}
			}
		
			@Override
			public LocalDate fromString(String str) {
				if (str == null) {
					return null;
				} else {
					return LocalDate.from(formatter.parse(str));
				}
			}
		});
	}
	
	public static void showColorPicker(Node node, DrawColor initialColor, Consumer<DrawColor> colorListener) {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(toFxColor(initialColor));
		colorPicker.valueProperty().addListener((obs, oldV, newV) -> colorListener.accept(toDrawColor(newV)));
		FxUtils.showIndependentPopOver(FxUtils.newPopOver(colorPicker), node);
	}
	
	public static void expandSingleNodes(TreeView<?> node) {
		TreeItem<?> item = node.getRoot();
		while (item.getChildren().size() == 1) {
			item.setExpanded(true);
			item = item.getChildren().get(0);
		}
		item.setExpanded(true);
	}
	
	/**
	 * Runs a task on the JavaFX application thread
	 * and blocks until it has been executed.
	 */
	public static void runAndWait(Runnable task) {
		Objects.requireNonNull(task, "Can not perform a null task!");
		
		if (Platform.isFxApplicationThread()) {
			task.run();
		} else {
			// Source: https://news.kynosarges.org/2014/05/01/simulating-platform-runandwait/
			CountDownLatch latch = new CountDownLatch(1);
			
			Platform.runLater(() -> {
				try {
					task.run();
				} finally {
					latch.countDown();
				}
			});
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				// Do nothing if interrupted
			}
		}
	}
}
