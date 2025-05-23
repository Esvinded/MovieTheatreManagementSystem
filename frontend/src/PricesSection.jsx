import React from 'react';

const PricesSection = () => {
  return (
    <div className="bg-[#070F2B] text-white py-10 px-6 min-h-screen">
      <h1 className="text-2xl font-bold text-yellow-400 mb-8 text-center">
        Bảng giá vé
      </h1>
      <div className="max-w-2xl mx-auto bg-[#1B1A55] border border-[#535C91] rounded-2xl p-6 shadow-lg shadow-yellow-500/10">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="text-yellow-400 border-b border-yellow-400">
              <th className="py-2 px-4">Ngày</th>
              <th className="py-2 px-4">Đối tượng</th>
              <th className="py-2 px-4">Giá vé</th>
            </tr>
          </thead>
          <tbody>
            {[
              'Thứ 2',
              'Thứ 3',
              'Thứ 4',
              'Thứ 5',
              'Thứ 6',
              'Thứ 7',
              'Chủ nhật',
            ].map((day, index) => (
              <tr
                key={index}
                className="border-b border-[#535C91] hover:bg-[#535C91] transition"
              >
                <td className="py-2 px-4 text-[#9290C3]">{day}</td>
                <td className="py-2 px-4 text-[#9290C3]">
                  Tất cả (người lớn, trẻ em, người già)
                </td>
                <td className="py-2 px-4 text-green-400 font-semibold">100.000đ</td>
              </tr>
            ))}
          </tbody>
        </table>
        <p className="mt-4 text-[#9290C3] text-sm italic text-center">
          * Giá vé áp dụng đồng giá cho tất cả mọi người vào tất cả các ngày trong tuần.
        </p>
      </div>
    </div>
  );
};

export default PricesSection;
