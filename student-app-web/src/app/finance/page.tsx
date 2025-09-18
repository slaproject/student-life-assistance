"use client";
import React from "react";
import ProtectedRoute from "../dashboard/ProtectedRoute";
import FinanceTabs from "./components/FinanceTabs";

export default function FinancePage() {
  return (
    <ProtectedRoute>
      <FinanceTabs />
    </ProtectedRoute>
  );
}
