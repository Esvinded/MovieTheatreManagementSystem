import React, { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

const Navbar = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleLogout = async () => {
    await logout();
    navigate("/auth-user");
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <nav className="flex justify-between items-center px-6 py-4 bg-[#0f0f2f] sticky top-0 z-50">
      <div className="flex items-center gap-8">
        <div
          className="text-3xl font-bold text-yellow-400 cursor-pointer"
          onClick={() => navigate("/home-page")}
        >
          HUST CINEMA
        </div>
        <div className="flex items-center gap-6 text-lg font-semibold text-white">
          <button onClick={() => navigate("/movies")} className="hover:text-yellow-400 transition">Phim</button>
          <button onClick={() => navigate("/cinemas")} className="hover:text-yellow-400 transition">Rạp</button>
          <button onClick={() => navigate("/prices")} className="hover:text-yellow-400 transition">Giá Vé</button>
        </div>
      </div>

      <div className="flex items-center gap-8">
        <button
          onClick={() => navigate("/booking")}
          className="bg-gradient-to-r from-red-500 to-yellow-400 text-white px-6 py-2 rounded-full font-bold text-lg shadow-lg hover:scale-105 transition"
        >
          Đặt vé ngay
        </button>

        {user ? (
          <div className="relative" ref={dropdownRef}>
            <div
              className="flex items-center gap-2 cursor-pointer"
              onClick={() => setDropdownOpen(!dropdownOpen)}
            >
              <img
                src={user.profileImageUrl}
                alt="avatar"
                className="w-10 h-10 rounded-full object-cover border-2 border-yellow-400"
              />
              <span className="text-white hover:text-yellow-400 font-semibold">
                {user.username} ▾
              </span>
            </div>

            {dropdownOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-50">
                <button
                  onClick={() => {
                    navigate("/account");
                    setDropdownOpen(false);
                  }}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                >
                  Thông tin tài khoản
                </button>
                <button
                  onClick={() => {
                    navigate("/booking-history");
                    setDropdownOpen(false);
                  }}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                >
                  Lịch sử đặt vé
                </button>
                <button
                  onClick={() => {
                    navigate("/change-password");
                    setDropdownOpen(false);
                  }}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                >
                  Đổi mật khẩu
                </button>
                <button
                  onClick={handleLogout}
                  className="block w-full px-4 py-2 text-left text-red-600 hover:bg-gray-100"
                >
                  Đăng xuất
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate("/auth-user")}
              className="text-white hover:text-yellow-400 font-semibold"
            >
              Đăng nhập
            </button>
            <button
              onClick={() => navigate("/auth-user")}
              className="bg-yellow-400 text-black px-4 py-2 rounded-md font-semibold hover:bg-yellow-300 transition"
            >
              Đăng ký
            </button>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
