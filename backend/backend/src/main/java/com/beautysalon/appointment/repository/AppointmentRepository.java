package com.beautysalon.appointment.repository;

import com.beautysalon.appointment.entity.Appointment;
import com.beautysalon.util.AbstractFileRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * OOP Concepts Applied:
 * - Abstraction: Provides an interface or abstract mechanism for data access without exposing persistence details.
 * - Inheritance: Inherits common CRUD operations from abstract repository classes.
 * - Encapsulation: Hides file/database reading and writing mechanics from the rest of the application.
 */
public class AppointmentRepository extends AbstractFileRepository<Appointment, String> {

    @Override
    protected String getFilePath() {
        return "appointments.txt";
    }

    @Override
    protected String serialize(Appointment appointment) {
        return String.join("|",
                appointment.getId(),
                appointment.getCustomerId(),
                appointment.getStaffId(),
                appointment.getServiceId(),
                appointment.getDate(),
                appointment.getTime(),
                String.valueOf(appointment.getDurationMinutes()),
                appointment.getStatus()
        );
    }

    @Override
    protected Appointment deserialize(String record) {
        String[] parts = record.split("\\|");
        return new Appointment(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5],
                Integer.parseInt(parts[6]),
                parts[7]
        );
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Appointment appointment) {
        return appointment.getId() + "|";
    }
}
