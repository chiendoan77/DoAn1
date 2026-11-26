import { ServiceCatalogPage } from "@/features/marketplace/service-catalog-page";
import { Suspense } from "react";

export default function CleaningPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-bg" />}>
      <ServiceCatalogPage service="cleaning" />
    </Suspense>
  );
}
