package spring.orm.util;

import java.util.List;

import spring.orm.customexceptions.InvalidWeekdayException;

public class UtilManager {

	public static String converWeekdays(List<String> weekdays) throws InvalidWeekdayException {
		/*
		 * This method takes a list of weekdays as input and converts them into a string representation.
		 */
		if (weekdays.size() == 1) {
			// If there is only one weekday in the list, return that weekday.
			return weekdays.get(0);
		} else {
			StringBuilder sb = new StringBuilder();

			// Create a StringBuilder to build the string representation of the weekdays.

			for (String day : weekdays) {
				// Iterate through each weekday in the list.

				if (!isValidWeekday(day)) {
					throw new InvalidWeekdayException("Invalid weekday: " + day);
				}

				switch (day.toUpperCase()) {
				case "MON":
					sb.append("M");
					break;
				case "TUE":
					sb.append("T");
					break;
				case "WED":
					sb.append("W");
					break;
				case "THU":
					sb.append("H");
					break;
				case "FRI":
					sb.append("F");
					break;
				case "SAT":
					sb.append("S");
					break;
				case "SUN":
					sb.append("U");
					break;

				}
			}

			return sb.toString();
			// Return the final string representation of the weekdays.
		}

	}

	private static boolean isValidWeekday(String day) {
		// Implement your logic to determine if a weekday is valid or not.
		// You can use a predefined set of valid weekdays or any other condition.
		// Return true if the weekday is valid, false otherwise.
		return true;
	}
}