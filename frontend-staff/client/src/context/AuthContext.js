import React, { createContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

// Create context
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Kiểm tra xem có thông tin người dùng trong localStorage không
    const checkAuthStatus = async () => {
      const staffData = localStorage.getItem('staff');
      const isAuth = localStorage.getItem('isAuthenticated');

      if (staffData && isAuth === 'true') {
        try {
          // Lấy dữ liệu từ localStorage
          const userData = JSON.parse(staffData);
          setCurrentUser(userData);
        } catch (error) {
          console.error('Error parsing user data:', error);
          // Xóa dữ liệu không hợp lệ
          localStorage.removeItem('staff');
          localStorage.removeItem('isAuthenticated');
          localStorage.removeItem('userRole');
        }
      }

      setLoading(false);
    };

    checkAuthStatus();
  }, []);

  // Login function sử dụng authAPI (dành riêng cho STAFF)
  const login = async (credentials) => {
    try {
      const userData = await authAPI.loginStaff(credentials);
      // Lưu thông tin user vào localStorage để cache
      localStorage.setItem('staff', JSON.stringify(userData));
      localStorage.setItem('isAuthenticated', 'true');
      localStorage.setItem('userRole', 'STAFF');

      setCurrentUser(userData);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  // Logout function sử dụng authAPI
  const logout = async () => {
    try {
      // Gọi API logout từ Spring Boot
      await authAPI.logout();
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      // Xóa thông tin user khỏi localStorage 
      localStorage.removeItem('staff');
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('userRole');
      setCurrentUser(null);
    }
  };

  // Check if user is authenticated
  const isAuthenticated = () => {
    return !!currentUser && localStorage.getItem('isAuthenticated') === 'true';
  };

  // Get user token/session id
  const getToken = () => {
    return currentUser ? 'session-auth' : null;
  };

  // Get user data
  const getUserData = () => {
    return currentUser;
  };

  // Get user role
  const getUserRole = () => {
    return localStorage.getItem('userRole') || '';
  };

  // Context value
  const value = {
    currentUser,
    loading,
    login,
    logout,
    isAuthenticated,
    getToken,
    getUserData: () => currentUser, // Thay thế getUserData để trả về currentUser
    getUserRole
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};