const usernameInput = document.getElementById("usernameInput");
const passwordInput = document.getElementById("passwordInput");
const registerDiv = document.getElementById("registerDiv");
const userp = document.getElementById("userp");

function fetchUserInfo() {
  fetch("/user/info", {
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
      userp.textContent = `${result.data.username} (${result.data.role})`;
    })
    .catch((e) => {
      console.log("Failed to fetch user info", e);
    });
}

function swapRegisterFormVisibility() {
  registerDiv.hidden = !registerDiv.hidden;
}

function register() {
  let usn = usernameInput.value;
  let pwd = passwordInput.value;

  if (!usn || !pwd) {
    window.alert("Please enter username and password");
    return;
  }

  fetch("/user/register/guest", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: usn,
      password: pwd,
    }),
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
    })
    .catch((e) => {
      window.alert("Register failed");
      console.log("Register failed", e);
    })
    .finally(() => {
      usernameInput.value = "";
      passwordInput.value = "";
      swapRegisterFormVisibility();
    });
}

fetchUserInfo();
