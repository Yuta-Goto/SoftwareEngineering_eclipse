/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.checkout;

import java.util.Date;

import app.AppException;
import app.ManagerFactory;
import domain.payment.PaymentException;
import domain.payment.PaymentManager;
import domain.room.RoomException;
import domain.room.RoomManager;

/**
 * Control class for Check-out Customer
 * 
 */
public class CheckOutRoomControl {
	
	public void checkOut(String roomNumber) throws AppException {
		try {
			//Clear room
			RoomManager roomManager = getRoomManager();
			Date stayingDate = roomManager.removeCustomer(roomNumber);
			/*
			 * Your code for clearing room by using domain.room.RoomManager
			 */
			//Consume payment
			PaymentManager paymentManager = getPaymentManager();
			paymentManager.consumePayment(stayingDate, roomNumber);
			
			/*
			 * Your code for consuming payment by using domain.payment.PaymentManager
			 */
		}
		catch (RoomException e) {
			AppException exception = new AppException("Failed to check-out", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
		catch (PaymentException e) {
			AppException exception = new AppException("Failed to check-out", e);
			exception.getDetailMessages().add(e.getMessage());
			exception.getDetailMessages().addAll(e.getDetailMessages());
			throw exception;
		}
	}

	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}

	private PaymentManager getPaymentManager() {
		return ManagerFactory.getInstance().getPaymentManager();
	}
}
