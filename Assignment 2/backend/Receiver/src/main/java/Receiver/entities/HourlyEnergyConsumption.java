package Receiver.entities;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(name = "total_consumption", nullable = false)
    private Double totalConsumption;
}
