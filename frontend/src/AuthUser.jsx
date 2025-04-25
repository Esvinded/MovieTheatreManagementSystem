import { useState } from "react";
import axios from "axios";

export default function AuthUser() {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/api/auth/customer/login", {
        username,
        password,
      });
      console.log("Đăng nhập thành công:", response.data);
      // TODO: Lưu token, chuyển hướng, v.v.
    } catch (error) {
      console.error("Lỗi đăng nhập:", error.response?.data || error.message);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/api/auth/customer/register", {
        username,
        email,
        password,
      });
      console.log("Đăng ký thành công:", response.data);
      // TODO: Chuyển sang trang đăng nhập hoặc thông báo
    } catch (error) {
      console.error("Lỗi đăng ký:", error.response?.data || error.message);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center" style={{ backgroundImage: "url('/background.jpg')" }}>
      <div className="w-full max-w-sm bg-white p-6 rounded shadow">
      <h2 className="text-center text-xl font-bold mb-4">Khách Hàng</h2>
        <div className="flex mb-6 border-b">
          <button className={`flex-1 py-2 font-bold ${isLogin ? "border-b-2 border-black" : ""}`} onClick={() => setIsLogin(true)}>Đăng Nhập</button>
          <button className={`flex-1 py-2 font-bold ${!isLogin ? "border-b-2 border-black" : ""}`} onClick={() => setIsLogin(false)}>Đăng Ký</button>
        </div>

        {isLogin ? (
          <form onSubmit={handleLogin} className="space-y-4">
            <input type="text" placeholder="Tên đăng nhập" value={username} onChange={(e) => setUsername(e.target.value)} className="w-full p-2 border rounded" required />
            <input type="password" placeholder="Mật khẩu" value={password} onChange={(e) => setPassword(e.target.value)} className="w-full p-2 border rounded" required />
            <button type="submit" className="w-full bg-yellow-400 py-2 font-bold rounded">ĐĂNG NHẬP</button>
          </form>
        ) : (
          <form onSubmit={handleRegister} className="space-y-4">
            <input type="text" placeholder="Tên đăng nhập" value={username} onChange={(e) => setUsername(e.target.value)} className="w-full p-2 border rounded" required />
            <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} className="w-full p-2 border rounded" required />
            <input type="password" placeholder="Mật khẩu" value={password} onChange={(e) => setPassword(e.target.value)} className="w-full p-2 border rounded" required />
            <button type="submit" className="w-full bg-yellow-400 py-2 font-bold rounded">ĐĂNG KÝ</button>
          </form>
        )}
      </div>
    </div>
  );
}
