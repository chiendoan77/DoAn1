# Nhà Tốt — Frontend

Next.js App Router application using TypeScript and Tailwind CSS.

## Run locally

To start the frontend, API Gateway, all backend services and MySQL together, start Docker Desktop and run from the repository root:

```powershell
docker compose up --build
```

Open `http://localhost:3000`. The Next.js BFF calls the Gateway inside the Docker network; the optional host Gateway address is `http://localhost:8080`.

For frontend-only development, Node.js 22+ and npm are required:

```powershell
cd font-end
npm ci
npm run dev
```

Set `API_GATEWAY_URL=http://localhost:8080` when running the frontend outside Docker. Run `npm run lint`, `npm run typecheck`, and `npm run build` before submitting changes.
