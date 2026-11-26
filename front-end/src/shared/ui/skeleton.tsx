export function Skeleton({ className = "" }: { className?: string }) {
  return <div aria-hidden="true" className={`animate-pulse rounded-md bg-brand-100 ${className}`} />;
}

export function EmptyState({ icon, title, description, action }: { icon?: React.ReactNode; title: string; description: string; action?: React.ReactNode }) {
  return (
    <div className="flex min-h-64 flex-col items-center justify-center rounded-lg border border-border bg-surface px-6 py-10 text-center">
      {icon && <div className="mb-3 text-brand-600">{icon}</div>}
      <h2 className="text-lg font-bold text-text">{title}</h2>
      <p className="mt-2 max-w-md text-sm text-muted">{description}</p>
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}
