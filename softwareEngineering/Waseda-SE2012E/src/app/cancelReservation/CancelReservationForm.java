package app.cancelReservation;

import app.AppException;

/**
 * Form class for Cancel Reservation
 * 
 */
public class CancelReservationForm {

	private CancelReservationControl cancelReservationHandler = new CancelReservationControl();

	private String reservationNumber;

	private CancelReservationControl getCancelReservationHandler() {
		return cancelReservationHandler;
	}

	public void cancel() throws AppException {
		CancelReservationControl cancelReservationHandler = getCancelReservationHandler();
		cancelReservationHandler.cancelReservation(reservationNumber);
	}

	public String getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(String reservationNumber) {
		this.reservationNumber = reservationNumber;
	}
}
