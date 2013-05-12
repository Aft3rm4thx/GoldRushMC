package com.goldrushmc.bukkit.train.db;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@Table(name = "train_station_tbl")
public class TrainStationTbl {

	@Id @GeneratedValue private int id;
	@Column(name = "STATION_NAME") @NotEmpty private String stationName;
	@OneToMany(mappedBy = "station") Set<TrainStationLocation> corners;
	@OneToMany(mappedBy = "station") Set<Trains> trains;
	@OneToMany(mappedBy = "station") Set<TrainSchedule> scheduledStop;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public Set<TrainStationLocation> getCorners() {
		return corners;
	}
	public void setCorners(Set<TrainStationLocation> corners) {
		this.corners = corners;
	}
	public Set<Trains> getTrains() {
		return trains;
	}
	public void setTrains(Set<Trains> trains) {
		this.trains = trains;
	}
	public Set<TrainSchedule> getScheduledStop() {
		return scheduledStop;
	}
	public void setScheduledStop(Set<TrainSchedule> scheduledStop) {
		this.scheduledStop = scheduledStop;
	}
}
