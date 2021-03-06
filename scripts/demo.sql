SHOW TOPICS;
SHOW QUERIES;
PRINT 'CLICKS';

CREATE TABLE FIGHTERS (id STRING , name STRING) WITH ( KAFKA_TOPIC = 'FIGHTERS', VALUE_FORMAT = 'JSON', KEY = 'id');

CREATE STREAM PLAYERS (ts LONG, character STRING, game STRING, human BOOLEAN, player INT) WITH ( KAFKA_TOPIC = 'CLICKS', VALUE_FORMAT = 'JSON', TIMESTAMP = 'ts');

SHOW TABLES;
SHOW STREAMS;
SHOW QUERIES;

DESCRIBE PLAYERS;
DESCRIBE EXTENDED PLAYERS;

SHOW STREAMS;


SELECT game, human, character FROM PLAYERS WHERE human = true AND game = 'StreetFighter';

SELECT game, human, character, name FROM PLAYERS LEFT JOIN FIGHTERS ON character = id WHERE human = true AND game = 'StreetFighter';

CREATE STREAM HUMANPLAYERS AS SELECT game, human, character, name FROM PLAYERS LEFT JOIN FIGHTERS ON character = id WHERE human = true AND game = 'StreetFighter';

SHOW STREAMS;
SHOW QUERIES;
SHOW PROPERTIES;

DESCRIBE EXTENDED HUMANPLAYERS;

SET 'commit.interval.ms' = '5000';

CREATE TABLE PLAYERSCHOICE AS SELECT name, count(*) AS SCORE FROM HUMANPLAYERS WINDOW TUMBLING (size 5 seconds) GROUP BY name;

SHOW TABLES;
SHOW QUERIES;

SELECT * FROM PLAYERSCHOICE;

DESCRIBE EXTENDED PLAYERSCHOICE;

SELECT TIMESTAMPTOSTRING(ROWTIME, 'yyyy-MM-dd HH:mm:ss'), score, name FROM PLAYERSCHOICE;

EXPLAIN CTAS_PLAYERSCHOICE;
