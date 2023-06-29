package spring.orm.customexceptions;

public class InvalidWeekdayException extends Exception {

	public InvalidWeekdayException(String message) {
		super(message);
	}

	public String getWeekday() {
		return "Invalid Weekday";
	}

}
