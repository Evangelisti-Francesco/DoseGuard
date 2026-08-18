package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.TimeSlot;

import java.util.List;

public interface TimeSlotDAO {

    List<TimeSlot> getAvailableByDoctor(Doctor doctor) throws DAOException;
    List<TimeSlot> getAllByDoctor(int doctorId) throws DAOException;
    TimeSlot findById(int id) throws DAOException;
    void save(TimeSlot slot, int doctorId) throws DAOException;
    boolean reserveSlot(int slotId, int minutes) throws DAOException;
    void releaseSlot(int slotId) throws DAOException;
    void delete(int slotId, int doctorId) throws DAOException;


}
