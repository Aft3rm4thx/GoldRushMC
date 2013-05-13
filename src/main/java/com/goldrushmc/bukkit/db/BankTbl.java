package com.goldrushmc.bukkit.db;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.validation.NotEmpty;

@Entity
@Table(name = "bank_tbl")
public class BankTbl {

	@Id @GeneratedValue private int id;
	@Column(name = "BANK_NAME") @NotEmpty private String name;
	@Column(name = "INTEREST") @NotEmpty private float interest;
	@Transient private float totalGold;
	@Transient private int sumOfAccounts;
	@OneToMany(mappedBy = "bank") private Set<PlayerTbl> customers;
	@OneToOne(mappedBy = "bank") private TownTbl town;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getInterest() {
		return interest;
	}
	public void setInterest(float interest) {
		this.interest = interest;
	}
	public float getTotalGold() {
		return totalGold;
	}
	
	@PostLoad
	public void setTotalGold() {
		totalGold = 0;
		if(customers != null) {
			for(PlayerTbl player : customers) {
				totalGold += player.getBankGold();
			}
		}
		if(town != null) totalGold += town.getGoldHeld();
	}
	
	@PostLoad
	public void setSumOfAccounts() {
		this.sumOfAccounts = customers.size();
	}

	public int getSumOfAccounts() {
		return sumOfAccounts;
	}

	public Set<PlayerTbl> getCustomers() {
		return customers;
	}
	public void addCustomer(PlayerTbl player) {
		this.customers.add(player);
	}
	public void removeCustomer(PlayerTbl player) {
		this.customers.remove(player);
	}
	public TownTbl getTown() {
		return town;
	}
	public void setTown(TownTbl town) {
		this.town = town;
	}

}
