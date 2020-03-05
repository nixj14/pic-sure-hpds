package edu.harvard.hms.dbmi.avillach.hpds.processing;

import edu.harvard.hms.dbmi.avillach.hpds.data.query.Query;
import edu.harvard.hms.dbmi.avillach.hpds.exception.NotEnoughMemoryException;

public interface Processor {

	public void runQuery(Query query, AsyncResult asyncResult) throws NotEnoughMemoryException;

}
