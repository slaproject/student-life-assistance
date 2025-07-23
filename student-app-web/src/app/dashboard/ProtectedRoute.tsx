"use client";
import React, { ReactNode, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../AuthContext";

export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/login");
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) return null;
  return <>{children}</>;
} 