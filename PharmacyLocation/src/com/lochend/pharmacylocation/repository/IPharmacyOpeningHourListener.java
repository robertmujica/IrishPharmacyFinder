package com.lochend.pharmacylocation.repository;

import java.util.ArrayList;
import java.util.List;

import com.lochend.pharmacylocation.entity.OpeningHour;
import com.lochend.pharmacylocation.entity.PharmacyBase;

public interface IPharmacyOpeningHourListener {
	void getPharmacyOpeningHoursCallback(List<OpeningHour> pharmacyList);
}
