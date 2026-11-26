"use client";

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { Account, CustomerProfile, Provider, apiRequest } from "@/lib/api";

type SessionValue = {
  account: Account | null;
  customerProfile: CustomerProfile | null;
  providerProfile: Provider | null;
  loading: boolean;
  refresh: () => Promise<Account | null>;
  signOut: () => Promise<void>;
};

const SessionContext = createContext<SessionValue | null>(null);

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const [account, setAccount] = useState<Account | null>(null);
  const [customerProfile, setCustomerProfile] = useState<CustomerProfile | null>(null);
  const [providerProfile, setProviderProfile] = useState<Provider | null>(null);
  const [loading, setLoading] = useState(true);

  async function refresh() {
    try {
      const current = await apiRequest<Account>("me");
      setAccount(current);
      if (current.role === "CUSTOMER") {
        try {
          setCustomerProfile(await apiRequest<CustomerProfile>("customer/profile"));
        } catch {
          setCustomerProfile(null);
        }
      } else if (current.role === "PROVIDER") {
        try {
          setProviderProfile(await apiRequest<Provider>("provider/profile"));
        } catch {
          setProviderProfile(null);
        }
      }
      return current;
    } catch {
      setAccount(null);
      setCustomerProfile(null);
      setProviderProfile(null);
      return null;
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let active = true;
    apiRequest<Account>("me")
      .then(async (current) => {
        if (!active) return;
        setAccount(current);
        if (current.role === "CUSTOMER") {
          try {
            setCustomerProfile(await apiRequest<CustomerProfile>("customer/profile"));
          } catch {
            setCustomerProfile(null);
          }
        } else if (current.role === "PROVIDER") {
          try {
            setProviderProfile(await apiRequest<Provider>("provider/profile"));
          } catch {
            setProviderProfile(null);
          }
        }
      })
      .catch(() => {
        if (!active) return;
        setAccount(null);
        setCustomerProfile(null);
        setProviderProfile(null);
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => { active = false; };
  }, []);

  async function signOut() {
    await fetch("/api/session", { method: "DELETE" });
    setAccount(null);
    setCustomerProfile(null);
    setProviderProfile(null);
  }

  const value = useMemo(
    () => ({ account, customerProfile, providerProfile, loading, refresh, signOut }),
    [account, customerProfile, providerProfile, loading],
  );
  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession() {
  const value = useContext(SessionContext);
  if (!value) throw new Error("useSession must be used inside SessionProvider.");
  return value;
}
