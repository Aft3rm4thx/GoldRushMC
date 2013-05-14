package com.goldrushmc.bukkit.train;

import org.bukkit.Location;

public class findCorners {

	Location northWest;
	Location northEast;
	Location southWest;
	Location southEast;
	
	Location mostSouthernOne;
	Location mostSouthernTwo;
	
	Location mostNorthernOne;
	Location mostNorthernTwo;
	
	Location mostEasternOne;
	Location mostEasternTwo;
	
	Location mostWesternOne;
	Location mostWesternTwo;
	
	public Location getNorthWest() {
		return northWest;		
	}
	public Location getNorthEast() {
		return northEast;		
	}
	public Location getSouthWest() {
		return southWest;		
	}
	public Location getSouthEast() {
		return southEast;		
	}
	
	public findCorners(Location locOne, Location locTwo, Location locThree, Location locFour) {
		
		getBothSouthern(locOne, locTwo, locThree, locFour);
		getBothNorthern(locOne, locTwo, locThree, locFour);
		getBothWestern(locOne, locTwo, locThree, locFour);
		getBothEastern(locOne, locTwo, locThree, locFour);
		
		
		if(mostNorthernOne == mostWesternOne){
			northWest = mostNorthernOne;
		} else if(mostNorthernOne == mostWesternTwo){
			northWest = mostNorthernOne;
		} else if(mostNorthernTwo == mostWesternTwo){
			northWest = mostNorthernTwo;
		} else if(mostNorthernTwo== mostWesternTwo){
			northWest = mostNorthernTwo;
		}
		
		if(mostNorthernOne == mostEasternOne){
			northEast = mostNorthernOne;
		} else if(mostNorthernOne == mostEasternTwo){
			northEast = mostNorthernOne;
		} else if(mostNorthernTwo == mostEasternTwo){
			northEast = mostNorthernTwo;
		} else if(mostNorthernTwo== mostEasternTwo){
			northEast = mostNorthernTwo;
		}
		
		if(mostSouthernOne == mostWesternOne){
			southWest = mostSouthernOne;
		} else if(mostSouthernOne == mostWesternTwo){
			southWest = mostSouthernOne;
		} else if(mostSouthernTwo == mostWesternTwo){
			southWest = mostSouthernTwo;
		} else if(mostSouthernTwo== mostWesternTwo){
			southWest = mostSouthernTwo;
		}
		
		if(mostSouthernOne == mostEasternOne){
			southEast = mostSouthernOne;
		} else if(mostSouthernOne == mostEasternTwo){
			southEast = mostSouthernOne;
		} else if(mostSouthernTwo == mostEasternTwo){
			southEast = mostSouthernTwo;
		} else if(mostSouthernTwo== mostEasternTwo){
			southEast = mostSouthernTwo;
		}
	}
	
	void getBothSouthern(Location locOne, Location locTwo, Location locThree, Location locFour){
		int mostSouthern = Integer.MIN_VALUE;
		Location first = null;
		int[] zArray = new int[] {locOne.getBlockZ(), locTwo.getBlockZ(), locTwo.getBlockZ(), locFour.getBlockZ()};
		for(int i=0; i<4;i++){
			if(zArray[i] > mostSouthern) {
				switch(i) {
				case 0: mostSouthernOne = locOne;
					mostSouthern = zArray[i];
					first = locOne;
					break;
				case 1: mostSouthernOne = locTwo;
					mostSouthern = zArray[i];
					first = locTwo;
					break;
				case 2: mostSouthernOne = locThree;
					mostSouthern = zArray[i];
					first = locTwo;
					break;
				case 3: mostSouthernOne = locFour;
					mostSouthern = zArray[i];
					first = locTwo;
					break;
				}
			}
		}
		for(int i=0; i<4;i++){
			if(zArray[i] == mostSouthern) {
				switch(i) {
				case 0: mostSouthernTwo = locOne;
					if(first != locOne){
						break;
					}					
				case 1: mostSouthernTwo = locTwo;
					if(first != locTwo){
						break;
					}
				case 2: mostSouthernTwo = locThree;
					if(first != locTwo){
						break;
					}
				case 3: mostSouthernTwo = locFour;
					if(first != locTwo){
						break;
					}
				}
			}
		}
	}
	
