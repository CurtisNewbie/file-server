// global var
const uploadNameInput = document.getElementById("uploadName");
const uploadFileInput = document.getElementById("uploadFile");
const listDiv = document.getElementById("listDiv");
const fileExtSpan = document.getElementById("fileExtSpan");

function upload() {
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

  fetch("/file/upload", {
    method: "POST",
    body: formData,
  })
    .then((response) => response.json())
    .then((result) => {
      if (!result) {
        console.log("Returned response abnormal");
        return;
      }
      console.log(result);
      if (result.hasError) {
        window.alert(result.msg);
        return;
      } else {
        window.location.reload();
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to upload file");
    });
}

function getList() {
  fetch("/file/list", {
    method: "GET",
  })
    .then((response) => response.json())
    .then((result) => {
      if (!result) {
        console.log("Returned response abnormal");
      }
      console.log(result);
      if (result.hasError) {
        window.alert(result.msg);
      } else {
        const list = result.data;
        for (let p of list) {
          let li = document.createElement("li");
          let innerLink = document.createElement("a");
          innerLink.href = "file/download?filePath=" + p;
          innerLink.textContent = p;
          li.appendChild(innerLink);
          li.classList.add("list-group-item");
          li.classList.add("list-group-item-action");
          li.setAttribute("target", "_blank");
          li.style.wordBreak = "break-all";
          listDiv.appendChild(li);
        }
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to fetch file list");
    });
}

function getSupportedFileExtension() {
  fetch("/file/extension", {
    method: "GET",
  })
    .then((response) => response.json())
    .then((result) => {
      if (!result) {
        console.log("Returned response abnormal");
      }
      console.log(result);
      if (result.hasError) {
        window.alert(result.msg);
      } else {
        const list = result.data;
        let listStr = "";
        for (let i = 0; i < list.length; i++) {
          if (i > 0) listStr += ", ";
          listStr += list[i];
        }
        fileExtSpan.textContent = listStr;
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to fetch supported file extensions");
    });
}

// ------------------------------- main ------------------------------
uploadFileInput.onchange = (e) => {
  uploadNameInput.value = uploadFileInput.files[0].name;
  console.log(uploadFileInput.files[0].name);
};
getList();
getSupportedFileExtension();
