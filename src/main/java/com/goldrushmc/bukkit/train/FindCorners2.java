package com.goldrushmc.bukkit.train;

import java.util.List;

import org.bukkit.Location;

public class FindCorners2 {
//	findNE(points: set of points){
//		  NE = new point;
//		  max = -infinity;
//		  for p in points{
//		    if p.x+p.y > max {
//		      NE = p;
//		      max = p.x+p.y;
//		    }
//		  }
//		}
	
	Location northEast, southEast, northWest, southWest;
	
	public FindCorners2(List<Location> locs) {
		
	}
	
	public void SECorner(List<Location> locs) {		
		double highest = 0;
		for(int i = 0; i < 4; i++) {
			double temp = locs.get(i).getX() + locs.get(i).getZ();
			if( temp > highest) {
				highest = temp;
				southEast = locs.get(i);
			}
		}
	}
	
	public void NWCorner(List<Location> locs) {
		double highest = 0;
		for(int i = 0; i < 4; i++) {
			double temp = locs.get(i).getX() + locs.get(i).getZ();
		}
	}
}
