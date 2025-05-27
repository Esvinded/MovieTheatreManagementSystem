import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext"; // <-- Sử dụng AuthContext
import axios from "axios";

export default function AuthUser() {
  const navigate = useNavigate();
  const { login, register } = useAuth();

  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [showResetPassword, setShowResetPassword] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await login(username, password);
      navigate("/home-page");
    } catch (error) {
      console.error("Lỗi đăng nhập:", error.response?.data || error.message);
      alert(error.response?.data || "Đăng nhập thất bại");
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await register(username, email, password);
      alert("Đăng ký thành công! Vui lòng đăng nhập.");
      setIsLogin(true);
    } catch (error) {
      console.error("Lỗi đăng ký:", error.response?.data || error.message);
      alert(error.response?.data || "Đăng ký thất bại");
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/api/auth/forgot-password", {
        username,
        email
      });
      console.log("Yêu cầu đặt lại mật khẩu thành công:", response.data);
      alert("Vui lòng kiểm tra email để đặt lại mật khẩu.");
      setShowResetPassword(false);
    } catch (error) {
      console.error("Lỗi yêu cầu reset password:", error.response?.data || error.message);
      alert(error.response?.data || "Gửi yêu cầu thất bại");
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: "url('/background.jpg')" }}
    >
      <div className="w-full max-w-sm bg-white p-6 rounded shadow">
        <h2 className="text-center text-xl font-bold mb-4">Khách Hàng</h2>

        {showResetPassword ? (
          <form onSubmit={handleResetPassword} className="space-y-4">
            <input
              type="text"
              placeholder="Nhập tên đăng nhập"
              className="w-full p-2 mb-3 border rounded"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="email"
              placeholder="Nhập email để đặt lại mật khẩu"
              className="w-full p-2 mb-3 border rounded"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <button type="submit" className="w-full bg-blue-400 py-2 font-bold rounded">
              Gửi yêu cầu
            </button>
            <button
              type="button"
              onClick={() => setShowResetPassword(false)}
              className="w-full bg-gray-300 py-2 font-bold rounded mt-2"
            >
              Quay lại đăng nhập
            </button>
          </form>
        ) : (
          <>
            <div className="flex mb-6 border-b">
              <button
                className={`flex-1 text-center py-2 font-bold ${isLogin ? "border-b-2 border-black" : ""}`}
                onClick={() => setIsLogin(true)}
              >
                Đăng Nhập
              </button>
              <button
                className={`flex-1 text-center py-2 font-bold ${!isLogin ? "border-b-2 border-black" : ""}`}
                onClick={() => setIsLogin(false)}
              >
                Đăng Ký
              </button>
            </div>

            {isLogin ? (
              <form onSubmit={handleLogin} className="space-y-4">
                <input
                  type="text"
                  placeholder="Tên đăng nhập"
                  className="w-full p-2 mb-3 border rounded"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
                <input
                  type="password"
                  placeholder="Mật khẩu"
                  className="w-full p-2 mb-3 border rounded"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <button type="submit" className="w-full bg-yellow-400 py-2 font-bold rounded">
                  ĐĂNG NHẬP
                </button>
                <div className="text-center mt-2">
                  <button
                    type="button"
                    onClick={() => setShowResetPassword(true)}
                    className="text-blue-500 underline text-sm"
                  >
                    Quên mật khẩu?
                  </button>
                </div>
              </form>
            ) : (
              <form onSubmit={handleRegister} className="space-y-4">
                <input
                  type="text"
                  placeholder="Tên đăng nhập"
                  className="w-full p-2 mb-3 border rounded"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
                <input
                  type="email"
                  placeholder="Email"
                  className="w-full p-2 mb-3 border rounded"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
                <input
                  type="password"
                  placeholder="Mật khẩu"
                  className="w-full p-2 mb-3 border rounded"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <button type="submit" className="w-full bg-yellow-400 py-2 font-bold rounded">
                  ĐĂNG KÝ
                </button>
              </form>
            )}
          </>
        )}
      </div>
    </div>
  );
}
