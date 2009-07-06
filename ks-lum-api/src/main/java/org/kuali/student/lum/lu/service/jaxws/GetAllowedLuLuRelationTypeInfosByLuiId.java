
package org.kuali.student.lum.lu.service.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 2.1.4
 * Tue Feb 24 12:25:30 EST 2009
 * Generated source version: 2.1.4
 */

@XmlRootElement(name = "getAllowedLuLuRelationTypeInfosByLuiId", namespace = "http://student.kuali.org/lum/lu")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllowedLuLuRelationTypeInfosByLuiId", namespace = "http://student.kuali.org/lum/lu", propOrder = {"luiId","relatedLuiId"})

public class GetAllowedLuLuRelationTypeInfosByLuiId {

    @XmlElement(name = "luiId")
    private java.lang.String luiId;
    @XmlElement(name = "relatedLuiId")
    private java.lang.String relatedLuiId;

    public java.lang.String getLuiId() {
        return this.luiId;
    }

    public void setLuiId(java.lang.String newLuiId)  {
        this.luiId = newLuiId;
    }

    public java.lang.String getRelatedLuiId() {
        return this.relatedLuiId;
    }

    public void setRelatedLuiId(java.lang.String newRelatedLuiId)  {
        this.relatedLuiId = newRelatedLuiId;
    }

}

