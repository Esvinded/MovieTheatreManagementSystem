import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const MoviesSection = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

 useEffect(() => {
  axios
    .get('http://localhost:8080/api/public/movies')
    .then((res) => {
      if (!Array.isArray(res.data)) {
        throw new Error('Dữ liệu trả về không hợp lệ.');
      }
      setMovies(res.data);
    })
    .catch((err) => {
      console.error('Lỗi khi lấy danh sách phim:', err);
      setError('Không thể lấy danh sách phim.');
    });
}, []);


  const formatDuration = (durationStr) => {
    const match = durationStr.match(/PT(?:(\d+)H)?(?:(\d+)M)?/);
    const hours = parseInt(match?.[1] || '0', 10);
    const minutes = parseInt(match?.[2] || '0', 10);
    return `${hours} giờ ${minutes} phút`;
  };

  const formatStatus = (status) => {
    switch (status) {
      case 'ACTIVE':
        return 'Đang chiếu';
      case 'INACTIVE':
        return 'Ngưng chiếu';
      default:
        return status;
    }
  };

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
        Danh sách phim
      </h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {movies.map((movie) => (
          <div
            key={movie.id}
            className="cursor-pointer bg-[#1B1A55] border border-[#535C91] rounded-2xl overflow-hidden transition duration-300 transform hover:scale-105 hover:shadow-xl flex flex-col"
            onClick={() => navigate(`/booking?movieId=${movie.id}`)} // 👈 điều hướng sang trang đặt vé
          >
            <img
              src={movie.posterURL}
              alt={movie.title}
              className="w-full h-64 object-contain"
            />
            <div className="p-4 text-white flex flex-col flex-grow">
              <h2 className="text-lg font-semibold mb-1 text-yellow-400">{movie.title}</h2>
              <p className="text-sm text-[#9290C3] mb-1">
                {formatDuration(movie.duration)}
              </p>
              <p className="text-sm italic text-green-400 mb-2">
                {formatStatus(movie.status)}
              </p>
              <div
                className="text-sm text-[#9290C3] overflow-hidden max-h-[80px] hover:overflow-auto hover:max-h-[200px] transition-all duration-300 whitespace-pre-wrap"
                title={movie.description}
              >
                {movie.description}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MoviesSection;
