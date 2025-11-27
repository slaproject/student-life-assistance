import type { Metadata } from "next";
import { Space_Grotesk, Inter, JetBrains_Mono } from "next/font/google";
import "./globals.css";
import "./styles/app.css";
import { AuthProvider } from "./AuthContext";
import Navbar from "./components/Navbar";
import { Analytics } from "@vercel/analytics/react";
import ThemeRegistry from './ThemeRegistry';

// Font for headings - bold, modern, geometric
const spaceGrotesk = Space_Grotesk({
  variable: "--font-heading",
  subsets: ["latin"],
  weight: ["300", "400", "500", "600", "700"],
  display: "swap",
});

// Font for body text - clean, highly readable
const inter = Inter({
  variable: "--font-body",
  subsets: ["latin"],
  weight: ["300", "400", "500", "600", "700"],
  display: "swap",
});

// Font for code/monospace - professional
const jetbrainsMono = JetBrains_Mono({
  variable: "--font-mono",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  display: "swap",
});

export const metadata: Metadata = {
  title: "Student Life Assistance Project (SLAP)",
  description: "A comprehensive platform for student productivity and organization",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${spaceGrotesk.variable} ${inter.variable} ${jetbrainsMono.variable}`}>
        <ThemeRegistry>
          <AuthProvider>
            <Navbar />
            {children}
          </AuthProvider>
          <Analytics />
        </ThemeRegistry>
      </body>
    </html>
  );
}
