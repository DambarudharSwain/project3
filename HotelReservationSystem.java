import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HotelReservationSystem {
	
	private static final String url = "jdbc:mysql://localhost:3306/Hotel_Database";
	private static final String username = "root";
	private static final String password = "dambaru123";
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
		}catch(ClassNotFoundException e) {
			System.out.println(e);
		}
		
		try {
			Connection con = DriverManager.getConnection(url, username, password);
			
			while(true) {
				System.out.println();
				System.out.println("HOTEL RESERVATION SYSTEM");
				Scanner sc = new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservation");
				System.out.println("5. Delete Reservation");
				System.out.println("0. Exit");
				
				System.out.println("Choose an option: ");
				int choice = sc.nextInt();
				
				switch(choice) {
				case 1:
					reserveRoom(con, sc);
					break;
				case 2:
					viewReservations(con);
					break;
				case 3:
					getRoomNumber(con, sc);
					break;
				case 4:
					updateReservation(con, sc);
					break;
				case 5:
					deleteReservation(con, sc);
					break;
				case 0:
					exit();
					sc.close();
					return;
				default:
					System.out.println("Invalid choice! Try again");
				}
			}
			
			
		}catch(SQLException e) {
			System.out.println(e);
		}catch(InterruptedException e) {
			System.out.println(e);
		}
	}	
		
		
		// Method of Insertion in the database
		private static void reserveRoom(Connection con, Scanner sc)		{
		
		try {
			System.out.println("Enter guest name: ");
			String guestName = sc.next();
			sc.nextLine();
			System.out.println("Enter room number: ");
			int roomNumber = sc.nextInt();
			System.out.println("Enter contact number: ");
			String contact = sc.next();
			
			String query = "insert into Reservations(Name, Room_no, Contact) values(?, ?, ?)";
			
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, guestName);
			ps.setInt(2, roomNumber);
			ps.setString(3, contact);
			
			int rowAffected = ps.executeUpdate();
			if(rowAffected >0) {
				System.out.println("Reservation successful");
			}
			else {
				System.out.println("Reservation failed");
			}
			
		}catch(SQLException e) {
			System.out.println(e);
		}

	}
	
	// View Reservations in the database
	private static void viewReservations(Connection con) {
		String query = "select * from Reservations";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			if(!rs.next()) {
				System.out.println("*The table is empty*");
			}
			
			while(rs.next()) {
			System.out.println("Guest ID: "+rs.getInt("ID"));
			System.out.println("Guest Name: "+rs.getString("Name"));
			System.out.println("Room number: "+rs.getInt("Room_no"));
			System.out.println("Contact number: "+rs.getString("Contact"));
			System.out.println("Rerservation date: "+rs.getTimestamp("Date").toString());
			System.out.println();
			}
		
		}catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	// Get room number of the user
	private static void getRoomNumber(Connection con, Scanner sc) {
		try {
			System.out.println("Enter reservation ID: ");
			int id = sc.nextInt();
			System.out.println("Enter guest name: ");
			String name = sc.next();
			
			String query = "select Room_no from Reservations where ID=? and Name=?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ps.setString(2, name);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("The room number for the guest of ID "+id+" and name "+name+" is: "+rs.getInt("ID"));
			}
			else {
				System.out.println("Reservation not found for the given ID and Name");
			}
			
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private static void updateReservation(Connection con, Scanner sc) {
		try {
			System.out.println("Enter ID of the guest to update: ");
			int id = sc.nextInt();
			sc.nextLine();
			
			if(!reservationExists(con, id)) {
				System.out.println("Reservation doesn't exists for the given ID");
				return;
			}
			
			System.out.println("Enter new Name: ");
			String name = sc.nextLine();
			System.out.println("Enter room number: ");
			int roomno = sc.nextInt();
			System.out.println("Enter contact number: ");
			String contact = sc.next();
			
			String query = "update Reservations set Name=?, Room_no=?, Contact=? where ID=?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, name);
			ps.setInt(2, roomno);
			ps.setString(3, contact);
			ps.setInt(4, id);
			
			int rowAffected = ps.executeUpdate();
			
			if(rowAffected>0) {
				System.out.println("Updation successfull");
			}
			else {
				System.out.println("Updation failed");
			}
		
			
		}catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	private static void deleteReservation(Connection con, Scanner sc) {
		try {
			System.out.println("Enter ID to be deleted: ");
			int id = sc.nextInt();
			
			if(!reservationExists(con, id)) {
				System.out.println("The guest for the given ID doesn't exist");
				return;
			}
			String query = "delete from Reservations where ID=?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			
			int rowAffected = ps.executeUpdate();
			if(rowAffected>0) {
				System.out.println("Deleted Successfully");
			}
			else {
				System.out.println("Deletion failed");
			}
	
			
		}catch(SQLException e) {
			System.out.println(e);
		}
	}
	
	private static boolean reservationExists(Connection con, int id) {
		try {
			String query = "select ID from Reservations where ID=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next();
			
		}catch(SQLException e) {
			System.out.println(e);
			return false;
		}
		
	}
	
	private static void exit() throws InterruptedException {
		System.out.print("Exiting");
		int i=5;
		while(i>=0) {
			System.out.print(".");
			Thread.sleep(500);
			i--;
		}
		System.out.println();
		System.out.println("Thank you for using Hotel Reservation System!");
	}
}