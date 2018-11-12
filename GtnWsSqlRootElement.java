
package com.stpl.gtn.gtn2o.ws.bean.sql;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sql")
public class GtnWsSqlRootElement {
	public GtnWsSqlRootElement() {
		/**
		 * empty constructor
		 */
	}

	private List<GtnWsSqlEntityElement> sqlEntity;

	public List<GtnWsSqlEntityElement> getSqlEntity() {
		return sqlEntity == null ? sqlEntity : Collections.unmodifiableList(sqlEntity);
	}

	@XmlElement(name = "entity")
	public void setSqlEntity(List<GtnWsSqlEntityElement> sqlEntity) {
		this.sqlEntity = Collections.unmodifiableList(sqlEntity);
	}

}
