package com.goldrushmc.bukkit.train.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "train_schedule_tbl")
public class TrainSchedule {

	@Id @GeneratedValue private int id;
	@Column(name = "DEPART_TIME") @NotNull  private long timeToDepart;
	@Column(name = "IS_NEXT") private boolean isNext;
	@OneToOne private Trains train;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTimeToDepart() {
		return timeToDepart;
	}
	public void setTimeToDepart(long timeToDepart) {
		this.timeToDepart = timeToDepart;
	}
	public Trains getTrain() {
		return train;
	}
	public void setTrain(Trains train) {
		this.train = train;
	}
	public boolean isNext() {
		return isNext;
	}
	public void setNext(boolean isNext) {
		this.isNext = isNext;
	}
}
