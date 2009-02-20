package org.kuali.student.core.organization.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.student.core.entity.Attribute;

@Entity
@Table(name = "KS_ORG_TYPE_ATTR_T")
public class OrgTypeAttribute extends Attribute<OrgType> {

}
