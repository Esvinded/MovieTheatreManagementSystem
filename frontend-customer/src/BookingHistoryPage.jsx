import React, { useEffect, useState } from "react";
import { getMyBookings, payBooking } from "./api";
import dayjs from "dayjs";

const BookingHistoryPage = () => {
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const res = await getMyBookings();
        console.log("Lịch sử đặt vé:", res.data);
        setBookings(res.data);
      } catch (err) {
        console.error("Lỗi khi tải lịch sử đặt vé:", err);
        alert("Không thể tải lịch sử đặt vé");
      }
    };
    fetchBookings();
  }, []);

  const handleContinuePayment = async (bookingId) => {
    if (!bookingId) return;
    try {
      const res = await payBooking(bookingId);
      window.location.href = res.data;
    } catch (err) {
      console.error("Lỗi thanh toán:", err);
      alert("Thanh toán thất bại!");
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0a0a23] text-white p-8">
      <h1 className="text-2xl font-bold text-yellow-400 mb-8 text-center">
        Lịch sử đặt vé
      </h1>

      {bookings.length === 0 ? (
        <p className="text-center text-gray-400">
          Bạn chưa có lịch sử đặt vé nào.
        </p>
      ) : (
        <div className="space-y-6 max-w-4xl mx-auto">
          {bookings.map((booking) => (
            <div
              key={booking.id}
              className="border border-gray-600 rounded p-4 bg-[#1a1a40] shadow"
            >
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">Phim:</span>{" "}
                {booking.movieTitle}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">Rạp:</span>{" "}
                {booking.theatreName}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">
                  Phòng chiếu:
                </span>{" "}
                {booking.screenName}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">
                  Suất chiếu:
                </span>{" "}
                {`${dayjs(booking.showtimeStartTime).format("HH:mm")} - ${dayjs(booking.showtimeEndTime).format("HH:mm")} - ${dayjs(booking.showtimeStartTime).format("DD/MM/YYYY")}`}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">Ghế:</span>{" "}
                {booking.seats?.map((seat) => seat.seatNumber).join(", ")}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">
                  Ngày đặt:
                </span>{" "}
                {dayjs(booking.bookingDate).format("DD/MM/YYYY HH:mm")}
              </div>
              <div className="mb-2">
                <span className="font-semibold text-yellow-300">
                  Trạng thái:
                </span>{" "}
                {booking.status === "CANCELLED" ? (
                  <span className="text-red-400">Đã hủy</span>
                ) : booking.status === "CONFIRMED" ? (
                  <span className="text-green-400">Đã xác nhận</span>
                ) : booking.status === "PAID" ? (
                  <span className="text-blue-400">Đã thanh toán</span>
                ) : booking.status === "AWAITING_PAYMENT" ? (
                  <span className="text-yellow-400">Chờ thanh toán</span>
                ) : (
                  <span className="text-gray-400">{booking.status}</span>
                )}
              </div>

              {booking.status === "AWAITING_PAYMENT" && (
                <div className="mt-4">
                  <button
                    onClick={() => handleContinuePayment(booking.id)}
                    className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded"
                  >
                    Tiếp tục thanh toán
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default BookingHistoryPage;
