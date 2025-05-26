import React, { useState } from "react";
import Banner from "./Banner"; // Import Banner
import MoviesSection from "./MoviesSection"; // Import MoviesSection
import CinemasSection from "./CinemasSection"; // Import CinemasSection
import PricesSection from "./PricesSection"; // Import PriceSection
import Footer from "./Footer";
// Dữ liệu Banner
const banners = [
  {
    image: "public/price.png",
    alt: "Price",
  },
  {
    image: "public/giamgia.png",
    alt: "HUST",
  },
  {
    image: "public/pop.png",
    alt: "POP",
  },
];

const HomePage = () => {
  return (
    <div className="bg-[#0a0a23] min-h-screen text-white font-sans">

      {/* Banner */}
      <Banner banners={banners} />

      {/* Các phần chính */}
      <MoviesSection />
      <CinemasSection />
      <PricesSection />
    </div>
  );
};

export default HomePage;
