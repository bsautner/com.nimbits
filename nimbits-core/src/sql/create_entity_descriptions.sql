create schema if not exists nimbits_schema;
grant all privileges on nimbits_schema.* to root@localhost;

drop table if exists nimbits_schema.ENTITY;
drop table if exists nimbits_schema.SEARCH_LOG;
drop table if exists nimbits_schema.SEQUENCE;
drop table if exists nimbits_schema.sequence;

CREATE TABLE SEQUENCE (
SEQ_COUNT INT NOT NULL,
SEQ_NAME varchar(200) not null
);
INSERT INTO SEQUENCE VALUES (0, "SEQ_GEN_TABLE");

create table nimbits_schema.SEARCH_LOG (
  ID_SEARCH_LOG INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  SEARCH_TEXT varchar(200) not null,
  SEARCH_COUNT INT NOT NULL default 1,
  TS timestamp not null
 ) ENGINE=MyISAM;


create unique index ID_SEARCH_LOG_UNIQUE on nimbits_schema.SEARCH_LOG  (ID_SEARCH_LOG);
create unique index SEARCH_TEXT_UNIQUE on nimbits_schema.SEARCH_LOG  (SEARCH_TEXT);



create table nimbits_schema.ENTITY (
--  // FK_INSTANCE INT NOT NULL,
  ID_ENTITY INT NOT NULL AUTO_INCREMENT,
  UUID varchar(100) not null,
  ENTITY_NAME varchar(200) not null,
  ENTITY_DESC TEXT,
  ENTITY_TYPE INT NOT NULL,
  INSTANCE_URL varchar(200) not null,
  TS timestamp not null,
  ACTIVE BOOL default 1,
  FULLTEXT(ENTITY_NAME, ENTITY_DESC),
 PRIMARY KEY (ID_ENTITY)
) ENGINE=MyISAM;



create unique index ID_ENTITY_UNIQUE on nimbits_schema.ENTITY (ID_ENTITY);
create unique index UUID_UNIQUE on nimbits_schema.ENTITY (UUID);
--create fulltext index entity_ft_idx on nimbits_schema.ENTITY (ENTITY_NAME, ENTITY_DESC);

-- insert into ENTITY values (0, 231232, "ben", "test", 1, "http://test", null, 1);
-- insert into ENTITY values (0, 23123121, "test1", "test", 1, "http://test", null, 1);
-- insert into ENTITY values (0, 23121322, "test2", "description", 1, "http://test", null, 1);
-- insert into ENTITY values (0, 23112323, "test3", "description", 1, "http://test", null, 1);
-- insert into ENTITY values (0, 23112324, "test3", "description", 1, "http://test", null, 1);