package com.goldrushmc.bukkit.train.db;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@Table(name = "train_tbl")
public class Trains {
	
	@Id @GeneratedValue private int id;
	@Column(name = "TRAIN_NAME") @NotEmpty private String trainName;
	@Column(name = "WORLD") @NotEmpty private String worldName;
	@OneToMany(mappedBy = "train") private Set<TrainSchedule> schedule;
	@OneToOne private TrainStatus status;
	@ManyToOne private TrainStationTbl station;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public Set<TrainSchedule> getSchedule() {
		return schedule;
	}
	public void setSchedule(Set<TrainSchedule> schedule) {
		this.schedule = schedule;
	}
	public TrainStatus getStatus() {
		return status;
	}
	public void setStatus(TrainStatus status) {
		this.status = status;
	}
	public String getWorldName() {
		return worldName;
	}
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	public TrainStationTbl getStation() {
		return station;
	}
	public void setStation(TrainStationTbl station) {
		this.station = station;
	}
}
