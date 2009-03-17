package org.kuali.student.lum.ui.requirements.server.gwt;

import java.util.List;

import org.kuali.student.lum.lu.dto.ReqComponentTypeInfo;
import org.kuali.student.lum.ui.requirements.client.service.RequirementsService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Zdenek Zraly
 */
public class RequirementsServiceGwt extends RemoteServiceServlet implements RequirementsService {

    private static final long serialVersionUID = 822326113643828855L;

    private RequirementsService serviceImpl;
	
    public RequirementsService getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(RequirementsService serviceImpl) {
    	this.serviceImpl = serviceImpl;
    }
    
    public List<ReqComponentTypeInfo> getReqComponentTypesForLuStatementType(String luStatementTypeKey) throws Exception {
        return serviceImpl.getReqComponentTypesForLuStatementType(luStatementTypeKey);
    }
}
