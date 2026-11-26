import { ReactNode } from "react";
import { Check } from "lucide-react";

type Props = {
  selected: boolean;
  title: string;
  description: string;
  icon: ReactNode;
  onClick: () => void;
};

export function RoleCard({ selected, title, description, icon, onClick }: Props) {
  return (
    <button
      type="button"
      aria-pressed={selected}
      onClick={onClick}
      className={`relative min-h-[68px] w-full rounded-md border-2 px-4 py-2.5 text-left transition ${
        selected
          ? "border-brand-600 bg-brand-50"
          : "border-border bg-surface hover:border-brand-500"
      }`}
    >
      {selected && (
        <span className="absolute right-3 top-3 grid h-5 w-5 place-items-center rounded-full bg-brand-600 text-white">
          <Check className="h-3.5 w-3.5" aria-hidden="true" />
        </span>
      )}
      <span className="flex items-center gap-3">
        <span className="grid h-9 w-9 shrink-0 place-items-center rounded-md bg-brand-100 text-brand-700 [&>svg]:h-5 [&>svg]:w-5">
          {icon}
        </span>
        <span>
          <span className="block text-base font-bold text-text">{title}</span>
          <span className="mt-0.5 block text-sm leading-5 text-muted">{description}</span>
        </span>
      </span>
    </button>
  );
}
