package org.kuali.student.poc.common.ws.exceptions;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "org.kuali.student.poc.common.ws.exceptions.jaxws.OperationFailedExceptionBean")
public class OperationFailedException extends Exception {

	private static final long serialVersionUID = -4366768594030258759L;

    public OperationFailedException() {
        super();
    }

    public OperationFailedException(String message) {
         super(message);
     }

}
