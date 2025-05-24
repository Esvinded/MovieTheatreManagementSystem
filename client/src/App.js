import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import 'bootstrap/dist/css/bootstrap.min.css';

// Import pages
import Dashboard from './pages/Dashboard';
import MoviesManagement from './pages/MoviesManagement';
import ShowtimesManagement from './pages/ShowtimesManagement';
import TheatresManagement from './pages/TheatresManagement';
import ScreensManagement from './pages/ScreensManagement';
import SeatsManagement from './pages/SeatsManagement';
import AuthStaffPage from './pages/AuthStaffPage';

// Import auth components
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';

// 🎨 Theme tím 
const theme = createTheme({
  palette: {
    mode: 'light', 
    primary: {
      main: '#1B1A55',       // tím navy
      dark: '#070F2B',       // tím đậm nhất
      light: '#535C91',      // tím trung tính
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#9290C3',        // tím pastel nhạt
      contrastText: '#ffffff',
    },
    background: {
      default: '#f4f4f4',     // hoặc '#070F2B' nếu dùng dark mode
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif',
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            <Route 
              path="/dashboard" 
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/admin/movies" 
              element={
                <PrivateRoute>
                  <MoviesManagement />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/admin/showtimes" 
              element={
                <PrivateRoute>
                  <ShowtimesManagement />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/admin/theatres" 
              element={
                <PrivateRoute>
                  <TheatresManagement />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/admin/screens" 
              element={
                <PrivateRoute>
                  <ScreensManagement />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/admin/seats" 
              element={
                <PrivateRoute>
                  <SeatsManagement />
                </PrivateRoute>
              } 
            />
            <Route path="/login" element={<Navigate to="/staff/auth" replace />} />
            <Route path="/staff/auth" element={<AuthStaffPage />} />
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
