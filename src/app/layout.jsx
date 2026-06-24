import '../styles/globals.css';
import { AuthProvider } from '../context/AuthContext';
import CustomCursor from '../components/ui/CustomCursor';

export const metadata = {
  title: 'HandymanHub | Global Standards. Local Expertise.',
  description: 'The Next Gen Workforce directory for skilled tradesmen in India.',
  manifest: '/manifest.json',
};

export const viewport = {
  themeColor: '#0A0B10',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&display=swap" rel="stylesheet" />
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossOrigin="" />
      </head>
      <body suppressHydrationWarning>
        <AuthProvider>
          <CustomCursor />
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}
