package org.harty911.rhtool.core.db;

import java.sql.SQLException;

public class DbVersionException extends SQLException {

	public DbVersionException(int appVersion, int dbVersion) {
		super( "Incompatible application level: "+appVersion+" (DB level is "+dbVersion+")");
	}

	private static final long serialVersionUID = 6050177633911172430L;

}
