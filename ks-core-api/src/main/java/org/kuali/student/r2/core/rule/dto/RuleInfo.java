/*
 * Copyright 2013 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.student.r2.core.rule.dto;

import org.kuali.student.r2.common.dto.IdEntityInfo;
import org.kuali.student.r2.core.rule.infc.Rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RuleInfo", propOrder = {"id",
        "name",
        "descr",
        "attributes", "_futureElements" })
public class RuleInfo extends IdEntityInfo implements Rule, Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAnyElement
    private List<Object> _futureElements;

    public RuleInfo() {

    }

    public RuleInfo(Rule input) {
        super(input);
    }
}
