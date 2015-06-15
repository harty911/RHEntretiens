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
	    case DONE: 		return"Finalisé";
	    case CANCELED: 	return"Annulé";
      default: throw new IllegalArgumentException();
	    }
	  }
}
