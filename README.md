# docker-compose demo app

This is a Spring Boot based Java app which connects to a MySQL instance 
running inside a Docker container in order to demonstrate docker-compose
capabilities.

Application assumes that a MySQL database with name **demo** is deployed 
on the host **demodb**. Default credentials are below:

* **Username:** user
* **Password:** test

You do not have to do anything if you use `docker-compose`. Docker Compose
starts all containers in parallel. Even if you use linking or `depends_on`,
it doesn't wait until the container is fully available. Spring Boot app fails
if MySQL isn't up and running during initialization. Therefore we need to use
the script `wait-for-it.sh` to wait until DB container is up and running.

## Usage

Type the following command and app should be available at 
`http://localhost:8080/api/people` if everything goes well.

```
docker-compose up -d
```

When you're done with it, you may stop and remove containers with below
command:

```
docker-compose down
```