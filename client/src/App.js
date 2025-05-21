import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import 'bootstrap/dist/css/bootstrap.min.css';

// Import pages
import Dashboard from './pages/Dashboard';
import MoviesManagement from './pages/MoviesManagement';
// Đã loại bỏ import EditMovie vì không cần trang này nữa
import ShowtimesManagement from './pages/ShowtimesManagement';
// Đã loại bỏ import CreateShowtime vì đã tích hợp vào ShowtimesManagement
import TheatresManagement from './pages/TheatresManagement';
import ScreensManagement from './pages/ScreensManagement';
import SeatsManagement from './pages/SeatsManagement';
import AuthStaffPage from './pages/AuthStaffPage';

// Import auth components
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';

// Create a theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            {/* Dashboard - protected */}
            <Route 
              path="/dashboard" 
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              } 
            />

            {/* Movies Management - protected */}
            <Route 
              path="/admin/movies" 
              element={
                <PrivateRoute>
                  <MoviesManagement />
                </PrivateRoute>
              } 
            />
            {/* Đã loại bỏ route edit movie vì không cần trang này nữa */}

            {/* Showtimes Management - protected */}
            <Route 
              path="/admin/showtimes" 
              element={
                <PrivateRoute>
                  <ShowtimesManagement />
                </PrivateRoute>
              } 
            />
            {/* Đã loại bỏ route /admin/showtimes/create vì đã tích hợp vào ShowtimesManagement */}

            {/* Theatres Management - protected */}
            <Route 
              path="/admin/theatres" 
              element={
                <PrivateRoute>
                  <TheatresManagement />
                </PrivateRoute>
              } 
            />

            {/* Screens Management - protected */}
            <Route 
              path="/admin/screens" 
              element={
                <PrivateRoute>
                  <ScreensManagement />
                </PrivateRoute>
              } 
            />

            {/* Seats Management - protected */}
            <Route 
              path="/admin/seats" 
              element={
                <PrivateRoute>
                  <SeatsManagement />
                </PrivateRoute>
              } 
            />

            {/* Authentication - chỉ dùng AuthStaffPage */}
            <Route path="/login" element={<Navigate to="/staff/auth" replace />} />
            <Route path="/staff/auth" element={<AuthStaffPage />} />

            {/* Redirect to dashboard by default */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;