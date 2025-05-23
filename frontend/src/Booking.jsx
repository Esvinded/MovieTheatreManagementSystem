import React, { useEffect, useState } from "react";
import {
  getMovies,
  getTheatresForMovie,
  getAvailableDates,
  getShowtimes,
  getSeats,
  createBooking,
  confirmBooking,
  payBooking,
  cancelBooking,
  getSeatsByScreen
} from "./api";
import dayjs from "dayjs";
import { useLocation } from "react-router-dom";

const BookingPage = () => {
  const [movies, setMovies] = useState([]);
  const [selectedMovie, setSelectedMovie] = useState(null);

  const [theatres, setTheatres] = useState([]);
  const [selectedTheatre, setSelectedTheatre] = useState(null);

  const [availableDates, setAvailableDates] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);

  const [showtimes, setShowtimes] = useState([]);
  const [selectedShowtime, setSelectedShowtime] = useState(null);

  const [seats, setSeats] = useState([]);
  const [selectedSeatIds, setSelectedSeatIds] = useState([]);
  const [bookingId, setBookingId] = useState(null);

  const [isLoading, setIsLoading] = useState(false);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const preselectedMovieId = queryParams.get("movieId");


  const handleMovieSelect = async (movie) => {
    setSelectedMovie(movie);
    setSelectedTheatre(null);
    setSelectedDate(null);
    setSelectedShowtime(null);
    setSeats([]);
    setSelectedSeatIds([]);
    setBookingId(null);
    try {
      const res = await getTheatresForMovie(movie.id);
      setTheatres(res.data);
    } catch (err) {
      console.error(err);
      alert("Không thể tải danh sách rạp.");
    }
  };

  const handleTheatreSelect = async (theatre) => {
    setSelectedTheatre(theatre);
    setSelectedDate(null);
    setSelectedShowtime(null);
    setSeats([]);
    setSelectedSeatIds([]);
    setBookingId(null);
    try {
      const res = await getAvailableDates(selectedMovie?.id, theatre.id);
      setAvailableDates(res.data);
    } catch (err) {
      console.error(err);
      alert("Không thể tải danh sách ngày.");
    }
  };

  const handleDateSelect = async (date) => {
    setSelectedDate(date);
    setSelectedShowtime(null);
    setSeats([]);
    setSelectedSeatIds([]);
    setBookingId(null);
    try {
      const res = await getShowtimes(
        selectedMovie?.id,
        selectedTheatre?.id,
        date
      );
      setShowtimes(res.data);
    } catch (err) {
      console.error(err);
      alert("Không thể tải suất chiếu.");
    }
  };

