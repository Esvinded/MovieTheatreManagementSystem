import React, { useState } from "react";
import axios from "axios";

const ChangePassword = () => {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert("Mật khẩu mới không khớp.");
      return;
    }

    try {
      const stored = JSON.parse(localStorage.getItem("user"));
      const token = stored?.token;

      await axios.post(
        "http://localhost:8080/api/auth/change-password",
        {
          oldPassword,
          newPassword,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          withCredentials: true,
        }
      );
      alert("Đổi mật khẩu thành công!");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (error) {
      console.error("Lỗi đổi mật khẩu:", error.response?.data || error.message);
      alert("Đổi mật khẩu thất bại. Hãy kiểm tra lại mật khẩu cũ.");
    }
  };

  return (
    <div className="bg-[#0a0a23] min-h-screen text-white font-sans flex flex-col items-center">
      <h2 className="text-3xl font-bold mt-12">Đổi mật khẩu</h2>

      <form
        onSubmit={handleChangePassword}
        className="mt-10 bg-[#1c1c3c] p-6 rounded-lg shadow-lg w-96"
      >
        <div className="mb-4">
          <label className="block text-lg font-medium">Mật khẩu cũ:</label>
          <input
            type="password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            required
            className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
          />
        </div>
        <div className="mb-4">
          <label className="block text-lg font-medium">Mật khẩu mới:</label>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
            className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
          />
        </div>
        <div className="mb-6">
          <label className="block text-lg font-medium">Xác nhận mật khẩu mới:</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
            className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
          />
        </div>
        <button
          type="submit"
          className="w-full bg-gradient-to-r from-red-500 to-yellow-400 text-white py-2 px-4 rounded-md font-semibold hover:scale-105 transition"
        >
          Đổi mật khẩu
        </button>
      </form>
    </div>
  );
};

export default ChangePassword;
