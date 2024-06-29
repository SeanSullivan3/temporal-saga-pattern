package ride.dao;


import ride.models.Booking;
import ride.models.CabAssignment;
import ride.models.Payment;
import ride.models.PaymentMethod;
import ride.models.Driver;
import ride.models.Rider;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;

public class ActivitiesDAO extends BaseDAO {

    public ActivitiesDAO(String url) {
        super(url);
    }

    public void insertBooking(Booking booking) throws SQLException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        String sql = "INSERT INTO bookings(bookingId,riderId,pickUpLocation,dropOffLocation,createdAt,status) VALUES(?,?,?,?,?,?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, booking.getBookingId());
            pstmt.setInt(2, booking.getRiderId());
            pstmt.setString(3, booking.getPickUpLocation());
            pstmt.setString(4, booking.getDropOffLocation());
            pstmt.setString(5, nowAsISO);
            pstmt.setString(6, booking.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET driverId=?,pickUpLocation=?,dropOffLocation=?,status=? WHERE bookingId=?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getDriverId());
            pstmt.setString(2, booking.getPickUpLocation());
            pstmt.setString(3, booking.getDropOffLocation());
            pstmt.setString(4, booking.getStatus().name());
            pstmt.setString(5, booking.getBookingId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void readBooking(String bookingId, Booking booking) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE bookingId = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                booking.setBookingId(rs.getString("bookingId"));
                booking.setRiderId(rs.getInt("riderId"));
                booking.setDriverId(rs.getInt("driverId"));
                booking.setPickUpLocation(rs.getString("pickUpLocation"));
                booking.setDropOffLocation(rs.getString("dropOffLocation"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void insertAssignment(CabAssignment cabAssignment) throws SQLException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        String sql = "INSERT INTO assignments(booking_id,driver_id,created_at,active) VALUES(?,?,?,?)";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cabAssignment.getBookingId());
            pstmt.setInt(2, cabAssignment.getDriverId());
            pstmt.setString(3, nowAsISO);
            pstmt.setBoolean(4, cabAssignment.getActive());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void deactivateAssignment(String bookingId) throws SQLException {
        String sql = "UPDATE assignments SET active=? WHERE booking_id=?;";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, false);
            pstmt.setString(2, bookingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void readPaymentMethod(int riderId, PaymentMethod paymentMethod) throws SQLException {
        String sql = "SELECT id, details FROM payment_methods WHERE rider_id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                paymentMethod.setId(rs.getInt("id"));
                paymentMethod.setDetails(rs.getString("details"));
                paymentMethod.setRiderId(riderId);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void insertPayment(Payment payment) throws SQLException {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        String sql = "INSERT INTO payments(payment_id, booking_id, amount, payment_method_id, createdAt, status) VALUES(?,?,?,?,?,?);";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payment.getPaymentId());
            pstmt.setString(2, payment.getBookingId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setInt(4, payment.getPaymentMethodId());
            pstmt.setString(5, nowAsISO);
            pstmt.setString(6, payment.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET amount=?, payment_method_id=?, status=? WHERE payment_id=?;";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, payment.getAmount());
            pstmt.setInt(2, payment.getPaymentMethodId());
            pstmt.setString(3, payment.getStatus().name());
            pstmt.setString(4, payment.getPaymentId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void readDriver(int driverId, Driver driver) throws SQLException {
        String sql = "SELECT name, contact FROM drivers WHERE id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                driver.setId(driverId);
                driver.setName(rs.getString("name"));
                driver.setContact(rs.getString("contact"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void readRider(int riderId, Rider rider) throws SQLException {
        String sql = "SELECT name, contact FROM riders WHERE id = ?";

        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rider.setId(riderId);
                rider.setName(rs.getString("name"));
                rider.setContact(rs.getString("contact"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
