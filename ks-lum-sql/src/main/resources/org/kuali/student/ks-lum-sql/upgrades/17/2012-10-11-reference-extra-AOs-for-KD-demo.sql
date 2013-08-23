UPDATE
    KSLU_CLU
SET
    KSLU_CLU.LUTYPE_ID='kuali.lu.type.activity.Lab'
WHERE
    KSLU_CLU.ID='067a873d-5308-4802-81bf-5b6502fa679a'
    /
UPDATE
    KSLU_CLU_IDENT
SET
    SHRT_NAME='LC/LB',
    LNG_NAME='Lecture/Lab'
WHERE
    ID='B219CCB82EBA4586BF4DBDCA031AE728'
    /

--Add new lecture format to clu
insert into KSLU_CLU_IDENT (ID, CD, DIVISION, LVL, LNG_NAME, ORG_ID, SHRT_NAME, ST, SUFX_CD, TYPE, VARTN, OBJ_ID, VER_NBR) values ('a0e7556e-49a4-4d40-90bd-8eb5b1ce7159', 'FormatShell', null, null, 'Lecture', null, 'LC', 'ACTIVE', null, 'kuali.lu.type.CreditCourseFormatShell.identifier', null, '96094f2b-2dbf-4d50-96ad-b53a60b54886', 56)
/
insert into KSLU_CLU (ID, CREATEID, CREATETIME, UPDATEID, UPDATETIME, VER_NBR, CURR_VER_END, CURR_VER_START, SEQ_NUM, VER_CMT, VER_IND_ID, VER_FROM_ID, CAN_CREATE_LUI, DEF_ENRL_EST, DEF_MAX_ENRL, EFF_DT, EXP_FIRST_ATP, EXPIR_DT, HAS_EARLY_DROP_DEDLN, CLU_INTSTY_QTY, CLU_INTSTY_TYPE, IS_ENRL, IS_HAZR_DISBLD_STU, LAST_ADMIT_ATP, LAST_ATP, NEXT_REVIEW_PRD, REF_URL, ST, ATP_DUR_TYP_KEY, TM_QUANTITY, STDY_SUBJ_AREA, ACCT_ID, RT_DESCR_ID, FEE_ID, LUTYPE_ID, OFFIC_CLU_ID, PRI_INSTR_ID, OBJ_ID) values ('5df4f20c-7d7c-4021-a8fc-708eb3c3fe45', null, TIMESTAMP '2012-10-12 23:47:52', null, TIMESTAMP '2012-05-09 23:47:52', 0, null, TIMESTAMP '2012-05-09 23:47:52', 1, null, '769f76ff-3ff1-4b65-8ac6-857599892f18', null, 0, 0, 0, null, null, null, 0, null, null, 0, 0, null, null, null, null, 'ACTIVE', null, null, null, null, null, null, 'kuali.lu.type.activity.Lecture', null, null, 'de4f6ce6-4eed-4d38-bb44-b3bfa52c9e0e')
/
insert into KSLU_CLU (ID, CREATEID, CREATETIME, UPDATEID, UPDATETIME, VER_NBR, CURR_VER_END, CURR_VER_START, SEQ_NUM, VER_CMT, VER_IND_ID, VER_FROM_ID, CAN_CREATE_LUI, DEF_ENRL_EST, DEF_MAX_ENRL, EFF_DT, EXP_FIRST_ATP, EXPIR_DT, HAS_EARLY_DROP_DEDLN, CLU_INTSTY_QTY, CLU_INTSTY_TYPE, IS_ENRL, IS_HAZR_DISBLD_STU, LAST_ADMIT_ATP, LAST_ATP, NEXT_REVIEW_PRD, REF_URL, ST, ATP_DUR_TYP_KEY, TM_QUANTITY, STDY_SUBJ_AREA, ACCT_ID, RT_DESCR_ID, FEE_ID, LUTYPE_ID, OFFIC_CLU_ID, PRI_INSTR_ID, OBJ_ID) values ('9dd0f450-7f93-4110-a340-f9f36f86269c', null, TIMESTAMP '2012-10-12 23:47:52', null, TIMESTAMP '2012-05-09 23:47:52', 0, null, TIMESTAMP '2012-05-09 23:47:52', 1, null, 'f0c06c51-765e-4ec3-b06d-a95e2e99db31', null, 0, 0, 0, null, null, null, 0, null, null, 0, 0, null, null, null, null, 'ACTIVE', null, null, null, null, null, null, 'kuali.lu.type.CreditCourseFormatShell', 'a0e7556e-49a4-4d40-90bd-8eb5b1ce7159', null, 'cc1a15ed-8f5f-4c15-83fe-9a0c6f73e54b')
/
insert into KSLU_CLUCLU_RELTN (ID, CREATEID, CREATETIME, UPDATEID, UPDATETIME, VER_NBR, CLU_RELTN_REQ, EFF_DT, EXPIR_DT, ST, CLU_ID, LU_RELTN_TYPE_ID, RELATED_CLU_ID, OBJ_ID) values ('7e917928-5343-419e-a4d5-b029e696edfa', null, TIMESTAMP '2012-05-09 23:47:52', null, TIMESTAMP '2012-05-09 23:47:52', 0, 1, null, null, 'ACTIVE', '9dd0f450-7f93-4110-a340-f9f36f86269c', 'luLuRelationType.contains', '5df4f20c-7d7c-4021-a8fc-708eb3c3fe45', '19e38c08-16e1-4f45-ba76-ba596919d20b')
/
insert into KSLU_CLUCLU_RELTN (ID, CREATEID, CREATETIME, UPDATEID, UPDATETIME, VER_NBR, CLU_RELTN_REQ, EFF_DT, EXPIR_DT, ST, CLU_ID, LU_RELTN_TYPE_ID, RELATED_CLU_ID, OBJ_ID) values ('f4f2254c-8113-47c7-9bff-3fe28e666f42', null, TIMESTAMP '2012-05-09 23:47:52', null, TIMESTAMP '2012-05-09 23:47:52', 0, 1, null, null, 'ACTIVE', '110bd7d7-3dc1-45e5-9ef9-bf858275fb04', 'luLuRelationType.hasCourseFormat', '9dd0f450-7f93-4110-a340-f9f36f86269c', 'd7ea1866-434a-424a-b708-20049be10e7b')
/
