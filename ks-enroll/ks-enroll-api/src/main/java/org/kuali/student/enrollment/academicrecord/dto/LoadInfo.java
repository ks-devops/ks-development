package org.kuali.student.enrollment.academicrecord.dto;

import org.kuali.student.enrollment.academicrecord.infc.Load;
import org.kuali.student.r2.common.dto.IdNamelessEntityInfo;

import javax.xml.bind.Element;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * TODO: There's another LoadInfo in Grading service - which seems half-based, check during review
 * @Version 2.0
 * @Author Sri komandur@uw.edu
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoadInfo", propOrder = {
        "id", "meta", "attributes",
        "totalCredits", "loadLevelCode",
        "typeKey", "stateKey", "_futureElements"})
public class LoadInfo extends IdNamelessEntityInfo implements Load, Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement
    private String totalCredits;
    @XmlElement
    private String loadLevelCode;
    @XmlAnyElement
    private List<Element> _futureElements;

    public LoadInfo() {

    }

    public LoadInfo(Load load) {
        super(load);
        if (null != load) {
            this.totalCredits = load.getTotalCredits();
            this.loadLevelCode = load.getLoadLevelCode();
        }
    }

    @Override
    public String getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(String totalCredits) {
        this.totalCredits = totalCredits;
    }

    @Override
    public String getLoadLevelCode() {
        return loadLevelCode;
    }

    public void setLoadLevelCode(String loadLevelCode) {
        this.loadLevelCode = loadLevelCode;
    }
}
