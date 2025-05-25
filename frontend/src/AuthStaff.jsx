import { useState } from "react";
import axios from "axios";

export default function AuthStaff() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [showResetPassword, setShowResetPassword] = useState(false);

  // Đăng nhập
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/api/auth/staff/login", {
        username,
        password,
      });
      console.log("Đăng nhập staff thành công:", response.data);
      
      // Lưu staff vào localStorage
      localStorage.setItem("staff", JSON.stringify(response.data));

      // Sau đăng nhập chuyển hướng về trang staff
      window.location.href = "/home-page-staff";
    } catch (error) {
      console.error("Lỗi đăng nhập staff:", error.response?.data || error.message);
      alert(error.response?.data || "Đăng nhập thất bại");
    }
  };

  // Reset Password
  const handleResetPassword = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/api/auth/forgot-password", {
        email,
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
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center" style={{ backgroundImage: "url('/background.jpg')" }}>
      <div className="w-full max-w-sm bg-white p-6 rounded shadow">
        <h2 className="text-center text-xl font-bold mb-4">Nhân Viên</h2>

        {/* Nếu đang ở màn reset password */}
        {showResetPassword ? (
          <form onSubmit={handleResetPassword} className="space-y-4">
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
        )}
      </div>
    </div>
  );
}
