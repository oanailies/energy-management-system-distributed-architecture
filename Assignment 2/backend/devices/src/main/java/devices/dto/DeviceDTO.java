package devices.dto;

import lombok.Data;

@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double maxHourlyConsumption;

}
