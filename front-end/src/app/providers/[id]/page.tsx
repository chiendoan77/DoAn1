import { Suspense } from "react";
import { ProviderDetailPage } from "@/features/marketplace/provider-detail-page";

type Props = { params: Promise<{ id: string }> };

export default function ProviderPage({ params }: Props) {
  return (
    <Suspense fallback={<div className="min-h-screen bg-bg" />}>
      <ProviderPageContent params={params} />
    </Suspense>
  );
}

async function ProviderPageContent({ params }: Props) {
  const { id } = await params;
  return <ProviderDetailPage providerId={id} />;
}
