
import styles from "./home-page.module.css";
import Link from "next/link";
import { SiteHeader } from "@/shared/components/site-header";

const services = [
  {
    href: "/services/cleaning",
    icon: "⌂",
    title: "Dọn dẹp nhà",
    description: "Tìm người hỗ trợ dọn dẹp định kỳ hoặc tổng vệ sinh, phù hợp với lịch sinh hoạt của gia đình.",
    label: "Khám phá dịch vụ dọn dẹp",
  },
  {
    href: "/services/repair",
    icon: "＋",
    title: "Sửa chữa gia đình",
    description: "Kết nối với nhà cung cấp dịch vụ sửa chữa và đặt lịch đến nhà thuận tiện.",
    label: "Khám phá dịch vụ sửa chữa",
  },
];

export function HomePage() {
  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <div className={styles.style3} />
        <div className={styles.style4}>
          <div className={styles.style5}>
            <p className={styles.style6}>
              <span className={styles.style7} /> Chăm nhà nhẹ nhàng hơn
            </p>
            <h1 className={styles.style8}>
              Việc nhà để <span className={styles.style9}>người giỏi</span> lo.
            </h1>
            <p className={styles.style10}>
              Nhà Tốt giúp bạn tìm dịch vụ chăm sóc tổ ấm đáng tin cậy, chọn nhà cung cấp và đặt lịch dễ dàng.
            </p>
            <Link className={styles.style11} href="#services">
              Chọn dịch vụ <span className={styles.style12} aria-hidden="true">→</span>
            </Link>
          </div>
          <div className={styles.style13}>
            <div className={styles.style14}>
              <span aria-hidden="true" className={styles.style15}>⌂</span>
              <div><p className={styles.style16}>Không gian</p><p className={styles.style17}>Sạch &amp; gọn</p></div>
            </div>
            <div className={styles.style18}>
              <span aria-hidden="true" className={styles.style15}>✳</span>
              <div><p className={styles.style16}>Lịch hẹn</p><p className={styles.style17}>Theo thời gian bạn</p></div>
            </div>
            <div className={styles.style19}>
              <span className={styles.style20} aria-hidden="true">✓</span>
              <p className={styles.style21}>Hồ sơ nhà cung cấp được xét duyệt trước khi hiển thị công khai.</p>
            </div>
          </div>
        </div>
      </section>

      <section id="services" className={styles.style22}>
        <div className={styles.style23}>
          <p className={styles.style24}>Dịch vụ cho ngôi nhà</p>
          <h2 className={styles.style25}>Bạn đang cần giúp việc gì?</h2>
          <div className={styles.style26}>
            {services.map((service) => (
              <Link key={service.href} href={service.href} className={styles.style27}>
                <span className={styles.style28} aria-hidden="true">{service.icon}</span>
                <h3 className={styles.style29}>{service.title}</h3>
                <p className={styles.style30}>{service.description}</p>
                <span className={styles.style31}>{service.label} <span className={styles.style32} aria-hidden="true">→</span></span>
              </Link>
            ))}
          </div>
        </div>
      </section>

      <section className={styles.style33}>
        <div className={styles.style34}>
          <div>
            <p className={styles.style35}>Dành cho nhà cung cấp</p>
            <h2 className={styles.style36}>Mang dịch vụ của bạn đến gần khách hàng hơn.</h2>
          </div>
          <Link className={styles.style37} href="/register?role=PROVIDER">
            Đăng ký làm đối tác <span className={styles.style12} aria-hidden="true">→</span>
          </Link>
        </div>
      </section>

      <footer className={styles.style38}>
        <div className={styles.style39}>
          <p>© 2026 Nhà Tốt. Kết nối dịch vụ, chăm sóc tổ ấm.</p>
          <Link href="/admin/providers">Dành cho quản trị viên</Link>
        </div>
      </footer>
    </main>
  );
}
