import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    host: "0.0.0.0", // Nasłuchiwanie na wszystkich interfejsach
    port: 3000, // Port dla frontendu
    proxy: {
      "/api": {
        target: "http://backend:5000", // Backend w sieci Dockera
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  },
});