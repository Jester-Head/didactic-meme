package utilities;

import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtility extends Spinner<LocalTime> {

	DateTimeUtility() {

	}

	/**
	 * Disables selecting dates before the current date.
	 *
	 * @param startDate UI component .
	 * @param endDate   UI component.
	 */
	public static void setDate(DatePicker startDate, DatePicker endDate) {
		ChronoLocalDate today = ChronoLocalDate.from(LocalDateTime.now());
		startDate.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(today));
			}
		});
		endDate.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate start = startDate.getValue();
				setDisable(empty || date.isBefore(today) || date.isBefore(start));
			}
		});


	}

	/**
	 * Handles populating time selection spinners in 12-hour format.
	 *
	 * @param spinner
	 * @return String indicating if current time is AM or PM
	 */
	public static String populateHoursTwelve(Spinner<Integer> spinner) {
		LocalTime time = LocalTime.now();
		String ampm = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
		SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12);
		int intTime = Integer.parseInt(formatter.format(time));

		if (intTime == 0) {
			hours.setValue(intTime + 12);
			ampm = "am";
		}
		if (intTime < 12 && intTime >= 1) {
			hours.setValue(intTime);
			ampm = "am";
		}
		if (intTime == 12) {
			ampm = "pm";
		}
		if (intTime > 12) {
			hours.setValue(intTime - 12);
			ampm = "pm";
		}
		spinner.setValueFactory(hours);
		return ampm;
	}

	public static void populateMinutes(Spinner<Integer> spinner) {
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm");
		SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
		int intTime = Integer.parseInt(formatter.format(time));
		hours.setValue(intTime);
		spinner.setValueFactory(hours);

	}

/*
	public LocalTime twelveHourTimes(Spinner<Integer> hoursSpin,Spinner<Integer> minutesSpin, String type) {
		int value = hoursSpin.getValue();
		LocalTime convertLocalTime = null;
		Toggle selectedToggle = toggleGroup.getSelectedToggle();
		if (selectedToggle.equals(startAmRb) || selectedToggle.equals(endAmRb)) {
			if (value == 12){
				convertLocalTime = LocalTime.of(0,minutesSpin.getValue());
			}else {
				convertLocalTime = LocalTime.of(value,minutesSpin.getValue());
			}
		}else {
			convertLocalTime = LocalTime.of(value+12,minutesSpin.getValue());
		}
		return convertLocalTime;
	}
*/

}