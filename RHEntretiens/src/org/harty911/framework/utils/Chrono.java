package org.harty911.framework.utils;

public class Chrono {

	private long t0;

	public Chrono() {
		reset();
	}

	public void reset() {
		t0 = System.currentTimeMillis();
	}

	public double seconds() {
		double dt = System.currentTimeMillis() - t0;
		dt /= 1000;
		return dt;
	}
	
	@Override
	public String toString() {
		return String.format("%.3fs", seconds());
	}
	
	
}
