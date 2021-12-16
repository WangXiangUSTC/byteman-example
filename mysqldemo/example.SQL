CREATE DATABASE IF NOT EXISTS chaos;
CREATE TABLE IF NOT EXISTS chaos.website (id int, name varchar(24), url varchar(24));
INSERT INTO chaos.website VALUES(1, "Chaos Mesh", "https://chaos-mesh.org");
INSERT INTO chaos.website VALUES(2, "GitHub", "https://github.com");
INSERT INTO chaos.website VALUES(2, "Google", "https://google.com");