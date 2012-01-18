drop table if exists nimbits_schema.ENTITY_DESCRIPTIONS;
drop table if exists nimbits_schema.SERVERS;



create table nimbits_schema.SERVERS (
 ID_SERVER INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 BASE_URL varchar(45) not null,
 OWNER_EMAIL varchar(45),
 SERVER_VERSION varchar(10),
 ACTIVE BOOL default 1,
 TS timestamp not null

) ENGINE=MyISAM;

create unique index ID_SERVERS_UNIQUE on nimbits_schema.SERVERS (ID_SERVER);
create unique index base_url_servers_UNIQUE on nimbits_schema.SERVERS (BASE_URL);


create table nimbits_schema.ENTITY_DESCRIPTIONS (
  FK_SERVER INT NOT NULL,
  ID_ENTITY_DESCRIPTIONS INT NOT NULL AUTO_INCREMENT,
  UUID varchar(100) not null,
  ENTITY_NAME varchar(200) not null,
  ENTITY_DESC TEXT,
  ENTITY_TYPE INT NOT NULL,
  TS timestamp not null,
  ACTIVE BOOL default 1,
  FULLTEXT(ENTITY_NAME, ENTITY_DESC),
  INDEX par_ind (FK_SERVER),
  FOREIGN KEY (FK_SERVER) REFERENCES SERVERS(id_server) ON DELETE CASCADE,
 PRIMARY KEY (FK_SERVER, ID_ENTITY_DESCRIPTIONS)
) ENGINE=MyISAM;


create unique index ID_ENTITY_DESCRIPTIONS_UNIQUE on nimbits_schema.ENTITY_DESCRIPTIONS (ID_ENTITY_DESCRIPTIONS);
create unique index UUID_UNIQUE on nimbits_schema.ENTITY_DESCRIPTIONS (UUID);