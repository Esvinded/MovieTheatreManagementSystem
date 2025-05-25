import React, { useEffect, useState } from "react";
import { useAuth } from "./AuthContext";
import axios from "axios";

const AccountPage = () => {
  const { user, setUser } = useAuth();
  const [fullName, setFullName] = useState("");
  const [dob, setDob] = useState("");
  const [phone, setPhone] = useState("");
  const [inputUrl, setInputUrl] = useState("");

  const fetchProfile = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/customer/profile", {
        withCredentials: true,
      });
      const updatedUser = res.data;
      setUser(updatedUser);
      setFullName(updatedUser.fullName || "");
      setDob(updatedUser.dateOfBirth || "");
      setPhone(updatedUser.phoneNumber || "");
    } catch (err) {
      console.error("Lỗi khi lấy profile:", err);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      await axios.put(
        "http://localhost:8080/api/customer/profile",
        {
          fullName: fullName || "",
          phoneNumber: phone || "",
          dateOfBirth: dob || "",
        },
        { withCredentials: true }
      );
      await fetchProfile();
      alert("Thông tin đã được cập nhật!");
    } catch (error) {
      console.error("Lỗi cập nhật tài khoản:", error.response?.data || error.message);
      alert("Lỗi khi cập nhật thông tin.");
    }
  };

  const handleUpdatePicture = async (e) => {
    e.preventDefault();
    if (!inputUrl.trim()) {
      alert("Vui lòng nhập URL ảnh.");
      return;
    }

    try {
      const params = new URLSearchParams();
      params.append("profileImageUrl", inputUrl);

      await axios.put("http://localhost:8080/api/customer/profile/picture", params, {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        withCredentials: true,
      });

      await fetchProfile();
      setInputUrl("");
      alert("Ảnh đại diện đã được cập nhật!");
    } catch (error) {
      console.error("Lỗi cập nhật ảnh:", error.response?.data || error.message);
      alert("Lỗi khi cập nhật ảnh đại diện.");
    }
  };

  return (
    <div className="bg-[#0a0a23] min-h-screen text-white font-sans flex flex-col items-center">
      <h2 className="text-3xl font-bold mt-12">Thông tin tài khoản</h2>

      <div className="mt-8 flex justify-center space-x-12 flex-wrap">
        {/* Form cập nhật thông tin */}
        <form onSubmit={handleUpdate} className="bg-[#1c1c3c] p-6 rounded-lg shadow-lg w-96 mb-6">
          <div className="mb-4">
            <label className="block text-lg font-medium">Họ và tên:</label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Nhập họ và tên"
              required
              className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
            />
          </div>
          <div className="mb-4">
            <label className="block text-lg font-medium">Ngày sinh:</label>
            <input
              type="date"
              value={dob}
              onChange={(e) => setDob(e.target.value)}
              required
              className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
            />
          </div>
          <div className="mb-4">
            <label className="block text-lg font-medium">Số điện thoại:</label>
            <input
              type="text"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Nhập số điện thoại"
              required
              className="w-full p-3 mt-2 bg-[#2c2c54] text-white rounded-md"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-gradient-to-r from-red-500 to-yellow-400 text-white py-2 px-4 rounded-md font-semibold hover:scale-105 transition"
          >
            Cập nhật thông tin
          </button>
        </form>

        {/* Form cập nhật ảnh đại diện */}
        <div className="bg-[#1c1c3c] p-6 rounded-lg shadow-lg w-96 flex flex-col items-center mb-6">
          <h3 className="text-xl font-semibold mb-4">Ảnh đại diện</h3>
          {user?.profileImageUrl ? (
            <img
              src={`${user.profileImageUrl}?${Date.now()}`} // ép load ảnh mới
              alt="Ảnh đại diện"
              className="w-40 h-40 rounded-full object-cover border-2 border-yellow-400 mb-4"
            />
          ) : (
            <p className="mb-4">Chưa có ảnh đại diện</p>
          )}

          <form onSubmit={handleUpdatePicture} className="w-full space-y-3">
            <input
              type="text"
              placeholder="Nhập URL ảnh..."
              value={inputUrl}
              onChange={(e) => setInputUrl(e.target.value)}
              className="w-full p-3 text-black rounded-md"
            />
            <button
              type="submit"
              className="w-full bg-gradient-to-r from-blue-500 to-green-400 text-white py-2 px-4 rounded-md font-semibold hover:scale-105 transition"
            >
              Cập nhật ảnh đại diện
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AccountPage;
