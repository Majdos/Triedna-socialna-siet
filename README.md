# Triedna sociálna sieť - Student social network

Student social network is platform for students where they can share news, socialize and collaborate.

<img src="index_page.png">

## Motivation

Original idea was to integrate school's canteen, edupage and social network into one service. I managed to implement only the social network part,
because I didn't have enough time to implement reliable web scraper, which would parse data from canteen and edupage.

## Features

- authentication is done on Google servers with protocol OAuth2

- because of OAuth2, only students with school's email are allowed to use
  service (only if school provides Gmail addresses)

- content loads lazily. The content loads as user browses content

- user can use markdown to format text in posts and comments

- content is synchronized with server. The project uses Websockets
  to achieve synchronization

- user can use data stored on server. The data can be fetched from REST API

## Installation

The project comes with installation script, which installs backend and frontend
dependencies. It also setups environment.

### Build steps

1. edit following files: .env.database.local, .env.spring.local
2. run command `./mvnw exec:exec@install exec:exec@dist package`. This command installs and
compiles dependencies.
3. start containers `docker-compose up`. The web server will listen to port 8080
