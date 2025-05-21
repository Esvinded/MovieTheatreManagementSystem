import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { CircularProgress, Box } from '@mui/material';

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useContext(AuthContext);

  // Hiển thị loading indicator khi đang kiểm tra trạng thái auth
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!isAuthenticated()) {
    // Chuyển hướng đến trang đăng nhập nếu không xác thực
    return <Navigate to="/login" replace />;
  }

  // Trả về children nếu đã xác thực
  return children;
};

export default PrivateRoute;