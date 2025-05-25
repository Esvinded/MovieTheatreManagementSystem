import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const VNPayResultPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [status, setStatus] = useState(null);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const responseCode = params.get('vnp_ResponseCode');

    if (responseCode === '00') {
      setStatus('success');
    } else {
      setStatus('fail');
    }

    // Tự động chuyển trang sau 5 giây
    const timeout = setTimeout(() => {
      navigate('/home-page');
    }, 5000);

    return () => clearTimeout(timeout);
  }, [location, navigate]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-[#0a0a23] text-white text-center p-6">
      {status === 'success' ? (
        <>
          <h1 className="text-3xl font-bold text-green-400">🎉 Thanh toán thành công!</h1>
          <p className="mt-4 text-lg">Cảm ơn bạn đã đặt vé. Chúc bạn xem phim vui vẻ!</p>
          <p className="mt-2 text-sm text-gray-400">Bạn sẽ được chuyển về trang chủ trong giây lát...</p>
        </>
      ) : status === 'fail' ? (
        <>
          <h1 className="text-3xl font-bold text-red-400">❌ Thanh toán thất bại</h1>
          <p className="mt-4 text-lg">Có lỗi xảy ra hoặc bạn đã hủy thanh toán. Vui lòng thử lại.</p>
          <p className="mt-2 text-sm text-gray-400">Bạn sẽ được chuyển về trang chủ trong giây lát...</p>
        </>
      ) : (
        <p className="text-lg">Đang xử lý kết quả thanh toán...</p>
      )}
    </div>
  );
};

export default VNPayResultPage;
