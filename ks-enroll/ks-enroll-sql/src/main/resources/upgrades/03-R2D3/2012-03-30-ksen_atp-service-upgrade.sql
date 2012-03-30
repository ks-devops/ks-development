UPDATE KSEN_ATP SET CREATEID='admin' WHERE CREATEID IS NULL
/

ALTER TABLE KSEN_ATP MODIFY CREATEID VARCHAR2(255) NOT NULL
/

UPDATE KSEN_ATP SET CREATETIME=TO_DATE('1-1-1970 00:00:00','MM-DD-YYYY HH24:Mi:SS') WHERE CREATETIME IS NULL
/

ALTER TABLE KSEN_ATP MODIFY CREATETIME TIMESTAMP NOT NULL
/

ALTER TABLE KSEN_ATP MODIFY DESCR_PLAIN VARCHAR2(4000)
/

ALTER TABLE KSEN_ATP MODIFY VER_NBR NUMBER(19) NOT NULL
/

ALTER TABLE KSEN_ATP_ATTR RENAME COLUMN OWNER TO OWNER_ID
/

UPDATE KSEN_ATPATP_RELTN SET CREATEID='admin' WHERE CREATEID IS NULL
/

ALTER TABLE KSEN_ATPATP_RELTN MODIFY CREATEID VARCHAR2(255) NOT NULL
/

UPDATE KSEN_ATPATP_RELTN SET CREATETIME=TO_DATE('1-1-1970 00:00:00','MM-DD-YYYY HH24:Mi:SS') WHERE CREATETIME IS NULL
/

ALTER TABLE KSEN_ATPATP_RELTN MODIFY CREATETIME TIMESTAMP NOT NULL
/

ALTER TABLE KSEN_ATPATP_RELTN MODIFY VER_NBR NUMBER(19) NOT NULL
/

ALTER TABLE KSEN_ATPATP_RELTN_ATTR RENAME COLUMN OWNER TO OWNER_ID
/

UPDATE KSEN_ATPMSTONE_RELTN SET CREATEID='admin' WHERE CREATEID IS NULL
/

ALTER TABLE KSEN_ATPMSTONE_RELTN MODIFY CREATEID VARCHAR2(255) NOT NULL
/

UPDATE KSEN_ATPMSTONE_RELTN SET CREATETIME=TO_DATE('1-1-1970 00:00:00','MM-DD-YYYY HH24:Mi:SS') WHERE CREATETIME IS NULL
/

ALTER TABLE KSEN_ATPMSTONE_RELTN MODIFY CREATETIME TIMESTAMP NOT NULL
/

ALTER TABLE KSEN_ATPMSTONE_RELTN MODIFY VER_NBR NUMBER(19) NOT NULL
/

DROP TABLE KSEN_ATPMSTONE_RELTN_ATTR
/

DROP TABLE KSEN_ATPTYPE_ATTR
/

UPDATE KSEN_MSTONE SET CREATEID='admin' WHERE CREATEID IS NULL
/

ALTER TABLE KSEN_MSTONE MODIFY CREATEID VARCHAR2(255) NOT NULL
/

UPDATE KSEN_MSTONE SET CREATETIME=TO_DATE('1-1-1970 00:00:00','MM-DD-YYYY HH24:Mi:SS') WHERE CREATETIME IS NULL
/

ALTER TABLE KSEN_MSTONE MODIFY CREATETIME TIMESTAMP NOT NULL
/

ALTER TABLE KSEN_MSTONE MODIFY DESCR_PLAIN VARCHAR2(4000)
/

ALTER TABLE KSEN_MSTONE MODIFY VER_NBR NUMBER(19) NOT NULL
/

ALTER TABLE KSEN_MSTONE_ATTR RENAME COLUMN OWNER TO OWNER_ID
/
