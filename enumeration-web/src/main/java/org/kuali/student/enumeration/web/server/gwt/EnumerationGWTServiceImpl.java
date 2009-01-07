package org.kuali.student.enumeration.web.server.gwt;

import java.util.Date;
import javax.xml.namespace.QName;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.kuali.student.enumeration.dto.EnumeratedValue;
import org.kuali.student.enumeration.dto.EnumeratedValueList;
import org.kuali.student.enumeration.dto.EnumerationMetaList;
import org.kuali.student.enumeration.service.EnumerationService;
import org.kuali.student.enumeration.web.client.service.EnumerationGWTService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EnumerationGWTServiceImpl extends RemoteServiceServlet implements EnumerationGWTService {

	 EnumerationService enumerationService; 
	 
	 public EnumerationGWTServiceImpl() {// Hard coding properties after removing Spring and before Guice
		 ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
		 factory.setServiceClass(EnumerationService.class);
		 factory.setWsdlLocation("classpath:wsdl/EnumerationService.wsdl");
		 factory.setServiceName(new QName("http://student.kuali.org/wsdl/EnumerationService","EnumerationService"));
		 factory.setAddress("http://localhost:8787/enumeration-services/EnumerationService");
		 enumerationService = (EnumerationService) factory.create();
	 }
	    public EnumerationMetaList fetchEnumerationMetas() {
	        
	        return enumerationService.findEnumerationMetas();
	    }
	    public EnumeratedValueList fetchEnumeration(String enumerationKey,String enumContextKey,String contextValue,Date   contextDate ){
	        return enumerationService.fetchEnumeration(enumerationKey, enumContextKey, contextValue, contextDate);
	    }
	    
	    public EnumeratedValue addEnumeratedValue(String enumerationKey,EnumeratedValue value){
	     return enumerationService.addEnumeratedValue(enumerationKey, value);

	    }

	    public EnumeratedValue updateEnumeratedValue(String enumerationKey,String code,EnumeratedValue value){
	        return enumerationService.updateEnumeratedValue(enumerationKey, code, value);
	    }

	    public boolean removeEnumeratedValue(String enumerationKey,String code){
	        return enumerationService.removeEnumeratedValue(enumerationKey, code);
	    }

	    
	    
	    public EnumerationService getEnumerationService() {
	        return enumerationService;
	    }
	    public void setEnumerationService(EnumerationService enumerationService) {
	        this.enumerationService = enumerationService;
	    }
}