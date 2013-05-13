package com.goldrushmc.bukkit.Directions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class GetDirections {

	public List<Location> getDirection(Location loc1, Location loc2, Location loc3,
			Location loc4) {
		
		List<Location> directions = new ArrayList<Location>();

		Location southwest = null;
		Location northwest = null;
		Location southeast = null;
		Location northeast = null;

		int x1 = loc1.getBlockX();
		int z1 = loc1.getBlockZ();

		int x2 = loc2.getBlockX();
		int z2 = loc2.getBlockZ();

		int x3 = loc3.getBlockX();
		int z3 = loc3.getBlockZ();

		int x4 = loc4.getBlockX();
		int z4 = loc4.getBlockZ();

		if (x1 < x2 && z1 < z2) {

			if (x1 < x3 && z1 < z3) {

				if (x1 < x4 && z1 < z4) {

					northwest = loc1;

				} else {

					northwest = loc4;

				}
			} else if (x3 < x4 && z3 < z4) {

				northwest = loc3;

			} else {

				northwest = loc4;

			}

		} else if (x2 < x3 && z2 < z3) {

			if (x2 < x4 && z2 < z4) {

				northwest = loc2;

			} else {

				northwest = loc4;

			}

		} else if (x3 < x4 && z3 < z4) {

			northwest = loc3;

		} else {

			northwest = loc4;

		}

		if (x1 > x2 && z1 > z2) {

			if (x1 > x3 && z1 > z3) {

				if (x1 > x4 && z1 > z4) {

					northwest = loc1;

				} else {

					northwest = loc4;

				}
			} else if (x3 > x4 && z3 > z4) {

				northwest = loc3;

			} else {

				northwest = loc4;

			}

		} else if (x2 > x3 && z2 > z3) {

			if (x2 > x4 && z2 > z4) {

				northwest = loc2;

			} else {

				northwest = loc4;

			}

		} else if (x3 > x4 && z3 > z4) {

			northwest = loc3;

		} else {

			northwest = loc4;

		}

		if (x1 > x2 && z1 < z2) {

			if (x1 > x3 && z1 < z3) {

				if (x1 > x4 && z1 < z4) {

					northeast = loc1;

				} else {

					northeast = loc4;

				}
			} else if (x3 > x4 && z3 < z4) {

				northeast = loc3;

			} else {

				northeast = loc4;

			}

		} else if (x2 > x3 && z2 < z3) {

			if (x2 > x4 && z2 < z4) {

				northeast = loc2;

			} else {

				northeast = loc4;

			}

		} else if (x3 > x4 && z3 < z4) {

			northeast = loc3;

		} else {

			northeast = loc4;

		}

		if (x1 < x2 && z1 > z2) {

			if (x1 < x3 && z1 > z3) {

				if (x1 < x4 && z1 > z4) {

					southwest = loc1;

				} else {

					southwest = loc4;

				}
			} else if (x3 < x4 && z3 > z4) {

				southwest = loc3;

			} else {

				southwest = loc4;

			}

		} else if (x2 < x3 && z2 > z3) {

			if (x2 < x4 && z2 > z4) {

				southwest = loc2;

			} else {

				southwest = loc4;

			}

		} else if (x3 < x4 && z3 > z4) {

			southwest = loc3;

		} else {

			southwest = loc4;

		}
		
		directions.add(northeast);
		directions.add(southeast);
		directions.add(northwest);
		directions.add(southwest);
		
		return directions;

	}

	
}
