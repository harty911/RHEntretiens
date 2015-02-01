package org.harty911.rhtool.core.model;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.harty911.rhtool.core.db.RHDbConnector;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.TalkDoc;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.core.utils.RHModelUtils;

public class RHModel {
	
	public final static Logger LOGGER = Logger.getLogger( RHModel.class.getName());
		
	RHDbConnector db;

	User userContext;

	boolean isBatchMode = false;
	
	public RHModel( RHDbConnector rhDb) {
		db = rhDb;
		RHModelUtils.createAdminIfNotExists(this);
	}


	public RHDbConnector getDbConnector() {
		return db;
	}

	/**
	 * Set current user context (login) 
	 * @param user
	 */
	public void setUserContext( User user) {
		userContext = user;
		LOGGER.info("User '"+ userContext.getLogin() +"' connected");
	}
	
	public User getUserContext() {
		return userContext;
	}

	public void close() {
		try {
			db.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to close DB-Connector", e);
		}
	}

	public <T extends RHModelObject> void save( T object) {
		try {
			db.updateOrCreate( object);
			LOGGER.fine(object+" saved");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable save object "+object, e);
		}
	}
	
	
	public <T extends RHModelObject> void refresh( T object) {
		try {
			db.refresh( object);
			LOGGER.fine(object+" refreshed");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable refresh object "+object, e);
		}
	}


	/**
	 * Lists DB stored fields for the given RHModelObject class
	 * @param clazz
	 * @return DB field list
	 */
	public <T extends RHModelObject> List<Field> getDBFields(Class<T> clazz) {
		return db.getDBFields(clazz);
	}	

	
	/**
	 * @return list of available users
	 */
	public List<User> getUsers() {
		try {
			LOGGER.fine( "List User[]");
			return db.getAll(User.class);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get Users", e);
			return Collections.emptyList();
		}
	}

	
	/**
	 * Lists RHEnum values for the specified class
	 * @param clazz
	 * @return RHEnum values list
	 */
	public <T extends RHEnum> List<T> getEnumValues(Class<T> clazz) {
		try {
			LOGGER.fine( "List "+clazz.getSimpleName()+"[]");
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("deleted", false);
			return db.getByField(clazz, fields);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get "+clazz.getSimpleName()+"s", e);
			return Collections.emptyList();
		}
	}	

	
	/**
	 * Get RHEnum value by text
	 * @param clazz
	 * @return RHEnum value
	 */
	public <T extends RHEnum> T getEnumValue( Class<T> clazz, String text) {
		try {
			LOGGER.fine( "List "+clazz.getSimpleName()+"[]");
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("deleted", false);
			fields.put("text", text);
			List<T> list = db.getByField(clazz, fields);
			if( list.size()!=1) {
				LOGGER.log(Level.SEVERE,"No "+clazz.getSimpleName()+" named "+text);
				return null;
			}
			return list.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get "+clazz.getSimpleName()+"s", e);
			return null;
		}
	}	

	

	/**
	 * Get a user by Login
	 * @param login the user login
	 * @return 
	 */
	public User getUserByLogin(String login) {
		try {
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("login", login);
			List<User> users = db.getByField(User.class, fields);
			return users.isEmpty() ? null : users.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get user", e);
			return null;
		}
	}

	
	/**
	 * @return list of available employees
	 */
	public List<Employee> getEmployees() {
		try {
			LOGGER.fine( "List Employee[]");
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("deleted", false);
			return db.getByField(Employee.class, fields);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get Employees", e);
			return Collections.emptyList();
		}
	}

	
	/**
	 * @return list of available talks
	 */
	public List<Talk> getTalks() {
		try {
			LOGGER.fine( "List Talk[]");
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("deleted", false);
			return db.getByField(Talk.class, fields);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get Talks", e);
			return Collections.emptyList();
		}
	}

	public List<Talk> getTalks( Employee emp) {
		try {
			LOGGER.fine( "List "+emp+" -> Talk[]");
			Map<String, Object> fields = new LinkedHashMap<>();
			Map<String, Boolean> orders = new LinkedHashMap<>();
			fields.put("deleted", false);
			fields.put("employee_id", emp);
			orders.put("date", false);
			return db.getByQuery(Talk.class, fields, orders);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to get LastTalk", e);
			return null;
		}
	}

	
	public void setBatchMode(boolean batch) {
		
		try {
			if( isBatchMode && !batch) {
				db.batchModeEnd();
			}
			else if( !isBatchMode && batch) {
				db.batchModeBegin();			
			}
			isBatchMode = batch;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to switch BatchMode to "+batch, e);
			e.printStackTrace();
		}
	}


	public void emptyTrashDoc() {
		LOGGER.fine("Remove files for deleted docs");
		try {
			Map<String, Object> fields = new LinkedHashMap<>();
			fields.put("deleted", true);
			for( RHDocument doc : db.getByField(TalkDoc.class, fields)) {
				doc.trash();
				db.delete(doc);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE,"Unable to trash TalkDocs", e);
		}
	}


}
