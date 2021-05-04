package com.meta1203.screenshirt.models;

import java.awt.Color;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatus {
	LocalDateTime now;
	Color shirtColor;
}
