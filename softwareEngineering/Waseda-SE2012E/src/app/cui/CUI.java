/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import app.AppException;
import app.ManagerFactory;
import app.cancelReservation.CancelReservationForm;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomForm;
import domain.DaoFactory;
import domain.room.AvailableQty;
import domain.room.AvailableQtyDao;
import domain.room.RoomException;
//追加
import domain.room.RoomManager;
import util.DateUtil;

/**
 * CUI class for Hotel Reservation Systems
 * 
 */
public class CUI {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private BufferedReader reader;

	CUI() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	private void execute() throws IOException, NumberFormatException, RoomException {
		try {
			while (true) {
				int selectMenu;
				System.out.println("");
				System.out.println("Menu");
				System.out.println("1: Reservation");
				System.out.println("2: Check-in");
				System.out.println("3: Check-out");
				System.out.println("4: Cancel Reservation");
				System.out.println("9: End");
				System.out.print("> ");

				try {
					String menu = reader.readLine();
					selectMenu = Integer.parseInt(menu);
				}
				catch (NumberFormatException e) {
					selectMenu = -1;
				}

				if (selectMenu == 9) {
					break;
				}

				switch (selectMenu) {
					case 1:
						reserveRoom();
						break;
					case 2:
						checkInRoom();
						break;
					case 3:
						checkOutRoom();
						break;
					case 4:   //追加
						cancelReservation();
						break;
					default:
						System.out.println("Invalid menu option.");
						break;
				}
			}
			System.out.println("Ended");
		}
		catch (AppException e) {
			System.err.println("Error");
			System.err.println(e.getFormattedDetailMessages(LINE_SEPARATOR));
		}
		finally {
			reader.close();
		}
	}
	
	// 予約キャンセル用メソッドを追加
		private void cancelReservation() throws IOException, AppException {
			System.out.println("Input reservation number to cancel");
			System.out.print("> ");

			String reservationNumber = reader.readLine();

			if (reservationNumber == null || reservationNumber.length() == 0) {
				System.out.println("Invalid reservation number");
				return;
			}

			CancelReservationForm cancelReservationForm = new CancelReservationForm();
			cancelReservationForm.setReservationNumber(reservationNumber);
			cancelReservationForm.cancel();
			
			System.out.println("But there is no fee to pay.");
			System.out.println("Reservation has been cancelled.");
		}


	private void reserveRoom() throws IOException, AppException,NumberFormatException, RoomException {
		System.out.println("Input arrival date in the form of yyyy/mm/dd");
		System.out.print("> ");

		String dateStr = reader.readLine();
		
		// Validate input
		Date stayingDate = DateUtil.convertToDate(dateStr);
		if (stayingDate == null) {
			System.out.println("Invalid input");
			return;
		}
		
		//ここに人数選択追加
		System.out.println("How many people? (ex. 1 or 2 or 5)");
		System.out.print("> ");

		String reserverNumStr = reader.readLine();
		// Validate input
		int reserverNum;
		reserverNum = Integer.parseInt(reserverNumStr);
		
		
		RoomManager roomManager = getRoomManager();
		//追加
		AvailableQtyDao availableQtyDao = getAvailableQtyDao();
		AvailableQty availableQty = availableQtyDao.getAvailableQty(stayingDate);
		if (availableQty == null) {
			availableQty = new AvailableQty();
			availableQty.setQty(AvailableQty.AVAILABLE_ALL);
			availableQty.setDate(stayingDate);
		}
		int maxAvailableQty = roomManager.getMaxAvailableQty();
		if (availableQty.getQty() == AvailableQty.AVAILABLE_ALL) {
			// If all rooms are available,  
			// set then maximum number obtained to number of available rooms 
			availableQty.setQty(maxAvailableQty);

			// Newly register availableQty data on stayingDate to DB
			availableQtyDao.createAbailableQty(availableQty);
		}

		int availableRooms = availableQty.getQty();
		//roomManager.getMaxAvailableQty()
		System.out.println(availableRooms);
		//domain関連の追加ここまで。要変更
		if(availableRooms >= reserverNum) {
			for(int i=0;i<reserverNum;i++) {
				ReserveRoomForm reserveRoomForm = new ReserveRoomForm();
				reserveRoomForm.setStayingDate(stayingDate);
				String reservationNumber = reserveRoomForm.submitReservation();

				System.out.println("Reservation has been completed.");
				System.out.println("Arrival (staying) date is " + DateUtil.convertToString(stayingDate) + ".");
				System.out.println("Reservation number is " + reservationNumber + ".");
			}
			
		}else {
			System.out.println("Sorry. The number of reservers cannot stay on the day");
		}
		
	}

	private void checkInRoom() throws IOException, AppException {
		System.out.println("Input reservation number");
		System.out.print("> ");

		String reservationNumber = reader.readLine();

		if (reservationNumber == null || reservationNumber.length() == 0) {
			System.out.println("Invalid reservation number");
			return;
		}

		CheckInRoomForm checkInRoomForm = new CheckInRoomForm();
		checkInRoomForm.setReservationNumber(reservationNumber);

		String roomNumber = checkInRoomForm.checkIn();
		System.out.println("Check-in has been completed.");
		System.out.println("Room number is " + roomNumber + ".");

	}

	private void checkOutRoom() throws IOException, AppException {
		System.out.println("Input room number");
		System.out.print("> ");

		String roomNumber = reader.readLine();

		if (roomNumber == null || roomNumber.length() == 0) {
			System.out.println("Invalid room number");
			return;
		}

		CheckOutRoomForm checkoutRoomForm = new CheckOutRoomForm();
		checkoutRoomForm.setRoomNumber(roomNumber);
		checkoutRoomForm.checkOut();
		System.out.println("Check-out has been completed.");
	}
	
	private RoomManager getRoomManager() {
		return ManagerFactory.getInstance().getRoomManager();
	}
	
	private AvailableQtyDao getAvailableQtyDao() {
		return DaoFactory.getInstance().getAvailableQtyDao();
	}

	public static void main(String[] args) throws Exception {
		CUI cui = new CUI();
		cui.execute();
	}
}
