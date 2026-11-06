# Marketplace API

Gateway không route review mới, profile/provider browse hay catalog tới Marketplace; các luồng hiện tại được sở hữu bởi Review, Profile và Catalog. Marketplace còn legacy controller/tables và migration export; endpoint export không phải public API.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/internal/migration/reviews` | Review migration API key | Xuất review legacy để import một lần sang Review service. |
| GET | `/internal/migration/provider-profiles` | Profile migration API key | Xuất provider profiles legacy để import sang Profile. |

Provider application/profile, categories, package CRUD, admin package moderation and public package listing are documented in [Profile](../profile/API.md) and [Catalog](../catalog/API.md).
