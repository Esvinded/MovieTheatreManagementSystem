import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./HomePage";
import MovieBooking from "./Booking";
import PricesSection from "./PricesSection";
import CinemasSection from "./CinemasSection";
import MoviesSection from "./MoviesSection";
import Navbar from "./NavBar";
import AuthUser from "./AuthUser";
import AccountPage from "./AccountPage";
import { AuthProvider } from "./AuthContext"; // ✅ Import AuthProvider
import ChangePassword from "./ChangePassword";
import Footer from "./Footer";
import BookingHistoryPage from "./BookingHistoryPage";
import VNPayReturnHandler from "./vnpayReturn"; 

function App() {
  return (
    <AuthProvider> {/* ✅ Bọc toàn bộ ứng dụng */}
      <Router>
        <Navbar />
        <Routes>
          <Route path="/home-page" element={<HomePage />} />
          <Route path="/movies" element={<MoviesSection />} />
          <Route path="/cinemas" element={<CinemasSection />} />
          <Route path="/prices" element={<PricesSection />} />
          <Route path="/booking" element={<MovieBooking />} />
          <Route path="/auth-user" element={<AuthUser />} />
          <Route path="/account" element={<AccountPage />} />
          <Route path="/change-password" element={<ChangePassword />} />
          <Route path="/booking-history" element={<BookingHistoryPage />} />
          <Route path="/vnpay-result" element={<VNPayReturnHandler/>} />
        </Routes>
        <Footer />
      </Router>
    </AuthProvider>
  );
}

export default App;
