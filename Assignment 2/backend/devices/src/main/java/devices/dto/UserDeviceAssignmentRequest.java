package devices.dto;

import lombok.Data;

@Data
public class UserDeviceAssignmentRequest {
    private Long userId;
    private Long deviceId;
}
