./build.sh
docker build . -t file-server:latest
# add -v ~/some/folder/to/mount:/home/file/server for mounting
docker run --name file-server-app -p 8080:8080 file-server:latest


