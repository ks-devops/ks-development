-- update kew app doc id until we get the change in the master db
ALTER TABLE KREW_DOC_HDR_T MODIFY (APP_DOC_ID VARCHAR2(255));

-- clean up ksb registry table
DELETE FROM KRSB_SVC_DEF_T;

-- create namespaces for lookups
insert into KRNS_NMSPC_T (NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
values ('KS-ORG', 'F102F3FA08CF45CFAA404FBB89D831AA', '1', 'Kuali Student Organization', 'Y', null);
insert into KRNS_NMSPC_T (NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
values ('KS-SYS', 'G102F3FA08CF45CFAA404FBB89D831AA', '1', 'Kuali Student System', 'Y', null);

-- insert kim responsibility for 'Resolve Exception' responsibility template
INSERT INTO KRIM_RSP_T (RSP_ID,NMSPC_CD,NM,ACTV_IND,RSP_TMPL_ID,VER_NBR,DESC_TXT,OBJ_ID) 
  VALUES ('1','KS-SYS','Resolve Exception','Y','2',0,'Responsibility for Kuali Student Exception Routing','5ADFE1V2441D6320E04AAAA189D85169');
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('1','KualiStudentDocument','13','54','5G4F09744G28EF33E0404F8189AAAF24','1',1);
INSERT INTO KRIM_ROLE_RSP_T (ACTV_IND,OBJ_ID,RSP_ID,ROLE_ID,ROLE_RSP_ID,VER_NBR) 
  VALUES ('Y','BC27A267EF607417E0404F8189DAA0A9','1','63','1',1);
INSERT INTO KRIM_ROLE_RSP_ACTN_T (ROLE_RSP_ACTN_ID,OBJ_ID,VER_NBR,ACTN_TYP_CD,ACTN_PLCY_CD,ROLE_MBR_ID,ROLE_RSP_ID,FRC_ACTN) 
  VALUES ('1','A102F3FA08CF45CFAA404FBB89D831AA',1,'A','F','*','1','Y');

-- workflow module kim permissions
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5A4F0974494BEAA3E0404F8189D84F24','20','3',1);
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F0974494CEF33E04AAF8189D84F24','21','4',1);
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F0974494DEF33E0404F8189D8AA24','22','15',1);
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1E','3','13','74','20','KualiStudentDocument');
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1F','3','13','75','21','KualiStudentDocument');
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1G','3','13','76','22','KualiStudentDocument');
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AB','20','63','750',1);
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AC','21','63','751',1);
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AD','22','63','752',1);

-- rice system module kim permissions
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F0974494BEF33E0404XX189D8AA24','23','10',1);
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1H','3','13','77','23','KualiStudentDocument');
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AA','23','63','753',1);

-- kim module kim permissions
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F097X494BEF33E0404XX189D8AA24','24','35',1);
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F09XX494BEF33E0404XX189D8AA24','25','36',1);
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F09744XXBEF33E0404XX189D8AA24','26','37',1);
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KS-SYS','5B4F0B74494BEF33XX404XX189D8AA24','27','38',1);
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1D','20','4','70','24','KS*');
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1C','20','4','71','25','KS*');
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1B','20','4','72','26','KS*');
INSERT INTO KRIM_PERM_ATTR_DATA_T (VER_NBR,OBJ_ID,KIM_TYP_ID,KIM_ATTR_DEFN_ID,ATTR_DATA_ID,PERM_ID,ATTR_VAL)
   VALUES (1,'5C7D9976406BAA02E0404F8189D86F1A','20','4','73','27','KS*');
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AE','24','63','754',1);
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AF','25','63','755',1);
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AG','26','63','756',1);
INSERT INTO KRIM_ROLE_PERM_T (ACTV_IND,OBJ_ID,PERM_ID,ROLE_ID,ROLE_PERM_ID,VER_NBR)
  VALUES ('Y','5C27A267EF5C7417E0404F8189D830AH','27','63','757',1);

-- insert kim type for 'Department'
INSERT INTO KRIM_ATTR_DEFN_T (ACTV_IND,APPL_URL,CMPNT_NM,KIM_ATTR_DEFN_ID,NM,LBL,NMSPC_CD,OBJ_ID,VER_NBR) 
  VALUES ('Y','${application.url}','org.kuali.rice.student.bo.KualiStudentKimAttributes','100','department','Department','KS-ORG','5ADAA8B6D4AA7954E0404F8189D85002',1);
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR) 
  VALUES ('Y','100','Department Type','KS-ORG','5ADF18B6ACACA954E040AA8189D85002','kimRoleTypeService',1);
INSERT INTO KRIM_TYP_ATTR_T (ACTV_IND,KIM_ATTR_DEFN_ID,KIM_TYP_ATTR_ID,KIM_TYP_ID,OBJ_ID,SORT_CD,VER_NBR) 
  VALUES ('Y','100','200','100','5C7D9976406BAA02E0404F8189D86F11','a',1);

-- insert kim role for 'Department' responsibility to use
INSERT INTO KRIM_ROLE_T (ACTV_IND,KIM_TYP_ID,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR) 
  VALUES ('Y','100','KS-ORG','6102F3FA08CF45CFAA404F8189D831AA','900','Department Reviewer',1);

-- insert kim role members into 'Department Reviewer' role
INSERT INTO KRIM_ROLE_MBR_T (MBR_ID,MBR_TYP_CD,OBJ_ID,ROLE_ID,ROLE_MBR_ID,VER_NBR) 
  VALUES ('user1','P','5B4AA21E43857717E0404F8189D821F7','900','1290',1);
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T (VER_NBR,OBJ_ID,ATTR_DATA_ID,KIM_TYP_ID,ROLE_MBR_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
  VALUES (1,'5B4AA21E43857717E0404AA189DAA1F7','5','100','1290','100','PresidentsOffice');
INSERT INTO KRIM_ROLE_MBR_T (MBR_ID,MBR_TYP_CD,OBJ_ID,ROLE_ID,ROLE_MBR_ID,VER_NBR) 
  VALUES ('user2','P','5BAA421E43857717E0404F8189D821F7','900','1291',1);
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T (VER_NBR,OBJ_ID,ATTR_DATA_ID,KIM_TYP_ID,ROLE_MBR_ID,KIM_ATTR_DEFN_ID,ATTR_VAL) 
  VALUES (1,'5B4AA21E4385AA17E0404AA189DAA1F7','6','100','1291','100','AlumniAssociation');

-- insert kim responsibility for 'Department' route node
INSERT INTO KRIM_RSP_T (RSP_ID,NMSPC_CD,NM,ACTV_IND,RSP_TMPL_ID,VER_NBR,DESC_TXT,OBJ_ID) 
  VALUES ('2','KS-ORG','Review','Y','1',0,'Responsibility for Department Review','5ADFE172441D6320E04AAAA189D85169');
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('2','CluDocument','13','7','5B4F09744A28EF33E0404F8189AAAF24','2',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('3','Department Review','16','7','5BAA09744A28EF33E0404F8189AAAF24','2',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('4','false','41','7','5B4F097AAA28EF33E0404F8189AAAF24','2',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('5','false','40','7','5B4F09744A28AA33E0404F8189AAAF24','2',1);
INSERT INTO KRIM_ROLE_RSP_T (ACTV_IND,OBJ_ID,RSP_ID,ROLE_ID,ROLE_RSP_ID,VER_NBR) 
  VALUES ('Y','5C27A267EF607417E0404F8189DAA0A9','2','900','4',1);
INSERT INTO KRIM_ROLE_RSP_ACTN_T (ROLE_RSP_ACTN_ID,OBJ_ID,VER_NBR,ACTN_TYP_CD,ACTN_PLCY_CD,ROLE_MBR_ID,ROLE_RSP_ID,FRC_ACTN) 
  VALUES ('3','6102F3FA08CF45CFAA404FBB89D831AA',1,'A','F','*','4','Y');

-- insert kim type for 'College'
INSERT INTO KRIM_ATTR_DEFN_T (ACTV_IND,APPL_URL,CMPNT_NM,KIM_ATTR_DEFN_ID,NM,LBL,NMSPC_CD,OBJ_ID,VER_NBR) 
  VALUES ('Y','${application.url}','org.kuali.rice.student.bo.KualiStudentKimAttributes','101','college','College','KS-CLG','5ADCA8B6D4AA7954E0404F8189D85002',1);
INSERT INTO KRIM_TYP_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,SRVC_NM,VER_NBR) 
  VALUES ('Y','101','College Type','KS-CLG','5ADF18B6ACACA95CE040AA8189D85002','kimRoleTypeService',1);
INSERT INTO KRIM_TYP_ATTR_T (ACTV_IND,KIM_ATTR_DEFN_ID,KIM_TYP_ATTR_ID,KIM_TYP_ID,OBJ_ID,SORT_CD,VER_NBR) 
  VALUES ('Y','101','201','101','5C7D9976C06BAA02E0404F8189D86F11','a',1);

-- insert kim role for 'College' responsibility to use
INSERT INTO KRIM_ROLE_T (ACTV_IND,KIM_TYP_ID,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR) 
  VALUES ('Y','101','KS-CLG','6102F3RA08CF45CFAA404F8189D831AA','901','College Reviewer',1);

-- insert kim responsibility for 'College' route node
INSERT INTO KRIM_RSP_T (RSP_ID,NMSPC_CD,NM,ACTV_IND,RSP_TMPL_ID,VER_NBR,DESC_TXT,OBJ_ID) 
  VALUES ('3','KS-CLG','Review','Y','1',0,'Responsibility for College Review','5ADFE1C2441D6320E04AAAA189D85169');
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('6','CluDocument','13','7','5B4F0974GG28EF33E0404F8189AAAF24','3',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('7','College Review','16','7','5BAG09744GG8EF33E0404F8189AAAF24','3',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('8','false','41','7','5B4F097AAA28EF33E0404F81GGAAAF24','3',1);
INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID,ATTR_VAL,KIM_ATTR_DEFN_ID,KIM_TYP_ID,OBJ_ID,RSP_ID,VER_NBR) 
  VALUES ('9','false','40','7','5B4F09744A28GA33E0404F818GGGAF24','3',1);
INSERT INTO KRIM_ROLE_RSP_T (ACTV_IND,OBJ_ID,RSP_ID,ROLE_ID,ROLE_RSP_ID,VER_NBR) 
  VALUES ('Y','5C27A267EF607417E0404FG189DAA0A9','2','901','5',1);
INSERT INTO KRIM_ROLE_RSP_ACTN_T (ROLE_RSP_ACTN_ID,OBJ_ID,VER_NBR,ACTN_TYP_CD,ACTN_PLCY_CD,ROLE_MBR_ID,ROLE_RSP_ID,FRC_ACTN) 
  VALUES ('4','6102F3FA08CF45GFAA404FBB89D831AA',1,'A','F','*','5','Y');

