package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.model.Booking;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookingDAO implements BookingDAO {

    // Identity Map — mantiene le istanze già caricate
    protected final List<Booking> identityMap = new ArrayList<>();

    protected Booking findInCache(int id) {
        return identityMap.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    protected List<Booking> findInCacheByPatient(int patientId) {
        return identityMap.stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId)
                .toList();
    }

    protected void addToCache(Booking booking) {
        if (findInCache(booking.getId()) == null) {
            identityMap.add(booking);
        }
    }

    protected void updateInCache(int bookingId) {
        Booking cached = findInCache(bookingId);
        if (cached != null) {
            cached.cancel();
        }
    }
}
