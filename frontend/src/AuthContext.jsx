import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";

// Cấu hình axios để luôn gửi cookie
axios.defaults.withCredentials = true;

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true); // Để chờ khi check phiên đăng nhập

  // Khi app khởi động, gọi API để xem user đã đăng nhập chưa
  useEffect(() => {
    const checkSession = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/customer/profile",
          {withCredentials: true} // Đảm bảo gửi cookie
        );
        setUser(response.data);
      } catch (error) {
        setUser(null); // chưa đăng nhập
      } finally {
        setLoading(false);
      }
    };

    checkSession();
  }, []);

  // Đăng nhập
  const login = async (username, password) => {
    const response = await axios.post(
      "http://localhost:8080/api/auth/customer/login",
      { username, password },
      { withCredentials: true } // Đảm bảo gửi cookie
    );

    if (response.data === "Customer login successful") {
      const profile = await axios.get("http://localhost:8080/api/customer/profile");
      setUser(profile.data);
      return profile.data;
    }

    throw new Error(response.data || "Login failed");
  };

  // Đăng ký
  const register = async (username, email, password) => {
    const response = await axios.post("http://localhost:8080/api/auth/customer/register", {
      username,
      email,
      password,
    });
    return response.data;
  };

  // Đăng xuất
  const logout = async () => {
    try {
      await axios.post("http://localhost:8080/api/auth/logout",
        {},
        { withCredentials: true } // Đảm bảo gửi cookie
      );
    } catch (error) {
      console.error("Logout failed", error.response?.data || error.message);
    } finally {
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider value={{ user, setUser, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
