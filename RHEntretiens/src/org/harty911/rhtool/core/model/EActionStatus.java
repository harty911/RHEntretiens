package org.harty911.rhtool.core.model;

public enum EActionStatus {
	NOTHING,
	OPEN,
	IN_PROGRESS,
	DONE,
	CANCELED;
	
	  @Override
	  public String toString() {
	    switch(this) {
	    case NOTHING: 	return "Aucune";
	    case OPEN: 		return "A mener";
	    case IN_PROGRESS: return"En cours";
	    case DONE: 		return"Finalis�";
	    case CANCELED: 	return"Annul�";
	    default: throw new IllegalArgumentException();
	    }
	  }

	public boolean isOpen() {
		return( this==OPEN || this==IN_PROGRESS);
	}

	public boolean isNothing() {
		return( this==NOTHING);
	}
}