	void getBothNorthern(Location locOne, Location locTwo, Location locThree, Location locFour){
		int mostNorthern = Integer.MAX_VALUE;
		Location first = null;
		int[] zArray = new int[] {locOne.getBlockZ(), locTwo.getBlockZ(), locTwo.getBlockZ(), locFour.getBlockZ()};
		for(int i=0; i<4;i++){
			if(zArray[i] < mostNorthern) {
				switch(i) {
				case 0: mostNorthernOne = locOne;
					mostNorthern = zArray[i];
					first = locOne;
					break;
				case 1: mostNorthernOne = locTwo;
					mostNorthern = zArray[i];
					first = locTwo;
					break;
				case 2: mostNorthernOne = locThree;
					mostNorthern = zArray[i];
					first = locTwo;
					break;
				case 3: mostNorthernOne = locFour;
					mostNorthern = zArray[i];
					first = locTwo;
					break;
				}
			}
		}
		for(int i=0; i<4;i++){
			if(zArray[i] == mostNorthern) {
				switch(i) {
				case 0: mostNorthernTwo = locOne;
					if(first != locOne){
						break;
					}					
				case 1: mostNorthernTwo = locTwo;
					if(first != locTwo){
						break;
					}
				case 2: mostNorthernTwo = locThree;
					if(first != locTwo){
						break;
					}
				case 3: mostNorthernTwo = locFour;
					if(first != locTwo){
						break;
					}
				}
			}
		}
	}
	
	void getBothEastern(Location locOne, Location locTwo, Location locThree, Location locFour){
		int mostEastern = Integer.MIN_VALUE;
		Location first = null;
		int[] XArray = new int[] {locOne.getBlockX(), locTwo.getBlockX(), locTwo.getBlockX(), locFour.getBlockX()};
		for(int i=0; i<4;i++){
			if(XArray[i] > mostEastern) {
				switch(i) {
				case 0: mostEasternOne = locOne;
					mostEastern = XArray[i];
					first = locOne;
					break;
				case 1: mostEasternOne = locTwo;
					mostEastern = XArray[i];
					first = locTwo;
					break;
				case 2: mostEasternOne = locThree;
					mostEastern = XArray[i];
					first = locTwo;
					break;
				case 3: mostEasternOne = locFour;
					mostEastern = XArray[i];
					first = locTwo;
					break;
				}
			}
		}
		for(int i=0; i<4;i++){
			if(XArray[i] == mostEastern) {
				switch(i) {
				case 0: mostEasternTwo = locOne;
					if(first != locOne){
						break;
					}					
				case 1: mostEasternTwo = locTwo;
					if(first != locTwo){
						break;
					}
				case 2: mostEasternTwo = locThree;
					if(first != locTwo){
						break;
					}
				case 3: mostEasternTwo = locFour;
					if(first != locTwo){
						break;
					}
				}
			}
		}
	}
	
	void getBothWestern(Location locOne, Location locTwo, Location locThree, Location locFour){
		int mostWestern = Integer.MAX_VALUE;
		Location first = null;
		int[] XArray = new int[] {locOne.getBlockX(), locTwo.getBlockX(), locTwo.getBlockX(), locFour.getBlockX()};
		for(int i=0; i<4;i++){
			if(XArray[i] < mostWestern) {
				switch(i) {
				case 0: mostWesternOne = locOne;
					mostWestern = XArray[i];
					first = locOne;
					break;
				case 1: mostWesternOne = locTwo;
					mostWestern = XArray[i];
					first = locTwo;
					break;
				case 2: mostWesternOne = locThree;
					mostWestern = XArray[i];
					first = locTwo;
					break;
				case 3: mostWesternOne = locFour;
					mostWestern = XArray[i];
					first = locTwo;
					break;
				}
			}
		}
		for(int i=0; i<4;i++){
			if(XArray[i] == mostWestern) {
				switch(i) {
				case 0: mostWesternTwo = locOne;
					if(first != locOne){
						break;
					}					
				case 1: mostWesternTwo = locTwo;
					if(first != locTwo){
						break;
					}
				case 2: mostWesternTwo = locThree;
					if(first != locTwo){
						break;
					}
				case 3: mostWesternTwo = locFour;
					if(first != locTwo){
						break;
					}
				}
			}
		}
	}
}
