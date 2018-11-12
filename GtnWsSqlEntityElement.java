
package com.stpl.gtn.gtn2o.ws.bean.sql;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;




public class GtnWsSqlEntityElement {

	private String sqlQuery = "";

	private String sqlID = "";

	public String getSqlQuery() {
		return sqlQuery;
	}

	@XmlElement(name = "query")
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getSqlID() {
		return sqlID;
	}

	@XmlAttribute(name = "id")
	public void setSqlID(String sqlID) {
		this.sqlID = sqlID;
	}

}
