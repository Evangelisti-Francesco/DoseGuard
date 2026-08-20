package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Booking;

import java.util.List;

public interface BookingDAO {

    void save(Booking booking) throws DAOException;
    List<Booking> findByPatient(int patientId) throws DAOException;
    List<Booking> findByDoctor(int doctorId) throws DAOException;
    List<Booking> findCompletedBookings(int patientId, int doctorId) throws DAOException;
    List<Booking> findUpcomingBookings(int patientId, int doctorId) throws DAOException;
    List<Booking> findPastByPatient(int patientId) throws DAOException;
    void cancel(int bookingId, int patientId) throws DAOException;

    List<Booking> findAll() throws  DAOException;

}
