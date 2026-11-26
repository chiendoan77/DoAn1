import { ServiceCatalogPage } from "@/features/marketplace/service-catalog-page";
import { Suspense } from "react";

export default function RepairPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-bg" />}>
      <ServiceCatalogPage service="repair" />
    </Suspense>
  );
}
