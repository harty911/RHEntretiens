package org.harty911.rhtool.core.db;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
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
import org.harty911.rhtool.core.model.objects.RHETypeTalkDoc;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.TalkDoc;
import org.harty911.rhtool.core.model.objects.User;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

/**
 * Adapter class between RHModel and database (supported by ORMLite in this case
 * @author P13
 *
 */
public class RHDbConnector {
	
	public static final String DB_FILENAME = "RHTool.db";

	public final static int DB_SCHEMA_VERSION = 4;
	
	private static final String SCHEMA_VERSION = "SCHEMA_VERSION";
	
	private final ConnectionSource connectionSource;
	private DatabaseConnection batchConnection = null;

	private File dbDir;
	
	private final static Logger LOGGER = Logger.getLogger( RHDbConnector.class.getName());
	
	
	public static RHDbConnector openDatabase( File dbDir, boolean createIfNotExists) throws SQLException {
		File dbFile = new File( dbDir, DB_FILENAME);
		boolean create = false;
		if( !dbFile.exists()) {
			if( ! createIfNotExists)
				throw new SQLException("Database file not found: " + dbFile);
			create = true;
		}

		RHDbConnector db = new RHDbConnector( dbFile);
		if( create) 
			db._createDB();
		db._upgradeDB();
		return db;
	}

	
	public static boolean isDatabase( File dbDir) {
		if( dbDir==null)
			return false;
		File dbFile = new File( dbDir, DB_FILENAME);
		return dbFile.exists();
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
        dbDir = dbFile.getParentFile();
        
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

	public <T extends RHModelObject> List<T> getByField( Class<T> clazz, Map<String, Object> fieldValues) throws SQLException {
		Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
		return dao.queryForFieldValuesArgs(fieldValues);
	}

	public <T extends RHModelObject> List<T> getByQuery( Class<T> clazz, Map<String, Object> fieldValues, Map<String, Boolean> fieldOrders) throws SQLException {
		if( fieldValues==null || fieldValues.size() == 0)
			return Collections.emptyList();
		Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
		QueryBuilder<T,?> qb = dao.queryBuilder();
		Where<T,?> w = qb.where();
		for( Map.Entry<String,Object> entry : fieldValues.entrySet())
			w.eq( entry.getKey(), new SelectArg(entry.getValue()));
		w.and(fieldValues.size());
		
		if( fieldOrders!=null && fieldOrders.size() != 0)
			for(  Map.Entry<String,Boolean> entry :fieldOrders.entrySet())
				qb.orderBy(entry.getKey(), entry.getValue());
		
		return qb.query();
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
		TableUtils.createTableIfNotExists( connectionSource, RHETypeTalkDoc.class);
	}
	

	private void _upgradeDB() throws SQLException {
		int dbVersion = 0;
		try{
			dbVersion = Integer.parseInt( getProperty(SCHEMA_VERSION));
		} catch( NumberFormatException e) {
			// default value = 0
		}
		LOGGER.info( "current DB version="+dbVersion);
		if( dbVersion > DB_SCHEMA_VERSION) { 
			throw new DbVersionException( DB_SCHEMA_VERSION, dbVersion);
		}
		
		if( dbVersion < 2) {
			LOGGER.info("Upgrade to DB schema version 2");
			Dao<Employee,?> dao = DaoManager.createDao(connectionSource, Employee.class);
			// Ajout des champs manquant (version 2)
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN detail VARCHAR DEFAULT '';");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN next_date VARCHAR;");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN action_status INTEGER;");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN action_detail VARCHAR DEFAULT '';");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN action_date VARCHAR;");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN accomp_status INTEGER;");
			dao.executeRaw("ALTER TABLE `t_entretien` ADD COLUMN accomp_detail VARCHAR DEFAULT '';");
		} 

		if( dbVersion < 3) {
			LOGGER.info("Upgrade to DB schema version 3");
			Dao<Employee,?> dao = DaoManager.createDao(connectionSource, Employee.class);
			dao.executeRaw("ALTER TABLE `t_collaborateur` ADD COLUMN replace INTEGER DEFAULT -1;");
		}
		if( dbVersion < 4) {
			LOGGER.info("Upgrade to DB schema version 4");
			Dao<Employee,?> dao = DaoManager.createDao(connectionSource, Employee.class);
			dao.executeRaw("ALTER TABLE `t_e_canal` ADD COLUMN ordre INTEGER DEFAULT 0;");
			dao.executeRaw("ALTER TABLE `t_e_classif` ADD COLUMN ordre INTEGER DEFAULT 0;");
			dao.executeRaw("ALTER TABLE `t_e_contrat` ADD COLUMN ordre INTEGER DEFAULT 0;");
			dao.executeRaw("ALTER TABLE `t_e_intitiative` ADD COLUMN ordre INTEGER DEFAULT 0;");
			dao.executeRaw("ALTER TABLE `t_e_motif` ADD COLUMN ordre INTEGER DEFAULT 0;");
			dao.executeRaw("ALTER TABLE `t_e_typetalkdoc` ADD COLUMN ordre INTEGER DEFAULT 0;");
			
			// NOT SUPPORTED IN SQLITE : dao.executeRaw("ALTER TABLE `t_collaborateur` DROP COLUMN replace;");
			dao.executeRaw("ALTER TABLE `t_utilisateur` ADD COLUMN replace INTEGER DEFAULT -1;");
		}		
		
		setProperty(SCHEMA_VERSION, String.valueOf(DB_SCHEMA_VERSION));
	}

	/**
	 * @return the document directory near the DB file (create it if needed)
	 */
	public File getDocDir() {
		File docDir = new File( dbDir, "DBDocs");
		if( !docDir.exists())
			docDir.mkdir();
		return docDir;
	}
}
