package com.meta1203.screenshirt.models;

import lombok.Data;

@Data
public class AccelerometerStatus {
	Double rawX;
	Double rawY;
	Double rawZ;
	
	Double X;
	Double Y;
	Double Z;
}
