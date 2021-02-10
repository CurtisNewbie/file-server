function upload() {
  const uploadNameInput = document.getElementById("uploadName");
  const uploadFileInput = document.getElementById("uploadFile");

  const uploadNameStr = uploadNameInput.value;
  if (!uploadNameStr || uploadNameStr.length === 0) {
    window.alert("File name cannot be empty");
    return;
  }
  if (uploadFileInput.files.length === 0) {
    window.alert("Please select a file to upload");
    return;
  }

  const formData = new FormData();
  formData.append("filePath", uploadNameStr);
  formData.append("file", uploadFileInput.files[0]);

  fetch("http://localhost:8080/file/upload", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.text())
    .then((result) => {
      if (!result || result.length == 0) {
        console.log("Returned response abnormal");
      }
      let json = JSON.parse(result);
      console.log(json);
      if (json.hasError) {
        window.alert(json.msg);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to upload file");
    });
}

function download(path) {
  fetch("http://localhost:8080/file/download", {
    method: "POST",
    body: {
      filePath: path,
    },
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (!result || result.length == 0) {
        console.log("Returned response abnormal");
      }
      let json = JSON.parse(result);
      console.log(json);
      if (json.hasError) {
        window.alert(json.msg);
      }
    });
}

function getList() {
  fetch("http://localhost:8080/file/list", {
    method: "GET",
  })
    .then((response) => response.text())
    .then((result) => {
      if (!result || result.length == 0) {
        console.log("Returned response abnormal");
      }
      let json = JSON.parse(result);
      console.log(json);
      if (json.hasError) {
        window.alert(json.msg);
      } else {
        const list = json.data;
        const outerDiv = document.getElementById("listDiv");
        for (let p of list) {
          let li = document.createElement("li");
          let innerLink = document.createElement("a");
          innerLink.href = "http://localhost:8080/file/download?filePath=" + p;
          innerLink.textContent = p;
          li.appendChild(innerLink);
          li.classList.add("list-group-item");
          li.classList.add("list-group-item-action");
          li.setAttribute("target", "_blank");
          li.style.wordBreak = "break-all";
          outerDiv.appendChild(li);
        }
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to fetch file list");
    });
}

getList();