const handleShowtimeSelect = async (showtime) => {
  setSelectedShowtime(showtime);
  setSeats([]);
  setSelectedSeatIds([]);
  setBookingId(null);

  try {
    const [allSeatsRes, bookedSeatsRes] = await Promise.all([
      getSeatsByScreen(showtime.screenId),
      getSeats(showtime.id),
    ]);
    console.log("Ghế đầy đủ:", allSeatsRes.data);
    console.log("Ghế đã đặt:", bookedSeatsRes.data);
    const allSeats = allSeatsRes.data;
    const bookedSeatIds = new Set(bookedSeatsRes.data.map((s) => s.seatId));

    const seatsWithStatus = allSeats.map((seat) => ({
      ...seat,
      status: bookedSeatIds.has(seat.id) ? "BOOKED" : "AVAILABLE",
    }));

    setSeats(seatsWithStatus);
    console.log("Ghế có status:", seatsWithStatus);
  } catch (err) {
    console.error("Lỗi khi tải ghế:", err);
    alert("Không thể tải ghế.");
  }
};

  const handleSeatClick = (seatId) => {
    if (bookingId) return;
    setSelectedSeatIds((prev) =>
      prev.includes(seatId)
        ? prev.filter((id) => id !== seatId)
        : [...prev, seatId]
    );
    setSeats((prev) =>
      prev.map((s) =>
        s.id === seatId
          ? {
              ...s,
              status: s.status === "SELECTED" ? "AVAILABLE" : "SELECTED",
            }
          : s
      )
    );
  };

  const handleConfirm = async () => {
    if (!selectedShowtime || selectedSeatIds.length === 0) {
      alert("Vui lòng chọn suất chiếu và ít nhất một ghế!");
      return;
    }

    try {
      setIsLoading(true);
      const res = await createBooking({
        showtimeId: selectedShowtime.id,
        seatIds: selectedSeatIds,
      });
      setBookingId(res.data.id);

      await confirmBooking(res.data.id);
      alert("Đã xác nhận đặt vé!");
    } catch (err) {
      console.error("Lỗi khi tạo booking:", err);
      alert("Đặt vé thất bại!");
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = async () => {
    if (!bookingId) return;
    const confirmed = window.confirm("Bạn có chắc chắn muốn hủy đặt vé?");
    if (!confirmed) return;

    try {
      setIsLoading(true);
      await cancelBooking(bookingId);
      alert("Đã hủy đặt vé!");
      setSelectedShowtime(null);
      setSeats([]);
      setBookingId(null);
    } catch (err) {
      console.error(err);
      alert("Hủy vé thất bại.");
    } finally {
      setIsLoading(false);
    }
  };

  const handlePayment = async () => {
    if (!bookingId) return;
    try {
      setIsLoading(true);
      const res = await payBooking(bookingId);
      window.location.href = res.data;
    } catch (err) {
      console.error("Lỗi thanh toán:", err);
      alert("Thanh toán thất bại!");
    } finally {
      setIsLoading(false);
    }
  };

 useEffect(() => {
  getMovies()
    .then((res) => {
      setMovies(res.data);
      if (preselectedMovieId) {
        const foundMovie = res.data.find(
          (m) => m.id.toString() === preselectedMovieId
        );
        if (foundMovie) {
          handleMovieSelect(foundMovie); // Gọi luôn logic chọn phim
        }
      }
    })
    .catch((err) => {
      console.error(err);
      alert("Không thể tải phim.");
    });
}, []);


  const getSeatClass = (status) => {
    switch (status) {
      case "BOOKED":
        return "bg-gray-500 text-white cursor-not-allowed";
      case "SELECTED":
        return "bg-blue-600 text-white";
      default:
        return "bg-emerald-500 text-white hover:bg-emerald-600";
    }
  };

  return (
    <div className="min-h-screen w-full px-4 md:px8 bg-[#0a0a23] p-8 mx-auto space-y-6 text-white">
      <h1 className="text-2xl text-yellow-400 mb-8 font-bold text-center">
        Đặt vé xem phim
      </h1>

      {/* Chọn phim */}
      <div>
        <label className="block mb-1">Chọn phim:</label>
        <div className="flex gap-2 flex-wrap">
          {movies.map((movie) => (
            <button
              key={movie.id}
              onClick={() => handleMovieSelect(movie)}
              className={`px-4 py-2 rounded border ${
                selectedMovie?.id === movie.id
                  ? "bg-blue-600"
                  : "bg-gray-700 hover:bg-gray-600"
              }`}
            >
              {movie.title}
            </button>
          ))}
        </div>
      </div>

      {/* Chọn rạp */}
      {selectedMovie && (
        <div>
          <label className="block mb-1">Chọn rạp:</label>
          <div className="flex gap-2 flex-wrap">
            {theatres.map((theatre) => (
              <button
                key={theatre.id}
                onClick={() => handleTheatreSelect(theatre)}
                className={`px-4 py-2 rounded border ${
                  selectedTheatre?.id === theatre.id
                    ? "bg-blue-600"
                    : "bg-gray-700 hover:bg-gray-600"
                }`}
              >
                {theatre.name}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Chọn ngày */}
      {selectedTheatre && (
        <div>
          <label className="block mb-1">Chọn ngày:</label>
          <div className="flex gap-2 flex-wrap">
            {availableDates.map((dateStr) => {
              const date = dayjs(dateStr).format("DD/MM/YYYY");
              return (
                <button
                  key={dateStr}
                  onClick={() => handleDateSelect(dateStr)}
                  className={`px-4 py-2 rounded border ${
                    selectedDate === dateStr
                      ? "bg-blue-600"
                      : "bg-gray-700 hover:bg-gray-600"
                  }`}
                >
                  {date}
                </button>
              );
            })}
          </div>
        </div>
      )}

      {/* Chọn suất chiếu */}
      {selectedDate && (
        <div>
          <label className="block mb-1">Chọn suất chiếu:</label>
          <div className="flex gap-2 flex-wrap">
            {showtimes.map((showtime) => (
              <button
                key={showtime.id}
                onClick={() => handleShowtimeSelect(showtime)}
                className={`px-4 py-2 rounded border ${
                  selectedShowtime?.id === showtime.id
                    ? "bg-blue-600"
                    : "bg-gray-700 hover:bg-gray-600"
                }`}
              >
                {dayjs(showtime.startTime).format("HH:mm")}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Chọn ghế */}
      {selectedShowtime && seats.length > 0 && (
        <div>
          <h2 className="text-xl font-semibold mb-2">Chọn ghế:</h2>
          <div className="space-y-2">
            {(() => {
              const rows = seats.reduce((acc, seat) => {
                if (!acc[seat.rowLabel]) acc[seat.rowLabel] = [];
                acc[seat.rowLabel].push(seat);
                return acc;
              }, {});
              const maxSeatsInRow = Math.max(
                ...Object.values(rows).map((row) => row.length)
              );
              return Object.entries(rows)
                .sort(([a], [b]) => a.localeCompare(b))
                .map(([rowLabel, rowSeats]) => {
                  const sortedRow = rowSeats.sort(
                    (a, b) => a.colNumber - b.colNumber
                  );
                  const missingSeats = maxSeatsInRow - sortedRow.length;
                  const marginLeft = (missingSeats * 22) / 2;

                  return (
                    <div key={rowLabel} className="flex items-center gap-2">
                      <span className="w-6 text-right">{rowLabel}</span>
                      <div
                        className="flex gap-2"
                        style={{ marginLeft: `${marginLeft}px` }}
                      >
                        {sortedRow.map((seat) => (
                          <button
                            key={seat.id}
                            disabled={seat.status === "BOOKED"}
                            onClick={() => handleSeatClick(seat.id)}
                            className={`text-xs p-2 rounded w-8 h-8 flex items-center justify-center ${getSeatClass(
                              seat.status
                            )}`}
                          >
                            {seat.colNumber}
                          </button>
                        ))}
                      </div>
                    </div>
                  );
                });
            })()}
          </div>

          {/* Chú thích */}
          <div className="mt-4 flex gap-4 text-sm">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-gray-500 rounded" />
              <span>Đã đặt</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-emerald-500 rounded" />
              <span>Còn trống</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-blue-600 rounded" />
              <span>Đang chọn</span>
            </div>
          </div>

          {/* Nút hành động */}
          <div className="mt-6 flex gap-4">
            <button
              onClick={handleConfirm}
              disabled={isLoading}
              className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded"
            >
              Xác nhận
            </button>
            <button
              onClick={handlePayment}
              disabled={!bookingId || isLoading}
              className="bg-emerald-500 hover:bg-emerald-600 text-white px-4 py-2 rounded"
            >
              Thanh toán
            </button>
            <button
              onClick={handleCancel}
              disabled={!bookingId || isLoading}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
            >
              Hủy đặt vé
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default BookingPage;
