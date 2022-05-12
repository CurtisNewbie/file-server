import { HttpHeaders } from "@angular/common/http";

// for development
const isThroughGateway = true;

const BASE_API = "/open/api";
const TOKEN = "token";
let emptyTokenCallback;

export function onEmptyToken(callback) {
  emptyTokenCallback = callback;
}

export function buildApiPath(
  subPath: string,
  service = "/file-service"
): string {
  service = service.startsWith("/", 0) ? service : "/" + service;
  subPath = subPath.startsWith("/", 0) ? subPath : "/" + subPath;
  return (isThroughGateway ? service : "") + BASE_API + subPath;
}

export function buildOptions() {
  let token = getToken();
  if (!token && emptyTokenCallback) {
    console.log("No token found, invoking registered onEmptyToken callback");
    emptyTokenCallback();
    return;
  }
  return {
    headers: new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: token,
    }),
    withCredentials: true,
  };
}

export function setToken(token: string) {
  if (token === null) localStorage.removeItem(TOKEN);
  else {
    localStorage.setItem(TOKEN, token);
  }
}

export function getToken() {
  return localStorage.getItem(TOKEN);
}
