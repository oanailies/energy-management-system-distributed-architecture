package Sender.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class DeviceMeasurement {

    private Double value;
    private long deviceId;
    private long timestamp;

    @JsonCreator
    public DeviceMeasurement(
            @JsonProperty("measurement_value") Double value,
            @JsonProperty("device_id") long deviceId,
            @JsonProperty("timestamp") long timestamp) {
        this.value = value;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }
}
