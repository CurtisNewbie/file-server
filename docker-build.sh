./build.sh
docker build . -t file-server:latest
# add '-v ~/some/folder/to/mount:/home/file/server' before 'file-server:latest' for mounting
docker run --name file-server-app -p 8080:8080 -v ~/file/server:/home/file/server file-server:latest

