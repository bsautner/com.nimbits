create schema if not exists nimbits_schema;
grant all privileges on nimbits_schema.* to root@localhost;

drop table if exists nimbits_schema.ENTITY;
drop table if exists nimbits_schema.INSTANCES;
drop table if exists nimbits_schema.SEARCH_LOG;


create table nimbits_schema.SEARCH_LOG (
  ID_SEARCH_LOG INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  SEARCH_TEXT varchar(200) not null,
  SEARCH_COUNT INT NOT NULL default 1,
  TS timestamp not null
 ) ENGINE=MyISAM;


create unique index ID_SEARCH_LOG_UNIQUE on nimbits_schema.SEARCH_LOG  (ID_SEARCH_LOG);
create unique index SEARCH_TEXT_UNIQUE on nimbits_schema.SEARCH_LOG  (SEARCH_TEXT);



create table nimbits_schema.INSTANCES (
 ID_INSTANCE INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 BASE_URL varchar(45) not null,
 OWNER_EMAIL varchar(45),
 CODE_VERSION varchar(10),
 ACTIVE BOOL default 1,
 TS timestamp not null

) ENGINE=MyISAM;

create unique index ID_INSTANCES_UNIQUE on nimbits_schema.INSTANCES (ID_INSTANCE);
create unique index base_url_instances_UNIQUE on nimbits_schema.INSTANCES (BASE_URL);

create table nimbits_schema.ENTITY (
  FK_INSTANCE INT NOT NULL,
  ID_ENTITY INT NOT NULL AUTO_INCREMENT,
  UUID varchar(100) not null,
  ENTITY_NAME varchar(200) not null,
  ENTITY_DESC TEXT,
  ENTITY_TYPE INT NOT NULL,
  TS timestamp not null,
  ACTIVE BOOL default 1,
  FULLTEXT(ENTITY_NAME, ENTITY_DESC),
  INDEX par_ind (FK_INSTANCE),
  FOREIGN KEY (FK_INSTANCE) REFERENCES INSTANCES(id_instance) ON DELETE CASCADE,
 PRIMARY KEY (FK_INSTANCE, ID_ENTITY)
) ENGINE=MyISAM;



create unique index ID_ENTITY_UNIQUE on nimbits_schema.ENTITY (ID_ENTITY);
create unique index UUID_UNIQUE on nimbits_schema.ENTITY (UUID);