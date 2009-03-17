package org.kuali.student.lum.ui.requirements.client.service;

import java.util.List;

import org.kuali.student.lum.lu.dto.ReqComponentTypeInfo;


/**
 * @author Zdenek Zraly
 * This class lists all of the methods that the remote calls between client and servlet make, 
 * most of these will be verbatim from the web service  
 *
 */
//TODO how do we do exceptions
public interface RequirementsRemoteService {
    public List<ReqComponentTypeInfo> getReqComponentTypesForLuStatementType(String luStatementTypeKey) throws Exception;
}
