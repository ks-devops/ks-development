
package org.kuali.student.rules.factfinder.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 2.1
 * Tue Sep 30 14:17:02 EDT 2008
 * Generated source version: 2.1
 */

@XmlRootElement(name = "fetchFactStructureInfoResponse", namespace = "http://student.kuali.org/poc/wsdl/brms/factfinder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fetchFactStructureInfoResponse", namespace = "http://student.kuali.org/poc/wsdl/brms/factfinder")

public class FetchFactStructureInfoResponse {

    @XmlElement(name = "return")
    private org.kuali.student.rules.factfinder.dto.FactStructureDTO _return;

    public org.kuali.student.rules.factfinder.dto.FactStructureDTO getReturn() {
        return this._return;
    }

    public void setReturn(org.kuali.student.rules.factfinder.dto.FactStructureDTO new_return)  {
        this._return = new_return;
    }

}

