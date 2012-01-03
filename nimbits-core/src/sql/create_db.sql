
-- drop table nimbits_schema.SERVERS;
-- drop table nimbits_schema.POINT_DESCRIPTIONS;


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


create table nimbits_schema.POINT_DESCRIPTIONS (
  FK_SERVER INT NOT NULL,
  ID_POINT INT NOT NULL AUTO_INCREMENT,
  UUID varchar(100) not null,
  POINT_NAME varchar(200) not null,
  POINT_DESC TEXT,
  TS timestamp not null,
  ACTIVE BOOL default 1,
  FULLTEXT(POINT_NAME, POINT_DESC),
  INDEX par_ind (FK_SERVER),
  FOREIGN KEY (FK_SERVER) REFERENCES SERVERS(id_server) ON DELETE CASCADE,
 PRIMARY KEY (FK_SERVER, ID_POINT)
) ENGINE=MyISAM;







