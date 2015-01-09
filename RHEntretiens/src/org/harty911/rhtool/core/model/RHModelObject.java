package org.harty911.rhtool.core.model;

import com.j256.ormlite.field.DatabaseField;

public abstract class RHModelObject {

	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField
	private boolean deleted = false;

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"("+getId()+")";
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void delete() {
		this.deleted = true;
	}
	public void undelelte() {
		this.deleted = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RHModelObject other = (RHModelObject)obj;
		return (id == other.id);
	}
}
