Azure study

- For upload file

`curl --location --request POST 'http://localhost:8080/api/files/upload' \
--form 'file=@"/Users/adithya/Desktop/Screenshot 2024-10-08 at 2.53.33 AM.png"'
`

- Download file

`  curl --location --request GET 'localhost:8080/api/files/download/37e5d8af-966a-3d03-9eda-ea7b603bd157'
`

- Delete file

`  curl --location --request DELETE 'localhost:8080/api/files/37e5d8af-966a-3d03-9eda-ea7b603bd157'
`
- Get Files list

`  curl --location --request GET 'localhost:8080/api/files/37e5d8af-966a-3d03-9eda-ea7b603bd157/metadata'
`

Connecting Mysql:



mysql -h 127.0.0.1 -P 3306 -u user -p

SHOW DATABASES;
