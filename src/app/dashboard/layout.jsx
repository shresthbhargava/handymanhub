"use client";

import React from 'react';
import DashboardLayout from '../../components/layout/DashboardLayout';

export default function DashboardLayoutWrapper({ children }) {
  return <DashboardLayout>{children}</DashboardLayout>;
}
