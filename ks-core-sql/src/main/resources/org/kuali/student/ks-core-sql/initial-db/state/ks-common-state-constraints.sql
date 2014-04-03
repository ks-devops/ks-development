

ALTER TABLE KSEN_STATE
    ADD CONSTRAINT KSEN_STATE_FK1 FOREIGN KEY (LIFECYCLE_KEY)
    REFERENCES KSEN_STATE_LIFECYCLE (ID)
/


ALTER TABLE KSEN_STATE_ATTR
    ADD CONSTRAINT KSEN_STATE_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_STATE (ID)
/


ALTER TABLE KSEN_STATE_CHG
    ADD CONSTRAINT KSEN_STATE_CHG_FK1 FOREIGN KEY (FROM_STATE_ID)
    REFERENCES KSEN_STATE (ID)
/

ALTER TABLE KSEN_STATE_CHG
    ADD CONSTRAINT KSEN_STATE_CHG_IF2 FOREIGN KEY (TO_STATE_ID)
    REFERENCES KSEN_STATE (ID)
/


ALTER TABLE KSEN_STATE_CHG_ATTR
    ADD CONSTRAINT KSEN_STATE_CHG_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_STATE_CHG (ID)
/






ALTER TABLE KSEN_STATE_CNSTRNT_ROS
    ADD CONSTRAINT KSEN_STATE_CNSTRNT_ROS_FK2 FOREIGN KEY (REL_OBJ_STATE_ID)
    REFERENCES KSEN_STATE (ID)
/

ALTER TABLE KSEN_STATE_CNSTRNT_ROS
    ADD CONSTRAINT KSEN_STATE_CNSTRNT_ROS_FK1 FOREIGN KEY (STATE_CNSTRNT_ID)
    REFERENCES KSEN_STATE_CNSTRNT (ID)
/



ALTER TABLE KSEN_STATE_LIFECYCLE_ATTR
    ADD CONSTRAINT KSEN_STATE_LIFECYCLE_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_STATE_LIFECYCLE (ID)
/



ALTER TABLE KSEN_STATE_PROPAGT
    ADD CONSTRAINT KSEN_STATE_PROPAGT_FK1 FOREIGN KEY (TARGET_STATE_CHG_ID)
    REFERENCES KSEN_STATE_CHG (ID)
/


ALTER TABLE KSEN_STATE_PROPAGT_ATTR
    ADD CONSTRAINT KSEN_STATE_PROPAGT_ATTR_FK1 FOREIGN KEY (OWNER_ID)
    REFERENCES KSEN_STATE_PROPAGT (ID)
/


ALTER TABLE KSEN_STATE_PROPAGT_CNSTRNT
    ADD CONSTRAINT KSEN_STATE_PROPAGT_CNS_FK2 FOREIGN KEY (STATE_CNSTRNT_ID)
    REFERENCES KSEN_STATE_CNSTRNT (ID)
/

ALTER TABLE KSEN_STATE_PROPAGT_CNSTRNT
    ADD CONSTRAINT KSEN_STATE_PROPAGT_CNS_FK1 FOREIGN KEY (STATE_PROPAGT_ID)
    REFERENCES KSEN_STATE_PROPAGT (ID)
/
