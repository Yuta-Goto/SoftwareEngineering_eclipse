package app.cancelReservation;

import java.util.Date;

import app.AppException;
import app.ManagerFactory;
import domain.payment.PaymentException;
import domain.payment.PaymentManager;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * Control class for Cancel Reservation
 * 
 */
public class CancelReservationControl {

    public void cancelReservation(String reservationNumber) throws AppException {
        try {
        	//Consume reservation
			ReservationManager reservationManager = getReservationManager();
			Date stayingDate = reservationManager.consumeReservation(reservationNumber);

			//Assign room
			RoomManager roomManager = getRoomManager();
			String roomNumber = roomManager.assignCustomer(stayingDate);

			//Create payment
			PaymentManager paymentManager = getPaymentManager();
			paymentManager.createPayment(stayingDate, roomNumber);
			
			stayingDate = roomManager.removeCustomer(roomNumber);
			paymentManager.consumePayment(stayingDate, roomNumber);
        }
        catch (RoomException e) {
            AppException exception = new AppException("Failed to cancel reservation", e);
            exception.getDetailMessages().add(e.getMessage());
            exception.getDetailMessages().addAll(e.getDetailMessages());
            throw exception;
        }
        catch (ReservationException e) {
            AppException exception = new AppException("Failed to cancel reservation", e);
            exception.getDetailMessages().add(e.getMessage());
            exception.getDetailMessages().addAll(e.getDetailMessages());
            throw exception;
        }
        catch (PaymentException e) {
			AppException exception = new AppException("Failed to check-in", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
    }

    private ReservationManager getReservationManager() {
		return ManagerFactory.getInstance().getReservationManager();
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private PaymentManager getPaymentManager() {
		return ManagerFactory.getInstance().getPaymentManager();
	}
}
