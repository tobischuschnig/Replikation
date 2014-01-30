Drop database replication;
create database replication;
use replication;

create table angestellte(
	anr		integer,
	name	varchar(255),
	primary key (anr)
	)ENGINE = INNODB;
	
insert into angestellte values(1,'Angestellter1');
insert into angestellte values(2,'Angestellter2');
insert into angestellte values(3,'Angestellter3');
insert into angestellte values(4,'Angestellter4');
insert into angestellte values(5,'Angestellter5');
insert into angestellte values(6,'Angestellter6');
insert into angestellte values(7,'Angestellter7');
insert into angestellte values(8,'Angestellter8');
insert into angestellte values(9,'Angestellter9');
insert into angestellte values(10,'Angestellter10');

create table produkte(
	pnr		integer,
	name	varchar(255),
	beschreibung	varchar(255),
	preis	integer,
	primary key(pnr)
	)ENGINE = INNODB;
	
insert into produkte values(1,'Produkt1','Produkt1 ist super',123);
insert into produkte values(2,'Produkt2','Produkt2 ist super',133);
insert into produkte values(3,'Produkt3','Produkt3 ist super',323);
insert into produkte values(4,'Produkt4','Produkt4 ist super',345);
insert into produkte values(5,'Produkt5','Produkt5 ist super',457);
insert into produkte values(6,'Produkt6','Produkt6 ist super',265);
insert into produkte values(7,'Produkt7','Produkt7 ist super',976);
insert into produkte values(8,'Produkt8','Produkt8 ist super',234);
insert into produkte values(9,'Produkt9','Produkt9 ist super',254);
insert into produkte values(10,'Produkt10','Produkt10 ist super',763);

create table kunden(
	knr		integer,
	name	varchar(255),
	anschrift	varchar(255),
	primary key(knr)
	)ENGINE = INNODB;
	
insert into kunden values(1,'Name1','Anschrift1');
insert into kunden values(2,'Name2','Anschrift2');
insert into kunden values(3,'Name3','Anschrift3');
insert into kunden values(4,'Name4','Anschrift4');
insert into kunden values(5,'Name5','Anschrift5');
insert into kunden values(6,'Name6','Anschrift6');
insert into kunden values(7,'Name7','Anschrift7');
insert into kunden values(8,'Name8','Anschrift8');
insert into kunden values(9,'Name9','Anschrift9');
insert into kunden values(10,'Name10','Anschrift10');
