package com.lochend.pharmacylocation.repository;

public interface IPharmacyRepository {

	public abstract void getAllNearestPharmacies(IPharmaciesListener _listener);

	public abstract void searchPharmacies(IPharmaciesListener _listener,
			String searchText);

	public abstract void getOpenOnlyNearestPharmacies(
			IPharmaciesListener _listener);

	public abstract void getPharmacyOpeningHours(int pharmacyId,
			IPharmacyOpeningHourListener _openingHourListener);
	
	void refreshDataFromCloud(final IPharmaciesListener _listener);

}