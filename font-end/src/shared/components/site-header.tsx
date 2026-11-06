"use client";

import styles from "./site-header.module.css";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { useRouter } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import { Account, CustomerProfile, Provider, apiRequest } from "@/lib/api";

const emptyProfile: CustomerProfile = {
  displayName: "",
  phoneNumber: "",
  defaultAddress: "",
  avatarUrl: "",
};

export function SiteHeader() {
  const router = useRouter();
  const [account, setAccount] = useState<Account | null>(null);
  const [profile, setProfile] = useState<CustomerProfile>(emptyProfile);
  const [provider, setProvider] = useState<Provider | null>(null);
  const [open, setOpen] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [saveError, setSaveError] = useState("");
  const [saved, setSaved] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    apiRequest<Account>("me")
      .then((currentAccount) => {
        setAccount(currentAccount);
        setProfile({
          ...emptyProfile,
          displayName: currentAccount.fullName,
          phoneNumber: currentAccount.phoneNumber,
        });
        if (currentAccount.role === "CUSTOMER") {
          setLoadingProfile(true);
          apiRequest<CustomerProfile>("customer/profile")
            .then(setProfile)
            .catch(() => setLoadError("Chưa tải được hồ sơ đã lưu. Bạn có thể nhập thông tin rồi thử lưu lại."))
            .finally(() => setLoadingProfile(false));
        } else if (currentAccount.role === "PROVIDER") {
          apiRequest<Provider>("provider/profile")
            .then(setProvider)
            .catch(() => setProvider(null));
        }
      })
      .catch(() => setAccount(null));
  }, []);

  async function saveProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setSaveError("");
    setSaved(false);
    try {
      const updatedProfile = await apiRequest<CustomerProfile>("customer/profile", {
        method: "PUT",
        body: JSON.stringify({ ...profile, avatarUrl: profile.avatarUrl || null }),
      });
      setProfile(updatedProfile);
      setSaved(true);
      setLoadError("");
    } catch (cause) {
      setSaveError(cause instanceof Error ? cause.message : "Không lưu được hồ sơ.");
    } finally {
      setSaving(false);
    }
  }

  async function signOut() {
    await fetch("/api/session", { method: "DELETE" });
    setAccount(null);
    setOpen(false);
    router.push("/");
  }

  const avatarUrl = account?.role === "CUSTOMER" ? profile.avatarUrl : provider?.imageUrl;
  const avatarLabel = account?.fullName.trim().charAt(0).toUpperCase() || "T";

  return (
    <header className={styles.style1}>
      <nav aria-label="Điều hướng chính" className={styles.style2}>
        <Link className={styles.style3} href="/" aria-label="Nhà Tốt - Trang chủ">
          <span className={styles.style4}>n</span>
          <span className={styles.style5}>nhà tốt</span>
        </Link>
        <div className={styles.style6}>
          <Link className={styles.style7} href="/#services">Dịch vụ</Link>
          {account ? (
            <>
              <Link className={styles.style8} href="/history">Lịch sử</Link>
              <Link className={styles.style7} href="/notifications">Thông báo</Link>
              {account.role === "PROVIDER" && <Link className={styles.style7} href="/provider/packages">Gói dịch vụ</Link>}
              {account.role === "ADMIN" && <Link className={styles.style7} href="/admin/providers">Duyệt hồ sơ</Link>}
              {account.role === "ADMIN" && <Link className={styles.style7} href="/admin/packages">Duyệt gói</Link>}
              <button
                type="button"
                aria-label={`Mở hồ sơ ${account.fullName}`}
                aria-expanded={open}
                aria-controls="account-receipt"
                onClick={() => setOpen((current) => !current)}
                className={styles.style9}
              >
                {avatarUrl ? <img className={styles.style10} src={avatarUrl} alt="" /> : avatarLabel}
              </button>
            </>
          ) : (
            <>
              <Link className={styles.style7} href="/register?role=PROVIDER">Đăng ký đối tác</Link>
              <Link className={styles.style11} href="/login">Đăng nhập</Link>
            </>
          )}
        </div>
      </nav>

      {account && open && (
        <section id="account-receipt" aria-label="Hồ sơ tài khoản" className={styles.style12}>
          <div className={styles.style13}>
            <p className={styles.style14}>Nhà Tốt · Hồ sơ cá nhân</p>
            <h2 className={styles.style15}>{account.role === "CUSTOMER" ? profile.displayName || account.fullName : account.fullName}</h2>
            <p className={styles.style16}>{account.email}</p>
          </div>

          {account.role === "CUSTOMER" ? (
            <form className={styles.style17} onSubmit={(event) => void saveProfile(event)}>
              <label className={styles.style18}>
                Tên hiển thị
                <input required maxLength={120} value={profile.displayName} onChange={(event) => setProfile({ ...profile, displayName: event.target.value })} className={styles.style19} />
              </label>
              <label className={styles.style18}>
                Số điện thoại
                <input required type="tel" pattern="[+]?[0-9][0-9 ()-]{7,22}" value={profile.phoneNumber} onChange={(event) => setProfile({ ...profile, phoneNumber: event.target.value })} className={styles.style19} />
              </label>
              <label className={styles.style18}>
                Địa chỉ mặc định
                <input required maxLength={500} value={profile.defaultAddress} onChange={(event) => setProfile({ ...profile, defaultAddress: event.target.value })} className={styles.style19} placeholder="Địa chỉ để nhà cung cấp đến phục vụ" />
              </label>
              <label className={styles.style18}>
                Avatar · liên kết ảnh HTTPS
                <input type="url" pattern="https://.+" value={profile.avatarUrl ?? ""} onChange={(event) => setProfile({ ...profile, avatarUrl: event.target.value })} className={styles.style19} placeholder="https://…" />
              </label>
              {loadingProfile && <p className={styles.style20}>Đang tải hồ sơ…</p>}
              {loadError && <p role="status" className={styles.style21}>{loadError}</p>}
              {saveError && <p role="alert" className={styles.style22}>{saveError}</p>}
              {saved && <p role="status" className={styles.style23}>Đã lưu hồ sơ.</p>}
              <div className={styles.style24}>
                <span className={styles.style25}>Thông tin chỉ dùng để phục vụ lịch hẹn.</span>
                <button disabled={saving} className={styles.style26}>
                  {saving ? "Đang lưu…" : "Lưu hồ sơ"}
                </button>
              </div>
            </form>
          ) : (
            <div className={styles.style27}>
              <p><span className={styles.style28}>Vai trò:</span> {account.role === "PROVIDER" ? "Nhà cung cấp" : "Quản trị viên"}</p>
              {account.role === "PROVIDER" && provider && (
                <>
                  <p><span className={styles.style28}>Khu vực:</span> {provider.address}</p>
                  <p><span className={styles.style28}>Trạng thái hồ sơ:</span> {provider.status === "PENDING" ? "Chờ duyệt" : provider.status === "APPROVED" ? "Đã duyệt" : "Từ chối"}</p>
                </>
              )}
              {account.role === "PROVIDER" && <p className={styles.style29}>Hồ sơ nhà cung cấp được quản lý riêng để tránh thay đổi trạng thái duyệt ngoài ý muốn.</p>}
              <button onClick={() => void signOut()} className={styles.style30}>
                Đăng xuất
              </button>
            </div>
          )}
          {account.role === "CUSTOMER" && (
            <button onClick={() => void signOut()} className={styles.style30}>
              Đăng xuất
            </button>
          )}
        </section>
      )}
    </header>
  );
}
