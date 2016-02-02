package com.lochend.pharmacylocation.repository;

import java.util.ArrayList;

import com.lochend.pharmacylocation.entity.PharmacyBase;

public interface IPharmaciesListener {
	void getPharmaciesCallback(ArrayList<PharmacyBase> pharmacyList);
}
