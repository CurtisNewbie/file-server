import { HttpHeaders } from "@angular/common/http";

// for development
const isThroughGateway = true;

const BASE_API = "/open/api";
const TOKEN = "token";

export function buildApiPath(
  subPath: string,
  service = "/file-service"
): string {
  service = service.startsWith("/", 0) ? service : "/" + service;
  subPath = subPath.startsWith("/", 0) ? subPath : "/" + subPath;
  return (isThroughGateway ? service : "") + BASE_API + subPath;
}

export function buildOptions() {
  return {
    headers: new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: localStorage.getItem(TOKEN),
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
