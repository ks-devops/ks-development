/*
 * Copyright 2006-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.student.enrollment.class2.registration.admin.form;

import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.core.person.dto.PersonInfo;
import org.kuali.student.enrollment.class2.registration.admin.util.AdminRegConstants;
import org.kuali.student.r2.core.acal.dto.TermInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 6/17/14.
 */
public class AdminRegistrationForm extends UifFormBase implements Serializable {

    private String clientState;

    private PersonInfo personInfo;

    private String program;
    private String standing;
    private String credits;
    private String major;
    private String department;

    private String termCode;
    private TermInfo termInfo;
    private String termName;

    private RegistrationCourse pendingDropCourse = new RegistrationCourse();

    private int editRegisteredIndex;
    private int editWaitlistedIndex;

    private RegistrationCourse tempRegCourseEdit;
    private RegistrationCourse tempWaitlistCourseEdit;

    private List<RegistrationCourse> pendingCourses;
    private List<RegistrationIssue> registrationIssues = new ArrayList<RegistrationIssue>();
    private List<RegistrationCourse> registeredCourses = new ArrayList<RegistrationCourse>();
    private List<RegistrationCourse> waitlistedCourses = new ArrayList<RegistrationCourse>();
    private List<RegistrationCourse> coursesInProcess = new ArrayList<RegistrationCourse>();

    //KSENROLL-13558 :work around for incorrect Data
    private List<Principal> principalIDs = new ArrayList<Principal>();

    public AdminRegistrationForm(){
        clientState = AdminRegConstants.ClientStates.OPEN;
        editRegisteredIndex = -1;
        editWaitlistedIndex = -1;
        personInfo = new PersonInfo();
        this.resetPendingCourseValues();
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getStanding() {
        return standing;
    }

    public void setStanding(String standing) {
        this.standing = standing;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
    }

    public TermInfo getTermInfo() {
        return termInfo;
    }

    public void setTermInfo(TermInfo termInfo) {
        this.termInfo = termInfo;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName( String termName) {
        this.termName = termName;
    }

    public List<RegistrationCourse> getRegisteredCourses() {
        return registeredCourses;
    }

    public void setRegisteredCourses(List<RegistrationCourse> registeredCourses) {
        this.registeredCourses = registeredCourses;
    }

    public List<RegistrationCourse> getWaitlistedCourses() {
        return waitlistedCourses;
    }

    public void setWaitlistedCourses(List<RegistrationCourse> waitlistedCourses) {
        this.waitlistedCourses = waitlistedCourses;
    }

    public int getRegisteredCredits() {
        int credits = 0;
        for (RegistrationCourse course: registeredCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public int getWaitlistedCredits() {
        int credits = 0;
        for (RegistrationCourse course: waitlistedCourses) {
            credits += course.getCredits();
        }

        return credits;
    }

    public List<RegistrationCourse> getPendingCourses() {
        return pendingCourses;
    }

    public void setPendingCourses(List<RegistrationCourse> pendingCourses) {
        this.pendingCourses = pendingCourses;
    }

    public List<RegistrationIssue> getRegistrationIssues() {
        return registrationIssues;
    }

    public void setRegistrationIssues(List<RegistrationIssue> registrationIssues) {
        this.registrationIssues = registrationIssues;
    }

    public List<RegistrationCourse> getCoursesInProcess() {
        return coursesInProcess;
    }

    public void setCoursesInProcess(List<RegistrationCourse> coursesInProcess) {
        this.coursesInProcess = coursesInProcess;
    }

    public RegistrationCourse getPendingDropCourse() {
        return pendingDropCourse;
    }

    public void setPendingDropCourse(RegistrationCourse pendingDropCourse) {
        this.pendingDropCourse = pendingDropCourse;
    }

    public int getEditRegisteredIndex() {
        return editRegisteredIndex;
    }

    public void setEditRegisteredIndex(int editRegisteredIndex) {
        this.editRegisteredIndex = editRegisteredIndex;
    }

    public int getEditWaitlistedIndex() {
        return editWaitlistedIndex;
    }

    public void setEditWaitlistedIndex(int editWaitlistedIndex) {
        this.editWaitlistedIndex = editWaitlistedIndex;
    }

    public RegistrationCourse getTempRegCourseEdit() {
        return tempRegCourseEdit;
    }

    public void setTempRegCourseEdit(RegistrationCourse tempRegCourseEdit) {
        this.tempRegCourseEdit = tempRegCourseEdit;
    }

    public RegistrationCourse getTempWaitlistCourseEdit() {
        return tempWaitlistCourseEdit;
    }

    public void setTempWaitlistCourseEdit(RegistrationCourse tempWaitlistCourseEdit) {
        this.tempWaitlistCourseEdit = tempWaitlistCourseEdit;
    }

    public List<Principal> getPrincipalIDs() {
        return principalIDs;
    }

    public void setPrincipalIDs(List<Principal> principalIDs) {
        this.principalIDs = principalIDs;
    }

    public void clear() {
        this.personInfo.setName(null);
        this.personInfo.setTypeKey(null);
        this.clearTermValues();
    }

    public void clearTermValues() {
        this.termInfo = null;
        this.termCode = null;
        this.termName = null;
        this.clearCourseRegistrationValues();
    }

    public void clearCourseRegistrationValues() {
        this.resetPendingCourseValues();
        this.registeredCourses = new ArrayList<RegistrationCourse>();
        this.waitlistedCourses = new ArrayList<RegistrationCourse>();
        this.registrationIssues = new ArrayList<RegistrationIssue>();
    }

    public void resetPendingCourseValues(){
        this.pendingCourses = new ArrayList<RegistrationCourse>();
        this.pendingCourses.add(new RegistrationCourse());
    }
}
