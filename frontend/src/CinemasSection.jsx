import React, { useEffect, useState } from 'react';
import axios from 'axios';

const CinemasSection = () => {
  const [cinemas, setCinemas] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const stored = localStorage.getItem('user');
    const token = stored ? JSON.parse(stored).token : null;

    if (!token) {
      setError('Bạn chưa đăng nhập.');
      return;
    }

    axios
      .get('http://localhost:8080/api/public/theatres/all', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      })
      .then((res) => {
        if (!Array.isArray(res.data)) {
          throw new Error('Dữ liệu trả về không hợp lệ.');
        }
        setCinemas(res.data);
      })
      .catch((err) => {
        console.error('Lỗi khi lấy danh sách rạp:', err);
        setError(
          'Không thể lấy danh sách rạp. Có thể bạn chưa đăng nhập hoặc token đã hết hạn.'
        );
      });
  }, []);

  if (error) {
    return (
      <div className="bg-[#070F2B] text-red-500 p-4 min-h-screen">
        {error}
      </div>
    );
  }

  return (
    <div className="bg-[#070F2B] min-h-screen p-6 text-white">
      <h1 className="text-2xl font-bold text-yellow-400 mb-8 text-center">
        Danh sách rạp chiếu phim
      </h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cinemas.map((cinema) => (
          <div
            key={cinema.id}
            className="bg-[#1B1A55] border border-[#535C91] shadow-lg shadow-yellow-500/10 rounded-2xl overflow-hidden transition duration-300 transform hover:scale-105 hover:shadow-yellow-400/20 p-4"
          >
            <h2 className="text-xl font-bold text-yellow-400 mb-2 border-b border-yellow-400 pb-1 text-center">
              {cinema.name}
            </h2>
            <p className="text-sm text-[#9290C3] mb-1">📍 {cinema.address}</p>
            <p className="text-sm text-[#9290C3] mb-1">🖥️ Số phòng: {cinema.totalScreen}</p>
            <p
              className={`text-sm italic font-medium ${
                cinema.status === 'ACTIVE'
                  ? 'text-green-400'
                  : 'text-red-400'
              }`}
            >
              {cinema.status === 'ACTIVE' ? '🟢 Hoạt động' : '🔴 Bảo trì'}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CinemasSection;
