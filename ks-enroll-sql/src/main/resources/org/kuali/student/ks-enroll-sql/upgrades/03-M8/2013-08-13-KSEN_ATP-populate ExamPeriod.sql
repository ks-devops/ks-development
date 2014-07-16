-- KSENROLL-8652
INSERT INTO KSEN_ATP (ATP_STATE,ATP_TYPE,CREATEID,CREATETIME,DESCR_FORMATTED,DESCR_PLAIN,END_DT,ID,NAME,OBJ_ID,START_DT,UPDATEID,UPDATETIME,VER_NBR)
  VALUES ('kuali.atp.state.Official','kuali.atp.type.ExamPeriod','batchjob',TO_DATE( '20130813101250', 'YYYYMMDDHH24MISS' ),'Fall 2012 Exam Period','Fall 2012 Exam Period',TO_DATE( '20121217000000', 'YYYYMMDDHH24MISS' ),'kuali.atp.ExamPeriod.2012Fall','ExamPeriod Fall 2012','d61523ab-7500-4a82-a3fd-a748f7bc6bfb',TO_DATE( '20121211000000', 'YYYYMMDDHH24MISS' ),'batchjob',TO_DATE( '20130813101250', 'YYYYMMDDHH24MISS' ),0)
/
INSERT INTO KSEN_ATPATP_RELTN (ID, OBJ_ID, ATP_TYPE, ATP_STATE, ATP_ID, RELATED_ATP_ID, EFF_DT, EXPIR_DT, VER_NBR, CREATETIME, CREATEID, UPDATETIME, UPDATEID)
  VALUES ('edfe4ba1-b215-4ed1-8a40-9d3eb5a1073a', 'ff1d161e-99e7-46ae-813d-ee925c092ec7', 'kuali.atp.atp.relation.associated.term2examperiod', 'kuali.atp.atp.relation.state.active', 'kuali.atp.2012Fall', 'kuali.atp.ExamPeriod.2012Fall', TIMESTAMP '2012-12-11 00:00:00', null, 0, TIMESTAMP '2012-08-13 05:12:50', 'batchjob', TIMESTAMP '2013-08-12 05:12:50', 'batchjob')
/