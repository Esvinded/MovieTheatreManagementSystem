import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

// Các API vẫn dùng
export const getMovies = () => API.get("/public/movies");
export const getTheatresForMovie = (movieId) => API.get(`/public/theatres?movieId=${movieId}`);
export const getAvailableDates = (movieId, theatreId) =>
  API.get(`/public/available-dates?movieId=${movieId}&theatreId=${theatreId}`);
export const getShowtimes = (movieId, theatreId, date) =>
  API.get(`/public/showtimes?movieId=${movieId}&theatreId=${theatreId}&date=${date}`);
export const getSeatsByScreen = (screenId) => API.get(`/seats/screen/${screenId}`);


export const getSeats = (showtimeId) =>
  API.get(`/bookings/showtime/${showtimeId}/seats`);
export const createBooking = (payload) => API.post("/bookings", payload);
export const toggleSeat = (bookingId, seatId) => API.patch(`/bookings/${bookingId}/seat/${seatId}`);
export const confirmBooking = (bookingId) => API.post(`/bookings/${bookingId}/confirm`);
export const payBooking = (bookingId) => API.post(`/bookings/${bookingId}/pay`);
export const cancelBooking = (bookingId) => API.post(`/bookings/${bookingId}/cancel`);
export const getMyBookings = () => API.get("/bookings/my-bookings");
export const getCurrentBooking = (showtimeId) =>
  API.get(`/bookings/current?showtimeId=${showtimeId}`);

