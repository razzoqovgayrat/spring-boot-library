package com.library.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DeviceInfo {
    private String deviceId;
    private String deviceName;
}
