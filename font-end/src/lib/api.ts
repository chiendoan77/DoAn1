export type Account = {
  id: string;
  email: string;
  phoneNumber: string;
  username: string;
  fullName: string;
  role: "CUSTOMER" | "PROVIDER" | "ADMIN";
};

export type ServiceCategory = {
  id: string;
  name: string;
  description: string;
};

export type Provider = {
  id: string;
  fullName: string;
  categoryIds: string[];
  categories: ServiceCategory[];
  address: string;
  imageUrl: string;
  description: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  rating: number;
  reviewCount: number;
  demoData: boolean;
};

export type Booking = {
  id: string;
  categoryId: string;
  categoryName: string;
  providerProfileId: string;
  providerName: string;
  customerName: string;
  scheduledAt: string;
  address: string;
  description: string;
  status: "REQUESTED" | "ACCEPTED" | "REJECTED" | "COMPLETED";
  createdAt: string;
};

export type Review = {
  id: string;
  bookingId: string;
  reviewerRole: "CUSTOMER" | "PROVIDER";
  targetRole: "CUSTOMER" | "PROVIDER";
  rating: number;
  comment: string;
  createdAt: string;
};

function getValidationMessages(payload: unknown): string[] {
  if (!payload || typeof payload !== "object" || !("errors" in payload)) {
    return [];
  }
  const errors = payload.errors;
  if (!errors || typeof errors !== "object") {
    return [];
  }
  return Object.values(errors)
    .flatMap((value) => Array.isArray(value) ? value : [value])
    .filter((value): value is string => typeof value === "string");
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`/api/backend/${path.replace(/^\/+/, "")}`, {
    ...init,
    headers: {
      ...(init?.body ? { "content-type": "application/json" } : {}),
      ...init?.headers,
    },
    cache: "no-store",
  });
  const text = await response.text();
  let payload: unknown;
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch {
      payload = text;
    }
  }

  if (!response.ok) {
    const validationMessages = getValidationMessages(payload);
    const error = validationMessages.length > 0
      ? validationMessages.join("\n")
      : payload && typeof payload === "object" && "detail" in payload
        ? String(payload.detail)
        : payload && typeof payload === "object" && "message" in payload
          ? String(payload.message)
          : "Yêu cầu không thành công.";
    throw new Error(error);
  }
  if (text && typeof payload === "string") {
    throw new Error("Máy chủ trả về dữ liệu không đúng định dạng.");
  }
  return payload as T;
}
