# Домашнее задание к занятию «2.1 Системы хранения данных (СУБД)»

В качестве результата пришлите ответы на вопросы в личном кабинете студента на сайте [netology.ru](https://netology.ru).

## Предисловие

Данные ДЗ будут представлять собой лабораторные работы, в рамках которой вы по инструкциям выполните определённые шаги.

## Задание PostgreSQL Authentication

В рамках данного задания мы посмотрим, как настроена по умолчанию аутентификация в Docker образе PostgreSQL v12.

В качестве целевой конфигурации используйте файл `docker-compose.yml` следующего содержания:

```yaml
version: '3.7'
services:
  postgres:
    image: postgres:12
    ports:
      - 5432:5432
    environment:
      - POSTGRES_DB=db
      - POSTGRES_USER=app
      - POSTGRES_PASSWORD=pass
```

### Порядок выполнения

Изучите следующие разделы документации, касающиеся [аутентификации пользователей PostgreSQL v12](https://postgrespro.ru/docs/postgrespro/12/client-authentication):
1. [Файл pg_hba.conf](https://postgrespro.ru/docs/postgrespro/12/auth-pg-hba-conf)
1. [Аутентификация Trust](https://postgrespro.ru/docs/postgrespro/12/auth-trust)
1. [Аутентификация Password](https://postgrespro.ru/docs/postgrespro/12/auth-password)

**Важно**: все команды необходимо выполнять в каталоге, в котором расположен ваш файл `docker-compose.yml`.
   
Далее воспользуйтесь следующей информацией для запуска контейнера и подключения к нему:
1. Запустите контейнер командой `docker-compose up`
1. Для получения shell (bash) в контейнер используйте команду `docker-compose exec postgres /bin/bash`
1. Для получения доступа к psql (оболочке выполнения запросов) используйте команду `docker-compose exec postgres psql -U app -d db` (от имени пользователя app подключаемся к базе db)

#### Часть 1. Настройки по умолчанию

Изучите файл `/var/lib/postgresql/data/pg_hba.conf` и ответьте на следующе вопросы (для этого используйте команду получения shell):
1. Какие методы аутентификации используются для подключения по TCP/IP с адресов 127.0.0.1/32 и ::1/128?
1. Какие методы аутентификации используются для подключения по TCP/IP со всех остальных адресов, кроме указанных в предыдущем пункте по протоколу?

Изучите настройки ролей, хранящихся в БД и ответьте на вопросы:
1. Верно ли следующее утверждение: пароль роли `app` хранится в виде `функция_хеширования password` (пароль хранится в поле `rolpassword`)? Если не верно, то приведите описание алгоритма, который используется для хранения хеша.
1. Какое значение (`t` будет означать `да`, `f` - нет) имеют поля `rolsuper`, `rolcreaterole`, `rolcreatedb`, `rolbypassrls` с указанием назначения данных столбцов (см. https://postgrespro.ru/docs/postgresql/12/catalog-pg-authid)

Для этого используйте команду получения psql, в котором выполните команду `select * from pg_authid;`:
```postgresql
db=# select * from pg_authid;
 oid  |          rolname          | rolsuper | rolinherit | rolcreaterole | rolcreatedb | rolcanlogin | rolreplication | rolbypassrls | rolconnlimit |             rolpassword             | rolvaliduntil 
------+---------------------------+----------+------------+---------------+-------------+-------------+----------------+--------------+--------------+-------------------------------------+---------------
 3373 | pg_monitor                | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 3374 | pg_read_all_settings      | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 3375 | pg_read_all_stats         | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 3377 | pg_stat_scan_tables       | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 4569 | pg_read_server_files      | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 4570 | pg_write_server_files     | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 4571 | pg_execute_server_program | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
 4200 | pg_signal_backend         | f        | t          | f             | f           | f           | f              | f            |           -1 |                                     | 
   xx | app                       | x        | x          | x             | x           | x           | x              | x            |            x | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | xxxxxxxxxxxxx
(9 rows)
```

#### Часть 2. Изменение метода аутентификации

1. Остановите контейнер, нажав `Ctrl + C` в консоли, в которой у вас была запущена команда `docker-compose up`
1. Изучите [документацию на образ Docker](https://hub.docker.com/_/postgres) и измените метод аутентификации на `reject` (для этого вам понадобится отредактировать `docker-compose.yml`)
1. Удалите существующий контейнер, запустив команду `docker-compose rm` 
1. Запустите новый контейнер, выполнив команду `docker-compose up`
1. Подключитесь с помощью `psql` и выполните команду `select * from pg_authid;`

Ответьте на вопрос: почему значения полей `rolcanlogin` и `rolpassword` для роли `app` не изменились и вы по-прежнему можете подключиться с помощью `psql` без указания пароля, хотя в `pg_hba.conf` для `host all all all` указано `reject`?

<details>
<summary>Подсказка</summary>

Подумайте о том, что:
1. Вы запускаете `psql`, который расположен внутри контейнера
1. `pg_hba.conf` обрабатывается сверху вниз до первого совпадения
</details>

### Результаты выполнения

Пришлите в личном кабинете студента ответы на указанные в разделе **Выполнение** вопросы:
1. Какие методы аутентификации используются для подключения по TCP/IP с адресов 127.0.0.1/32 и ::1/128?
1. Какие методы аутентификации используются для подключения по TCP/IP со всех остальных адресов, кроме указанных в предыдущем пункте по протоколу?
1. Верно ли следующее утверждение: пароль роли `app` хранится в виде `функция_хеширования password` (пароль хранится в поле `rolpassword`)? Если не верно, то приведите описание алгоритма, который используется для хранения хеша.
1. Какое значение (`t` будет означать `да`, `f` - нет) имеют поля `rolsuper`, `rolcreaterole`, `rolcreatedb`, `rolbypassrls` с указанием назначения данных столбцов (см. https://postgrespro.ru/docs/postgresql/12/catalog-pg-authid)
1. Почему значения полей `rolcanlogin` и `rolpassword` для роли `app` не изменились и вы по-прежнему можете подключиться с помощью `psql` без указания пароля, хотя в `pg_hba.conf` для `host all all all` указано `reject`?

## Задание PostgreSQL CIS Benchmarks

Изучите [CIS Benchmarks](https://www.cisecurity.org/cis-benchmarks/) на СУБД PostgreSQL v12, а именно `Ensure login via "host" TCP/IP Socket is configured correctly`.

Пришлите в личном кабинете студента ответы на следующие вопросы:
1. Какие методы **не рекомендуется** использовать для удалённых подключений?
1. Какие методы **рекомендуется** использовать для удалённых подключений?

## Задание PostgreSQL ПРД

В рамках данного задания мы изучим механизмы управления пользователями и ПРД, реализованные в рамках СУБД PostgreSQL.

В качестве целевой конфигурации используйте файл `docker-compose.yml` следующего содержания:

```yaml
version: '3.7'
services:
  postgres:
    image: postgres:12
    ports:
      - 5432:5432
    environment:
      - POSTGRES_DB=db
      - POSTGRES_USER=app
      - POSTGRES_PASSWORD=pass
```

### Порядок выполнения

Изучите следующие разделы документации, касающиеся управлением пользователями и ПРД:
1. [Роли](https://postgrespro.ru/docs/postgresql/12/user-manag)
1. [Система прав](https://postgrespro.ru/docs/postgresql/12/ddl-priv)

**Важно**: все команды необходимо выполнять в каталоге, в котором расположен ваш файл `docker-compose.yml`.

Далее воспользуйтесь следующей информацией для запуска контейнера и подключения к нему:
1. Запустите контейнер командой `docker-compose up`
1. Для получения shell (bash) в контейнер используйте команду `docker-compose exec postgres /bin/bash`
1. Для получения доступа к psql (оболочке выполнения запросов) используйте команду `docker-compose exec postgres psql -U app -d db` (от имени пользователя app подключаемся к базе db)

PostgreSQL хранит информацию о ролях и ПРД в системной базе данных. Далее мы с вами будем использовать некоторые ключевые слова SQL:
* `CREATE` - создание
* `DROP` - изменение
* `SELECT` - выборка данных
* `INSERT` - вставка данных
* `DELETE` - удаление данных
* `GRANT` - предоставление прав
* `REVOKE` - отзыв прав

Здесь стоит сразу оговориться, что в простейшем случае есть разделение на "структуры" (вроде БД или таблицы) и данные (вроде строк в таблице). БД и таблицы создаются/удаляются с помощью запросов `CREATE`/`DROP`, а вот данные можно вставлять/удалять с помощью запросов `INSERT`/`DELETE`.

Но поскольку сама СУБД хранит информацию о "структурах" в системных БД, то запросы типа `CREATE`/`DROP` находят своё отражение в виде строк в таблицах системных БД.

#### Часть 1. Подготовка данных

В оболочке psql выполните следующие команды:
1\. `SELECT * FROM pg_roles;` - отображает существующие в системе роли:

```postgresql
db=# SELECT * FROM pg_roles;
          rolname          | rolsuper | rolinherit | rolcreaterole | rolcreatedb | rolcanlogin | rolreplication | rolconnlimit | rolpassword | rolvaliduntil | rolbypassrls | rolconfig | oid  
---------------------------+----------+------------+---------------+-------------+-------------+----------------+--------------+-------------+---------------+--------------+-----------+------
 pg_signal_backend         | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 4200
 pg_read_server_files      | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 4569
 app                       | t        | t          | t             | t           | t           | t              |           -1 | ********    |               | t            |           |   10
 pg_write_server_files     | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 4570
 pg_execute_server_program | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 4571
 pg_read_all_stats         | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 3375
 pg_monitor                | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 3373
 pg_read_all_settings      | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 3374
 pg_stat_scan_tables       | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           | 3377
(9 rows)
```

2\. Создадим новую роль `reader` с правом входа `CREATE ROLE reader LOGIN PASSWORD 'secret';`:

```postgresql

db=# CREATE ROLE reader LOGIN PASSWORD 'secret';
CREATE ROLE
db=# SELECT * FROM pg_roles;
          rolname          | rolsuper | rolinherit | rolcreaterole | rolcreatedb | rolcanlogin | rolreplication | rolconnlimit | rolpassword | rolvaliduntil | rolbypassrls | rolconfig |  oid  
---------------------------+----------+------------+---------------+-------------+-------------+----------------+--------------+-------------+---------------+--------------+-----------+-------
 pg_signal_backend         | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  4200
 pg_read_server_files      | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  4569
 app                       | t        | t          | t             | t           | t           | t              |           -1 | ********    |               | t            |           |    10
 pg_write_server_files     | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  4570
 pg_execute_server_program | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  4571
 pg_read_all_stats         | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  3375
 pg_monitor                | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  3373
 pg_read_all_settings      | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  3374
 pg_stat_scan_tables       | f        | t          | f             | f           | f           | f              |           -1 | ********    |               | f            |           |  3377
 reader                    | f        | t          | f             | f           | t           | f              |           -1 | ********    |               | f            |           | 16385
(10 rows)
```

3\. Проверим существующие базы данных и их владельцев `SELECT * FROM pg_database`:

```postgresql
db=# SELECT * FROM pg_database;
  oid  |  datname  | datdba | encoding | datcollate |  datctype  | datistemplate | datallowconn | datconnlimit | datlastsysoid | datfrozenxid | datminmxid | dattablespace |        datacl        
-------+-----------+--------+----------+------------+------------+---------------+--------------+--------------+---------------+--------------+------------+---------------+----------------------
 13408 | postgres  |     10 |        6 | en_US.utf8 | en_US.utf8 | f             | t            |           -1 |         13407 |          480 |          1 |          1663 | 
 16384 | db        |     10 |        6 | en_US.utf8 | en_US.utf8 | f             | t            |           -1 |         13407 |          480 |          1 |          1663 | 
     1 | template1 |     10 |        6 | en_US.utf8 | en_US.utf8 | t             | t            |           -1 |         13407 |          480 |          1 |          1663 | {=c/app,app=CTc/app}
 13407 | template0 |     10 |        6 | en_US.utf8 | en_US.utf8 | t             | f            |           -1 |         13407 |          480 |          1 |          1663 | {=c/app,app=CTc/app}
(4 rows)
```

Поле `datdba` содержит `oid` роли-владельца БД (в нашем случае - `app`)

<details>
<summary>Более удобные команды оболочки psql</summary>

Оболочка psql содержит встроенные команды, содержащие "упрощённый" и более удобный вывод:
```postgresql
db=# \l
List of databases
   Name    | Owner | Encoding |  Collate   |   Ctype    | Access privileges 
-----------+-------+----------+------------+------------+-------------------
 db        | app   | UTF8     | en_US.utf8 | en_US.utf8 | 
 postgres  | app   | UTF8     | en_US.utf8 | en_US.utf8 | 
 template0 | app   | UTF8     | en_US.utf8 | en_US.utf8 | =c/app           +
           |       |          |            |            | app=CTc/app
 template1 | app   | UTF8     | en_US.utf8 | en_US.utf8 | =c/app           +
           |       |          |            |            | app=CTc/app
(4 rows)

db=# \du
List of roles
 Role name |                         Attributes                         | Member of 
-----------+------------------------------------------------------------+-----------
 app       | Superuser, Create role, Create DB, Replication, Bypass RLS | {}
 reader    |                                                            | {}
```
</details>

4\. Создадим тестовую базу данных `test` `CREATE DATABASE test;`:

```postgresql
db=# CREATE DATABASE test;
CREATE DATABASE

db=# \l
List of databases
   Name    | Owner | Encoding |  Collate   |   Ctype    | Access privileges 
-----------+-------+----------+------------+------------+-------------------
 db        | app   | UTF8     | en_US.utf8 | en_US.utf8 | 
 postgres  | app   | UTF8     | en_US.utf8 | en_US.utf8 | 
 template0 | app   | UTF8     | en_US.utf8 | en_US.utf8 | =c/app           +
           |       |          |            |            | app=CTc/app
 template1 | app   | UTF8     | en_US.utf8 | en_US.utf8 | =c/app           +
           |       |          |            |            | app=CTc/app
 test      | app   | UTF8     | en_US.utf8 | en_US.utf8 | 
(5 rows)
```

5\. Подключимся к созданной БД для создания таблиц в ней:
```postgresql
db=# \c test
You are now connected to database "test" as user "app".
```

После этого строка приглашения сменится с `db=#` на `test=#` (т.е. отображается БД, к которой мы подключены в данный момент)

6\. Создадим таблицу `records` в нашей тестовой БД `CREATE TABLE records (value TEXT, status TEXT, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP);`:
```postgresql
test=# CREATE TABLE records (value TEXT, status TEXT, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE
test=# \dt
        List of relations
 Schema |  Name   | Type  | Owner 
--------+---------+-------+-------
 public | records | table | app
(1 row)
```

Команда `\dt` показывает существующие в текущей БД (к которой мы подключены) таблицы.

С помощью SQL это можно сделать следующим образом (будут выведены все таблицы):
```postgresql
test=# select * from pg_tables;
     schemaname     |        tablename        | tableowner | tablespace | hasindexes | hasrules | hastriggers | rowsecurity 
--------------------+-------------------------+------------+------------+------------+----------+-------------+-------------
 public             | records                 | app        |            | f          | f        | f           | f
 pg_catalog         | pg_statistic            | app        |            | t          | f        | f           | f
 ...                | ...                     | ...        |            | ...        | ...      | ...         | ...
```

7\. Вставим запись в таблицу `INSERT INTO records(value, status) VALUES ('transfer money from 55** **** 0001 to 42** **** 0002', 'success');`:

```postgresql
test=# INSERT INTO records(value, status) VALUES ('transfer money from 55** **** 0001 to 42** **** 0002', 'success');
INSERT 0 1
  
test=# SELECT * FROM records;                                                                                       
                        value                         | status  |          created           
------------------------------------------------------+---------+----------------------------
 transfer money from 55** **** 0001 to 42** **** 0002 | success | 2021-01-25 07:08:41.966631
(1 row)
```

8\. Предоставим права роли `reader` на чтение содержимого таблицы `records` `GRANT SELECT ON records TO reader`:
```postgresql
test=# GRANT SELECT ON records TO reader;
GRANT
```

9\. Посмотрим предоставленные права (команда `\dp`):
```postgresql
test-# \dp
                              Access privileges
 Schema |  Name   | Type  | Access privileges | Column privileges | Policies 
--------+---------+-------+-------------------+-------------------+----------
 public | records | table | app=arwdDxt/app  +|                   | 
        |         |       | reader=r/app      |                   | 
(1 row)
```

#### Часть 2. Проверка ПРД

**Важно**: откройте новую консоль (окно терминала) в каталоге с файлом `docker-compose.yml` и все команды выполняйте там:

1\. Получим psql для пользователя `reader`: `docker-compose exec postgres psql -U reader -d test` 

2\. Выполним запрос на чтение:
```postgresql
test=> SELECT * FROM records;
                        value                         | status  |          created           
------------------------------------------------------+---------+----------------------------
 transfer money from 55** **** 0001 to 42** **** 0002 | success | 2021-01-25 07:08:41.966631
(1 row)
```

3\. Удостоверимся, что остальные действия запрещены:
```postgresql
test=> DELETE FROM records;
ERROR:  ???
```

4\. Заберём у роли `reader` права на чтение (нужно выполнять в первой консоли от имени пользователя `app`):
```postgresql
test=# REVOKE SELECT ON records FROM reader;
REVOKE

test=# \dp
Access privileges
 Schema |  Name   | Type  | Access privileges | Column privileges | Policies 
--------+---------+-------+-------------------+-------------------+----------
 public | records | table | app=arwdDxt/app   |                   | 
(1 row)
```

5\. Выполним запрос на чтение аналогично п.2 (от имени пользователя `reader`):
```postgresql
test=> SELECT * FROM records;
ERROR:  ???
```

### Результаты выполнения

Пришлите в личном кабинете студента сообщение, которое отображается вместо `???` в результате выполнения запросов с недостаточными правами доступа: `ERROR:  ???`.

## Материалы для доп.ознакомления

К данным материалам мы вернёмся в процессе прохождения курса, но вы уже сейчас можете ознакомиться с ними:
1. [Row Level Security](https://postgrespro.ru/docs/postgresql/12/ddl-rowsecurity)
1. [Шифрование данных](https://postgrespro.ru/docs/postgresql/12/encryption-options)
