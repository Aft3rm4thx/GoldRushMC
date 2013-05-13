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
@Table(name = "town_tbl")
public class TownTbl {

	@Id @GeneratedValue private int id;
	@Column(name = "TOWN_NAME") @NotEmpty private String name;
	@Column(name = "TAX") private int tax;
	@Column(name = "LEVEL") @NotEmpty private int level;
	@Column(name = "GOLD_HELD") private float goldHeld;
	@Transient private int population;
	@OneToOne(mappedBy = "town") private BankTbl bank;
	@OneToMany(mappedBy = "town") private Set<PlayerTbl> citizens;
	
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
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public float getGoldHeld() {
		return goldHeld;
	}
	public void setGoldHeld(float goldHeld) {
		this.goldHeld = goldHeld;
	}
	public int getPopulation() {
		return population;
	}
	public BankTbl getBank() {
		return bank;
	}
	public void setBank(BankTbl bank) {
		this.bank = bank;
	}
	public Set<PlayerTbl> getCitizens() {
		return citizens;
	}
	
	@PostLoad
	public void setPopulation(int population) {
		this.population = citizens.size();
	}
	public void addCitizen(PlayerTbl player) {
		this.citizens.add(player);
		this.population = this.citizens.size();
	}
	public void removeCitizen(PlayerTbl player) {
		this.citizens.remove(player);
		this.population = this.citizens.size();
	}
}
