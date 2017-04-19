CREATE TABLE IF NOT EXISTS "country" (
  "id" int(11) NOT NULL AUTO_INCREMENT,
  "name" varchar(50) NOT NULL,
  PRIMARY KEY ("id")
);


CREATE TABLE IF NOT EXISTS "specie" (
  "id" int(11) NOT NULL AUTO_INCREMENT,
  "name" varchar(50) NOT NULL,
  PRIMARY KEY ("id")
);


CREATE TABLE IF NOT EXISTS "inhabit" (
  "id" int(11) NOT NULL AUTO_INCREMENT,
  "country_id" int(11) NOT NULL,
  "specie_id" int(11) NOT NULL,
  "population" int(11) NOT NULL DEFAULT '0',
  "avarage_income" decimal(10,0) NOT NULL DEFAULT '0',
  PRIMARY KEY ("id"),
  UNIQUE KEY "country_id_specie_id" ("country_id","specie_id"),
  FOREIGN KEY ("country_id") REFERENCES "country" ("id"),
  FOREIGN KEY ("specie_id") REFERENCES "specie" ("id")
);
