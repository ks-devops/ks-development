
package org.kuali.student.rules.rulemanagement.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 2.1
 * Wed Oct 08 11:53:14 EDT 2008
 * Generated source version: 2.1
 */

@XmlRootElement(name = "updateBusinessRuleResponse", namespace = "http://student.kuali.org/poc/wsdl/brms/rulemanagement")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateBusinessRuleResponse", namespace = "http://student.kuali.org/poc/wsdl/brms/rulemanagement")

public class UpdateBusinessRuleResponse {

    @XmlElement(name = "return")
    private org.kuali.student.rules.rulemanagement.dto.StatusDTO _return;

    public org.kuali.student.rules.rulemanagement.dto.StatusDTO getReturn() {
        return this._return;
    }

    public void setReturn(org.kuali.student.rules.rulemanagement.dto.StatusDTO new_return)  {
        this._return = new_return;
    }

}

