
package org.kuali.student.poc.personidentity.person.jaxws;

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

@XmlRootElement(name = "assignPersonType", namespace = "http://student.kuali.org/poc/wsdl/personidentity/person/service")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assignPersonType", namespace = "http://student.kuali.org/poc/wsdl/personidentity/person/service")

public class AssignPersonType {

    @XmlElement(name = "personId")
    private java.lang.String personId;
    @XmlElement(name = "personTypeKey")
    private java.lang.String personTypeKey;

    public java.lang.String getPersonId() {
        return this.personId;
    }
    
    public void setPersonId( java.lang.String newPersonId ) {
        this.personId = newPersonId;
    }
    
    public java.lang.String getPersonTypeKey() {
        return this.personTypeKey;
    }
    
    public void setPersonTypeKey( java.lang.String newPersonTypeKey ) {
        this.personTypeKey = newPersonTypeKey;
    }
    
}

