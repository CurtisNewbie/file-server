// global var
const uploadNameInput = document.getElementById("uploadName");
const uploadFileInput = document.getElementById("uploadFile");
const listDiv = document.getElementById("listDiv");
let supportedExtensions = new Set();
const fileExtSpan = document.getElementById("fileExtSpan");
const statusStr = "";
const KB_UNIT = 1024;
const MB_UNIT = 1024 * 1024;
const GB_UNIT = 1024 * 1024 * 1024;

async function upload() {
  const uploadNameStr = uploadNameInput.value;
  if (!uploadNameStr || uploadNameStr.length === 0) {
    window.alert("File name cannot be empty");
    return;
  }
  if (uploadFileInput.files.length === 0) {
    window.alert("Please select a file to upload");
    return;
  }
  let fileExt = parseFileExt(uploadNameStr);
  console.log(fileExt);
  if (!fileExt) {
    window.alert("Please specify file extension");
    return;
  }
  if (!supportedExtensions.has(fileExt)) {
    window.alert(`File extension '${fileExt}' isn't supported`);
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
      }
      // clear the files selected
      uploadFileInput.value = [];
      // clear the fileName
      uploadNameInput.value = "";
      // clear the list, and reload the new one
      listDiv.innerHTML = "";
      getList();
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
        return;
      }
      console.log(result);
      if (result.hasError) {
        window.alert(result.msg);
        return;
      }

      const list = result.data;
      for (let p of list) {
        let li = document.createElement("li");
        let innerLink = document.createElement("a");
        innerLink.href = "file/download?filePath=" + p.fileName;
        innerLink.textContent = `'${p.fileName}', size: ${resolveSize(
          p.sizeInBytes
        )}`;
        li.appendChild(innerLink);
        li.classList.add("list-group-item");
        li.classList.add("list-group-item-action");
        li.setAttribute("target", "_blank");
        li.style.wordBreak = "break-all";
        listDiv.appendChild(li);
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
        return;
      }
      console.log(result);
      if (result.hasError) {
        window.alert(result.msg);
        return;
      }

      const list = result.data;
      let listStr = "";
      for (let i = 0; i < list.length; i++) {
        if (i > 0) {
          listStr += ", ";
        }
        let currStr = String(list[i]);
        listStr += currStr;
        supportedExtensions.add(currStr);
      }
      fileExtSpan.textContent = listStr;
    })
    .catch((error) => {
      console.error("Error:", error);
      window.alert("Failed to fetch supported file extensions");
    });
}

/**
 * Get file extension
 * @param {*} path
 * @returns fileExtension, or "" if there isn't one
 */
function parseFileExt(path) {
  if (!path || path.length === 0 || path.endsWith(".")) {
    return "";
  }
  let i = path.lastIndexOf(".");
  if (i <= 0) {
    return "";
  }
  return path.substring(i + 1);
}

function resolveSize(size) {
  if (size > GB_UNIT) {
    return divideUnit(size, GB_UNIT) + " gb";
  }
  if (size > MB_UNIT) {
    return divideUnit(size, MB_UNIT) + " mb";
  }
  return divideUnit(size, KB_UNIT) + " kb";
}

function divideUnit(size, unit) {
  return (size / unit).toFixed(1);
}

/**
 * logout current user by navigating to /logout url
 *
 */
function logout() {
  window.location.replace("/logout");
}

// ------------------------------- main ------------------------------
uploadFileInput.onchange = (e) => {
  uploadNameInput.value = uploadFileInput.files[0].name;
  console.log(uploadFileInput.files[0].name);
};
getList();
getSupportedFileExtension();
