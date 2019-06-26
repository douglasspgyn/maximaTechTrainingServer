# Maxima Tech - Training Server

### Description

Simple API developed with [Ktor](https://ktor.io/) and persisting with [PostgreSQL](https://www.postgresql.org/), for a training at [Máxima Tech](https://maximatech.com.br/).

* Secured with:
    * [Basic Authentication](https://tools.ietf.org/html/rfc7617);
    * [Bearer Authentication](https://tools.ietf.org/html/rfc6750) ([JWT](https://tools.ietf.org/html/rfc7519));
    * [BCrypt](https://en.wikipedia.org/wiki/Bcrypt);

### Instalation

You need an IDE configured for [Kotlin](https://kotlinlang.org/) (like [IntelliJ IDEA](https://www.jetbrains.com/idea/)) and PostgreSQL.

First, let's create the database, just run that script:
```
CREATE OR REPLACE FUNCTION funtion_update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = now();
   return NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE public.users (
   id serial NOT NULL,
   "name" varchar(100) NOT NULL,
   email varchar(100) NOT NULL,
   picture varchar NULL,
   password varchar(100) NOT NULL,
   "role" varchar(50) NOT NULL DEFAULT 'user'::character varying,
   created_at timestamp NOT NULL DEFAULT now(),
   updated_at timestamp NOT NULL DEFAULT now(),
   deleted_at timestamp NULL,
   CONSTRAINT users_pk PRIMARY KEY (id),
   CONSTRAINT users_un UNIQUE (email)
)
WITH (
   OIDS=FALSE
) ;
CREATE UNIQUE INDEX users_email_idx ON public.users USING btree (email) ;

CREATE TRIGGER trigger_set_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE PROCEDURE funtion_update_updated_at();
```

After that you clone the repository and need to create a ```.env``` file inside the main directory.

File Content:
```
PORT=5000
JDBC_DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/maximatechtrainingdb?user=postgres&password=postgres
```
Update this sample with your infos.

Than create a *Run/Debug Configuration* on the IDE choosing Kotlin. Set the *Main Class* as ```douglasspgyn.com.github.maximatechtrainingserver.ApplicationKt``` and *Use classpath of module* as ```maximaTechTrainingServer.main```.

So it's ready to test locally, just run and enjoy it.

You can use [Postman](https://www.getpostman.com/) for test the API.