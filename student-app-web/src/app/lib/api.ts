"use client";
import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export function getApiClient() {
  const instance = axios.create({ baseURL: API_BASE_URL });
  instance.interceptors.request.use((config) => {
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("token");
      const looksLikeJwt = token && token.split(".").length === 3;
      if (looksLikeJwt) {
        config.headers = config.headers || {};
        (config.headers as any).Authorization = `Bearer ${token}`;
      }
    }
    return config;
  });
  return instance;
}
