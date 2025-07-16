package Receiver.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@Entity
@Table(name = "hourly_energy_consumption")
public class HourlyEnergyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "total_consumption", nullable = false)
    private Double totalConsumption;


    @PrePersist
    @PreUpdate
    private void populateDateFromTimestamp() {
        if (timestamp != null) {
            this.date = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }
}
