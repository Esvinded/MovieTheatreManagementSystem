import React, { useEffect, useState, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";
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
  getSeatsByScreen,
  getCurrentBooking,
} from "./api";
import dayjs from "dayjs";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
dayjs.extend(isSameOrAfter);
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

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

  const stompClientRef = useRef(null);

  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const preselectedMovieId = queryParams.get("movieId");

  const { user } = useAuth();
  const navigate = useNavigate();

  const connectWebSocket = (showtimeId) => {
    const socket = new SockJS("http://localhost:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("✅ STOMP connected");

        client.subscribe(`/topic/showtime/${showtimeId}/seats`, (message) => {
          // The backend now sends an array of BookingSeatDto with status and bookingId
          const updatedSeats = JSON.parse(message.body);
          setSeats(updatedSeats);
        });
      },
      onStompError: (frame) => {
        console.error("❌ STOMP error", frame);
      },
    });

    client.activate();
    stompClientRef.current = client;
  };

  const sendSeatUpdate = (showtimeId, seatId, action) => {
    if (!stompClientRef.current || !stompClientRef.current.connected) return;
    stompClientRef.current.publish({
      destination: `/app/seat/${action}`,
      body: JSON.stringify({
        showtimeId,
        seatId,
      }),
    });
  };

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
     if (!user) {
      alert("Vui lòng đăng nhập để đặt vé.");
      navigate("/auth-user");
      return;
    }
    setSelectedShowtime(showtime);
    setSeats([]);
    setSelectedSeatIds([]);
    setBookingId(null);

    try {
      // Fetch user's current booking for this showtime
      const bookingRes = await getCurrentBooking(showtime.id);
      const booking = bookingRes.data;
      const userSelectedSeatIds = booking.seats
        ? booking.seats.map((s) => s.seatId)
        : [];
      setBookingId(booking.id);
      setSelectedSeatIds(userSelectedSeatIds);

      // Fetch all seats and all reserved/booked seats for this showtime
      const [allSeatsRes, bookedSeatsRes] = await Promise.all([
        getSeatsByScreen(showtime.screenId),
        getSeats(showtime.id),
      ]);
      const allSeats = allSeatsRes.data;
      // Map of seatId to status (BOOKED, RESERVED, etc.) and bookingId
      const seatStatusMap = {};
      bookedSeatsRes.data.forEach((s) => {
        seatStatusMap[s.seatId] = { status: s.status, bookingId: s.bookingId };
      });
      // Compose seat list with correct status for rendering
      const seatsWithStatus = allSeats.map((seat) => {
        if (userSelectedSeatIds.includes(seat.id)) {
          // Seat is in the current user's booking (should be blue and editable)
          return { ...seat, status: "RESERVED", bookingId: booking.id };
        } else if (seatStatusMap[seat.id]?.status === "BOOKED") {
          // Booked by anyone (always gray)
          return { ...seat, status: "BOOKED", bookingId: seatStatusMap[seat.id]?.bookingId };
        } else if (
          seatStatusMap[seat.id]?.status === "RESERVED" &&
          seatStatusMap[seat.id]?.bookingId !== booking.id
        ) {
          // Reserved by another user's booking (gray)
          return { ...seat, status: "RESERVED", bookingId: seatStatusMap[seat.id]?.bookingId };
        } else {
          // Available
          return { ...seat, status: "AVAILABLE", bookingId: null };
        }
      });
      setSeats(seatsWithStatus);
      connectWebSocket(showtime.id);
    } catch (err) {
      console.error("Lỗi khi tải ghế:", err);
      alert("Không thể tải ghế.");
    }
  };

  const handleSeatClick = (seatId) => {
    const seat = seats.find((s) => s.id === seatId);
    // Only allow selection if seat is AVAILABLE or RESERVED by current user
    if (!seat || (seat.status !== "AVAILABLE" && !(seat.status === "RESERVED" && seat.bookingId === bookingId))) return;

    const isSelected = selectedSeatIds.includes(seatId);
    let newSelectedSeats;
    if (isSelected) {
      newSelectedSeats = selectedSeatIds.filter((id) => id !== seatId);
    } else {
      newSelectedSeats = [...selectedSeatIds, seatId];
    }
    setSelectedSeatIds(newSelectedSeats);
    setSeats((prev) =>
      prev.map((s) =>
        s.id === seatId
          ? { ...s, status: isSelected ? "AVAILABLE" : "RESERVED", bookingId: isSelected ? null : bookingId }
          : s
      )
    );
    sendSeatUpdate(
      selectedShowtime.id,
      seatId,
      isSelected ? "deselect" : "select"
    );
  };

const handleConfirm = async () => {
  if (!selectedShowtime || selectedSeatIds.length === 0) {
    alert("Vui lòng chọn suất chiếu và ít nhất một ghế!");
    return;
  }

  if (!bookingId) {
    alert("Không tìm thấy đơn đặt vé. Vui lòng chọn ghế trước!");
    return;
  }

  try {
    setIsLoading(true);
    const res = await confirmBooking(bookingId);
    console.log("Xác nhận booking thành công:", res.data);
    alert("Đã xác nhận đơn đặt vé. Vui lòng tiếp tục thanh toán!");
    // Optional: chuyển sang bước thanh toán nếu có
    // navigate(`/payment/${bookingId}`);
  } catch (err) {
    console.error("Lỗi khi xác nhận booking:", err);
    alert("Xác nhận thất bại!");
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
      setBookingId(null);
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
          if (foundMovie) handleMovieSelect(foundMovie);
        }
      })
      .catch((err) => {
        console.error(err);
        alert("Không thể tải phim.");
      });
  }, []);

  const getSeatClass = (seat) => {
    if (seat.status === "BOOKED") {
      return "bg-gray-500 text-white cursor-not-allowed";
    }
    if (seat.status === "RESERVED" && seat.bookingId !== bookingId) {
      return "bg-gray-500 text-white cursor-not-allowed";
    }
    if (seat.status === "RESERVED" && seat.bookingId === bookingId) {
      return "bg-blue-600 text-white";
    }
    // Available
    return "bg-emerald-500 text-white hover:bg-emerald-600";
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
            {availableDates.filter((dateStr) => dayjs(dateStr).isSameOrAfter(dayjs(), "day")).map((dateStr) => {
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
                            disabled={seat.status === "BOOKED" || (seat.status === "RESERVED" && seat.bookingId !== bookingId)}
                            onClick={() => handleSeatClick(seat.id)}
                            className={`text-xs p-2 rounded w-8 h-8 flex items-center justify-center ${getSeatClass(seat)}`}
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
              <span>Đã đặt/Đang giữ bởi người khác</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-emerald-500 rounded" />
              <span>Còn trống</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-blue-600 rounded" />
              <span>Đang chọn (của bạn)</span>
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
