
package org.kuali.student.poc.personidentity.person.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by the CXF 2.0.4-incubator
 * Thu Mar 27 11:42:24 EDT 2008
 * Generated source version: 2.0.4-incubator
 * 
 */

@XmlRootElement(name = "searchForPersonIdsByRelationResponse", namespace = "http://student.kuali.org/poc/wsdl/personidentity/person/service")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchForPersonIdsByRelationResponse", namespace = "http://student.kuali.org/poc/wsdl/personidentity/person/service")

public class SearchForPersonIdsByRelationResponse {

    @XmlElement(name = "return")
    private java.util.List<String> _return;

    public java.util.List<String> get_return() {
        return this._return;
    }
    
    public void set_return( java.util.List<String> new_return ) {
        this._return = new_return;
    }
    
}

