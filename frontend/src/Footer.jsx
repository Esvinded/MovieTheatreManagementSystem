import React from 'react';
import {
  FaFacebook,
  FaYoutube,
  FaEnvelope,
  FaPhone,
  FaMapMarkerAlt,
} from 'react-icons/fa';

const Footer = () => {
  return (
    <footer className="bg-[#070F2B] border-t border-[#535C91] py-6 text-white text-sm mt-auto">
      <div className="max-w-6xl mx-auto px-4 grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Thông tin thương hiệu */}
        <div>
          <h2 className="text-lg font-bold text-yellow-400 mb-2">HUST Cinema</h2>
          <p className="text-[#9290C3]">
            Rạp chiếu phim hiện đại dành cho sinh viên và gia đình. Trải nghiệm điện ảnh đỉnh cao ngay tại trung tâm thủ đô.
          </p>
        </div>

        {/* Thông tin liên hệ */}
        <div>
          <h3 className="text-md font-semibold text-yellow-400 mb-2">Liên hệ</h3>
          <ul className="space-y-2 text-[#9290C3]">
            <li className="flex items-center gap-2">
              <FaMapMarkerAlt className="text-yellow-400" />
              Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội
            </li>
            <li className="flex items-center gap-2">
              <FaPhone className="text-yellow-400" />
              0123 456 789
            </li>
            <li className="flex items-center gap-2">
              <FaEnvelope className="text-yellow-400" />
              support@hustcinema.com
            </li>
          </ul>
        </div>

        {/* Mạng xã hội */}
        <div className="text-center md:text-left">
          <h3 className="text-md font-semibold text-yellow-400 mb-2">Kết nối với chúng tôi</h3>
          <div className="flex justify-center md:justify-start space-x-6 text-yellow-400 text-xl">
            <a
              href="https://facebook.com"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-white transition"
            >
              <FaFacebook />
            </a>
            <a
              href="https://youtube.com"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-white transition"
            >
              <FaYoutube />
            </a>
            <a
              href="mailto:support@hustcinema.com"
              className="hover:text-white transition"
            >
              <FaEnvelope />
            </a>
          </div>
        </div>
      </div>

      <div className="mt-6 text-center text-[#9290C3] text-xs">
        &copy; 2025 HUST Cinema. All rights reserved.
      </div>
    </footer>
  );
};

export default Footer;
