package Receiver.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "device_measurements")
public class DeviceMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("measurement_value")
    @Column(name = "measurement_value", nullable = false)
    private Double measurementValue;

    @JsonProperty("device_id")
    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @JsonProperty("timestamp")
    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    @JsonCreator
    public DeviceMeasurement(
            @JsonProperty("measurement_value") Double measurementValue,
            @JsonProperty("device_id") Long deviceId,
            @JsonProperty("timestamp") Long timestamp) {
        this.measurementValue = measurementValue;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }
}
