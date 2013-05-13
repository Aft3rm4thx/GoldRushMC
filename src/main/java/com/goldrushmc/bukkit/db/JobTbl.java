package com.goldrushmc.bukkit.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "job_tbl")
public class JobTbl {

	public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
	
	@Id @GeneratedValue private int id;
	@Column(name = "JOB_TITLE") private String jobTitle;
	@Enumerated private EmployeeStatus status;
}
