package org.harty911.rhtool.core.db;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.harty911.rhtool.core.model.RHModelObject;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.RHECanal;
import org.harty911.rhtool.core.model.objects.RHEClassif;
import org.harty911.rhtool.core.model.objects.RHEContrat;
import org.harty911.rhtool.core.model.objects.RHEInitiative;
import org.harty911.rhtool.core.model.objects.RHEMotif;
import org.harty911.rhtool.core.model.objects.RHEMotifPro;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.TalkDoc;
import org.harty911.rhtool.core.model.objects.User;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;


public class RHDbConnector {
	
	public final static int DB_SCHEMA_VERSION = 1;
	
	private static final String SCHEMA_VERSION = "SCHEMA_VERSION";
	
	private final ConnectionSource connectionSource;
	private DatabaseConnection batchConnection = null;
	
	private final static Logger LOGGER = Logger.getLogger( RHDbConnector.class.getName());
	
	
	public static RHDbConnector createDatabase( File dbDir) throws SQLException {
		File dbFile = new File( dbDir, "RHTool.db");
		if( dbFile.exists())
			throw new SQLException("Database file already exists: " + dbFile);
		
		RHDbConnector db = new RHDbConnector( dbFile);
		db._createDB();
		db._upgradeDB();
		return db;
	}
	
	
	public static RHDbConnector openDatabase( File dbDir) throws SQLException {
		File dbFile = new File( dbDir, "RHTool.db");
		if( !dbFile.exists())
			throw new SQLException("Database file not found: " + dbFile);

		RHDbConnector db = new RHDbConnector( dbFile);
		db._upgradeDB();
		return db;
	}

	
	private RHDbConnector( File dbFile) throws SQLException {
		try {
			Class.forName( "org.sqlite.JDBC");
		} catch( ClassNotFoundException e) {
			throw new SQLException("JDBC Driver not found", e);
		}
		
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY,"WARNING");
		//System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY,"DEBUG");
		
	    // create a connection source to our database
        connectionSource = new JdbcConnectionSource("jdbc:sqlite:"+dbFile.getPath());
        
	}

	
	public void setProperty( String name, String value) {
		DbProperty dp = new DbProperty(name,value);
		try {
			Dao<DbProperty, String> dao = DaoManager.createDao(connectionSource, DbProperty.class);
			dao.createOrUpdate(dp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public String getProperty( String name) {
		try {
			Dao<DbProperty, String> dao = DaoManager.createDao(connectionSource, DbProperty.class);
			DbProperty dp = dao.queryForId(name);
			if( dp==null)
				return null;
			else
				return dp.getValue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	
	public void batchModeBegin() throws SQLException {
		batchConnection = connectionSource.getReadWriteConnection();
		batchConnection.setAutoCommit(false);
		if( !connectionSource.saveSpecialConnection(batchConnection))
			batchConnection=null;
	}	
	
	
	public void batchModeEnd() throws SQLException {
		if( batchConnection==null)
			return;
		batchConnection.setAutoCommit(true);
		connectionSource.clearSpecialConnection(batchConnection);
		connectionSource.releaseConnection(batchConnection);
		batchConnection=null;
	}

	public <T extends RHModelObject> List<T> getAll(Class<T> clazz) throws SQLException {
		Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
		return dao.queryForAll();
	}


	public <T extends RHModelObject> List<T> getByField(Class<T> clazz, Map<String, Object> fieldValues) throws SQLException {
		Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
		return dao.queryForFieldValuesArgs(fieldValues);
	}


	public <T extends RHModelObject> void updateOrCreate( T object) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<T, ?> dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, object.getClass());
		dao.createOrUpdate(object);
	}
	
	public <T extends RHModelObject> void create(T object) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<T, ?> dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, object.getClass());
		dao.create(object);		
	}

	public <T extends RHModelObject> void update(T object) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<T, ?> dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, object.getClass());
		dao.update(object);		
	}

	public <T extends RHModelObject> void delete(T object) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<T, ?> dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, object.getClass());
		dao.delete(object);
	}
	
	public <T extends RHModelObject> void refresh(T object) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<T, ?> dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, object.getClass());
		dao.refresh(object);		
	}


	public <T extends RHModelObject> List<Field> getDBFields( Class<T> clazz) {
		List<Field> fields = new LinkedList<>();
		for( Field field : getAllFields(clazz)) {
			if( field.getAnnotation(DatabaseField.class)!=null) {
				fields.add( field);
			}
		}
		return fields;
	}
	
	
	@SuppressWarnings("rawtypes")
	private static List<Field> getAllFields(Class clazz) {
		List<Field> fields = new LinkedList<>();
		for( Field f : clazz.getDeclaredFields())
			fields.add(f);
		if( clazz.getSuperclass()!=null)
			fields.addAll(getAllFields(clazz.getSuperclass()));
		return fields;
	}	

	
	public void close() throws SQLException {
		if( connectionSource!=null) {
			connectionSource.close();
		}
	}


	private void _createDB() throws SQLException {
		// internal
		TableUtils.createTableIfNotExists( connectionSource, DbProperty.class);
		setProperty(SCHEMA_VERSION, String.valueOf(DB_SCHEMA_VERSION));
		
		// RH objects
		TableUtils.createTableIfNotExists( connectionSource, User.class);
		TableUtils.createTableIfNotExists( connectionSource, Employee.class);
		TableUtils.createTableIfNotExists( connectionSource, Talk.class);
		TableUtils.createTableIfNotExists( connectionSource, TalkDoc.class);
		// Enums
		TableUtils.createTableIfNotExists( connectionSource, RHEContrat.class);
		TableUtils.createTableIfNotExists( connectionSource, RHEClassif.class);
		TableUtils.createTableIfNotExists( connectionSource, RHEInitiative.class);
		TableUtils.createTableIfNotExists( connectionSource, RHECanal.class);
		TableUtils.createTableIfNotExists( connectionSource, RHEMotif.class);
		TableUtils.createTableIfNotExists( connectionSource, RHEMotifPro.class);
		
	}
	

	private void _upgradeDB() throws SQLException {
		int dbVersion = 0;
		try{
			dbVersion = Integer.parseInt( getProperty(SCHEMA_VERSION));
		} catch( NumberFormatException e) {
			// default value = 0
		}
		LOGGER.info( "SchemaVersion="+dbVersion);

/*
		if( dbVersion < 2) {
			LOGGER.info("Upgrade to DB schema version 2");
			Dao<Employee,?> dao = DaoManager.createDao(connectionSource, Employee.class);
			dao.executeRaw("ALTER TABLE `t_employee` ADD COLUMN weight INTEGER;");
		} 
		if( dbVersion < 3) {
			LOGGER.info("Upgrade to DB schema version 3");
			Dao<Employee,?> dao = DaoManager.createDao(connectionSource, Employee.class);
			dao.executeRaw("ALTER TABLE `t_employee` ADD COLUMN weight INTEGER;");
		}
*/		
		setProperty(SCHEMA_VERSION, String.valueOf(DB_SCHEMA_VERSION));
	}
}
