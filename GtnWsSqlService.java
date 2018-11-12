package com.stpl.gtn.gtn2o.ws.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;

import com.stpl.gtn.gtn2o.ws.bean.sql.GtnWsSqlEntityElement;
import com.stpl.gtn.gtn2o.ws.bean.sql.GtnWsSqlRootElement;
import com.stpl.gtn.gtn2o.ws.exception.GtnFrameworkGeneralException;
import com.stpl.gtn.gtn2o.ws.logger.GtnWSLogger;

public class GtnWsSqlService {

	private final Map<String, String> queryMap = new HashMap<>();
	private static final GtnWSLogger GTNLOGGER = GtnWSLogger.getGTNLogger(GtnWsSqlService.class);

	private GtnWsSqlService() {
		try {
			init();
		} catch (IOException | JAXBException e) {
			GTNLOGGER.error(e.getMessage());
		}
	}

	private void init() throws IOException, JAXBException {
		List<String> filePaths = loadAllFilePaths();
		for (String filePath : filePaths) {
			loadAllQueriesFromFile(filePath);
		}
	}

	private List<String> loadAllFilePaths() throws IOException {
		List<String> filePathsList = new ArrayList<>();
		Properties properties = new Properties();
		InputStream propertiesFileStream = getInputStream("/properties/sql-resources.properties");
		properties.load(propertiesFileStream);
		Iterator<?> iterator = properties.keySet().iterator();
		while (iterator.hasNext()) {
			filePathsList.add(properties.get(iterator.next()).toString());
		}
		return filePathsList;
	}

	private void loadAllQueriesFromFile(String filePath) throws JAXBException {
		InputStream configInStream = getInputStream(filePath);
		getResources(configInStream);
	}

	private void getResources(InputStream configInStream) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(GtnWsSqlRootElement.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		GtnWsSqlRootElement que = (GtnWsSqlRootElement) jaxbUnmarshaller.unmarshal(configInStream);
		List<GtnWsSqlEntityElement> list = que.getSqlEntity();
		for (GtnWsSqlEntityElement ans : list) {
			queryMap.put(ans.getSqlID(), ans.getSqlQuery());
		}
	}

	private Map<String, String> getqueryMap() {
		return queryMap;
	}

	public String getQuery(String sqlId) {
		return getqueryMap().get(sqlId);
	}

	private InputStream getInputStream(String filePath) {
		InputStream inputStream = GtnWsSqlService.class.getResourceAsStream(filePath);
		if (inputStream == null) {
			inputStream = GtnWsSqlService.class.getClassLoader().getResourceAsStream(filePath);
		}
		if (inputStream == null) {
			inputStream = getClass().getResourceAsStream(filePath);
		}
		if (inputStream == null) {
			inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
		}
		return inputStream;
	}

	@SuppressWarnings("rawtypes")
	public String getQuery(List input, String queryName) {
		StringBuilder sql = new StringBuilder();
		try {
			sql = new StringBuilder(getQuery(queryName));
			if (input != null) {
				for (Object temp : input) {
					if (sql.indexOf("?") != -1) {
						sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, String.valueOf(temp));
					}
				}
			}

		} catch (Exception ex) {
			GTNLOGGER.error("Exception in getQuery", ex);
		}
		return sql.toString();
	}

	@SuppressWarnings("rawtypes")
	public String getReplacedQuery(List input, String query) {
		StringBuilder sql = new StringBuilder();
		try {
			sql = new StringBuilder(query);
			if (input != null) {
				for (Object temp : input) {
					if (sql.indexOf("?") != -1) {
						sql.replace(sql.indexOf("?"), sql.indexOf("?") + 1, String.valueOf(temp));
					}
				}
			}

		} catch (Exception ex) {
			GTNLOGGER.error("Exception in getQuery", ex);
		}
		return sql.toString();
	}

	public List<Object[]> getResultFromProcedure(String sqlQuery, Object[] paramArray)
			throws GtnFrameworkGeneralException {
		GTNLOGGER.debug("Enter getResultFromProcedure ");

		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource) initialContext.lookup("java:jboss/datasources/jdbc/appDataPool");
			if (datasource != null) {
				return callResultProcedure(sqlQuery, paramArray, datasource);
			}
			GTNLOGGER.debug("Exiting getResultFromProcedure");
			return Collections.emptyList();
		} catch (NamingException namingException) {
			GTNLOGGER.error("Exception getResultFromProcedure", namingException);
			throw new GtnFrameworkGeneralException("Exception getResultFromProcedure", namingException);
		}
	}

	private List<Object[]> callResultProcedure(String sqlQuery, Object[] paramArray, DataSource datasource) {
		String procedureCallingQuery = getQuery(sqlQuery, paramArray.length);
		GTNLOGGER.info(procedureCallingQuery);
		try (Connection connection = datasource.getConnection();
				CallableStatement statement = connection.prepareCall(procedureCallingQuery);) {
			for (int i = 0; i < paramArray.length; i++) {
				statement.setObject(i + 1, paramArray[i]);
			}
			GTNLOGGER.debug("Ending callResultProcedure");
			return convertResultSetToList(statement.executeQuery());
		} catch (Exception ex) {
			GTNLOGGER.error("Exception in callResultProcedure", ex);
		}
		return Collections.emptyList();
	}

	private List<Object[]> convertResultSetToList(ResultSet rs) throws GtnFrameworkGeneralException {
		List<Object[]> objList = new ArrayList<>();
		if (rs != null) {
			try {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				int columnCount = rsMetaData.getColumnCount();
				Object[] header = new Object[columnCount];
				for (int i = 1; i <= columnCount; ++i) {
					Object label = rsMetaData.getColumnLabel(i);
					header[i - 1] = label;
				}
				while (rs.next()) {
					Object[] str = new Object[columnCount];
					for (int i = 1; i <= columnCount; ++i) {
						Object obj = rs.getObject(i);
						str[i - 1] = obj;
					}
					objList.add(str);
				}
			} catch (Exception ex) {
				GTNLOGGER.error("", ex);
				throw new GtnFrameworkGeneralException(ex);
			} finally {
				try {
					rs.close();
				} catch (SQLException ex) {
					GTNLOGGER.error("", ex);
				}
			}
			
		}

		return objList;
	}

	private String getQuery(String procedureName, int noOfArgs) {
		StringBuilder procedureToCall = new StringBuilder("{call ");
		procedureToCall.append(procedureName);
		for (int i = 0; i < noOfArgs; i++) {
			if (i == 0) {
				procedureToCall.append('(');
			}
			procedureToCall.append("?,");
			if (i == noOfArgs - 1) {
				procedureToCall.append(')');
			}
		}
		procedureToCall.replace(procedureToCall.lastIndexOf(","), procedureToCall.lastIndexOf(",") + 1,
				StringUtils.EMPTY);
		procedureToCall.append('}');
		return procedureToCall.toString();
	}
}
