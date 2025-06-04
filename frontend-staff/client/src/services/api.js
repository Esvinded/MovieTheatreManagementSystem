import axios from "axios";

// URL cơ sở API - thay đổi tại đây nếu backend chạy ở cổng khác
const API_BASE_URL = "http://localhost:8080";

// Cấu hình axios với headers mặc định
axios.defaults.headers.common["Content-Type"] = "application/json";
axios.defaults.headers.common["Accept"] = "application/json";
axios.defaults.withCredentials = true; // Đảm bảo gửi cookie cùng với yêu cầu
// Hàm tạo URL đầy đủ cho API endpoint
const getApiUrl = (endpoint) => `${API_BASE_URL}${endpoint}`;


export const authAPI = {
  // Staff login
  loginStaff: async (credentials) => {
    try {
      const response = await axios.post(getApiUrl("/api/auth/staff/login"), credentials);
      return response.data;
    } catch (error) {
      console.error("Staff login error:", error);
      // Sử dụng alert thông thường thay vì SweetAlert2
      alert(error.response?.data || "Đăng nhập thất bại");
      throw error;
    }
  },

  // Staff registration
  registerStaff: async (staffData) => {
    try {
      const response = await axios.post(getApiUrl("/api/auth/staff/register"), staffData);
      return response.data;
    } catch (error) {
      console.error("Staff registration error:", error);
      throw error;
    }
  },

  // Logout
  logout: async () => {
    try {
      const response = await axios.post(getApiUrl("/api/auth/logout"));
      return response.data;
    } catch (error) {
      console.error("Logout error:", error);
      throw error;
    }
  },

  // Forgot password
  forgotPassword: async (username) => {
    try {
      const response = await axios.post(getApiUrl("/api/auth/forgot-password"), { username });
      return response.data;
    } catch (error) {
      console.error("Forgot password error:", error);
      throw error;
    }
  },

  // Change password
  changePassword: async (oldPassword, newPassword) => {
    try {
      const response = await axios.post(getApiUrl("/api/auth/change-password"), {
        oldPassword,
        newPassword
      });
      return response.data;
    } catch (error) {
      console.error("Change password error:", error);
      throw error;
    }
  },


};

export const moviesAPI = {
  getAllMovies: async () => {
    const response = await axios.get("http://localhost:8080/api/movie/get");
    return response.data;
  },



  createMovie: async (movieData) => {
    try {
      // Tạo movieRequest theo định dạng MovieCreateRequest của backend
      const movieRequest = {
        title: movieData.title,
        duration: movieData.duration,
        PosterURL: movieData.PosterURL || "",
        description: movieData.description || "",
      };

      // Không cần chuyển đổi file sang base64 nữa
      // Backend chỉ nhận URL hình ảnh dưới dạng chuỗi

      console.log("Gửi dữ liệu phim mới đến backend:", {
        title: movieRequest.title,
        duration: movieRequest.duration,
        posterURL: movieRequest.PosterURL ? "URL đã được đặt" : "Không có URL",
        description: movieRequest.description
      });

      const response = await axios.post("http://localhost:8080/api/movie/set", movieRequest);
      return response.data;
    } catch (error) {
      console.error("Error creating movie:", error);
      throw error;
    }
  },

  updateMovie: async (id, movieData) => {
    const processedData = { ...movieData };

    if (processedData.posterFile && processedData.posterFile instanceof File) {
      try {
        const base64URL = await new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = () => resolve(reader.result);
          reader.onerror = reject;
          reader.readAsDataURL(processedData.posterFile);
        });

        processedData.PosterURL = base64URL;
      } catch (error) {
        console.error("Error converting file to Base64:", error);
      }
    }

    const movieRequest = {
      title: processedData.title,
      PosterURL: processedData.PosterURL || "",
      status: processedData.status || "ACTIVE",
      duration: processedData.duration
        ? `PT${Math.floor(parseInt(processedData.duration) / 60)}H${parseInt(processedData.duration) % 60}M`
        : "PT0H", 
      description: processedData.description || "",
    };

    if (processedData.posterFile) {
      delete processedData.posterFile;
    }

    try {
      const response = await axios.put(
        `http://localhost:8080/api/movie/update/${id}`,
        movieRequest,
      );
      return response.data;
    } catch (error) {
      console.error(`Error updating movie ${id}:`, error);
      throw error;
    }
  },

  deleteMovie: async (id) => {
    await axios.delete(`http://localhost:8080/api/movie/delete/${id}`);
  },
};

// Theatres API
export const theatresAPI = {
  getAllTheatres: async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/theatres/get");
      return response.data;
    } catch (error) {
      console.error("Error fetching theatres:", error);
      throw error;
    }
  },

  getTheatreById: async (id) => {
    try {
      // Lấy tất cả rạp rồi tìm rạp phù hợp theo ID
      const response = await axios.get("http://localhost:8080/api/theatres/get");
      const theatres = response.data;
      const theatre = theatres.find(t => t.id === id);
      return theatre || null;
    } catch (error) {
      console.error(`Error fetching theatre with ID ${id}:`, error);
      throw error;
    }
  },


  createTheatre: async (theatreData) => {
    try {
      // Tạo đúng định dạng TheatreCreateRequest theo yêu cầu của backend
      const theatreDto = {
        name: theatreData.name,
        address: theatreData.address,
        totalScreen: 1 // Phải >= 1 theo ràng buộc @Min(value = 1) ở backend
      };

      // Log để debug
      console.log('Gửi dữ liệu rạp chiếu:', theatreDto);
      
      // Gọi API để tạo rạp chiếu mới
      const response = await axios.post("http://localhost:8080/api/theatres/set", theatreDto);
      console.log('Kết quả tạo rạp chiếu:', response.data);
      return response.data;
    } catch (error) {
      console.error("Error creating theatre:", error);
      // Log chi tiết thông tin lỗi nếu có
      if (error.response) {
        console.error("Response data:", error.response.data);
        console.error("Response status:", error.response.status);
        console.error("Response headers:", error.response.headers);
      }
      throw error;
    }
  },

  updateTheatre: async (id, theatreData) => {
    try {
      // TheatreUpdateRequest chỉ cần name và address
      const theatreDto = {
        name: theatreData.name,
        address: theatreData.address
      };

      const response = await axios.put(`http://localhost:8080/api/theatres/update/${id}`, theatreDto);
      return response.data;
    } catch (error) {
      console.error(`Error updating theatre ${id}:`, error);
      throw error;
    }
  },
};

// Screens API
export const screensAPI = {
  getAllScreens: async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/screens/get");
      return response.data;
    } catch (error) {
      console.error("Error fetching screens:", error);
      throw error;
    }
  },



  getScreensByTheatre: async (theatreId) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/screens/get/theatre/${theatreId}`,
      );
      return response.data;
    } catch (error) {
      console.error(`Error fetching screens for theatre ${theatreId}:`, error);
      throw error;
    }
  },

  getScreenSeats: async (screenId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/seats/screen/${screenId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching seats for screen ${screenId}:`, error);
      throw error;
    }
  },

  createScreen: async (screenData) => {
    try {
      const screenCreateRequest = {
        theatreId: screenData.theatreId,
        name: screenData.name,
        capacity: screenData.capacity || 100,
      };

      const response = await axios.post("http://localhost:8080/api/screens/set", screenCreateRequest);
      return response.data;
    } catch (error) {
      console.error("Error creating screen:", error);
      throw error;
    }
  },

  updateScreen: async (id, screenData) => {
    try {
      // ScreenUpdateRequest 
      const screenUpdateRequest = {
        name: screenData.name,
        status: screenData.status
      };

      const response = await axios.put(`http://localhost:8080/api/screens/update/${id}`, screenUpdateRequest);
      return response.data;
    } catch (error) {
      console.error(`Error updating screen ${id}:`, error);
      throw error;
    }
  },

  deleteScreen: async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/screens/delete/${id}`);
    } catch (error) {
      console.error(`Error deleting screen ${id}:`, error);
      throw error;
    }
  },
};

// Seats API
export const seatsAPI = {
  getAllSeats: async () => {
    const response = await axios.get("http://localhost:8080/api/seats/get");
    return response.data;
  },

  getSeatById: async (id) => {
    const response = await axios.get(`http://localhost:8080/api/seats/${id}`);
    return response.data;
  },

  getSeatsByScreen: async (screenId) => {
    const response = await axios.get(`http://localhost:8080/api/seats/screen/${screenId}`);
    return response.data;
  },

  updateSeat: async (id, seatData) => {
    // SeatUpdateRequest 
    const updateRequest = {
      status: seatData.status || "ACTIVE"
    };

    try {
      const response = await axios.put(
        `http://localhost:8080/api/seats/update/${id}`,
        updateRequest,
      );
      return response.data;
    } catch (error) {
      console.error(`Error updating seat ${id}:`, error);
      throw error;
    }
  }
};

// Showtimes API
export const showtimesAPI = {


  // Lấy lịch chiếu theo phim
  getShowtimesByMovie: async (movieId) => {
    try {
      console.log(`Đang gọi API lấy lịch chiếu cho phim ID: ${movieId}`);
      
      // Log URL đầy đủ để kiểm tra
      const url = `http://localhost:8080/api/showtimes/movie/${movieId}`;
      console.log(`URL API: ${url}`);
      
      // Thử kết nối đến API 
      const response = await axios.get(url);
      console.log(`Kết quả API lịch chiếu cho phim ${movieId}:`, response.data);
      
      // Đảm bảo kết quả là một mảng
      const resultArray = Array.isArray(response.data) ? response.data : [response.data].filter(Boolean);
      console.log(`Đã chuyển đổi kết quả thành mảng có ${resultArray.length} phần tử`);
      
      return resultArray;
    } catch (error) {
      console.error(`Error fetching showtimes for movie ${movieId}:`, error);
      console.error('Chi tiết lỗi:', error.message);
      if (error.response) {
        console.error('Response status:', error.response.status);
        console.error('Response data:', error.response.data);
      }
      // Trả về mảng rỗng nếu có lỗi để tránh crash ứng dụng
      return [];
    }
  },

  // Lấy lịch chiếu theo màn hình - sử dụng axios trực tiếp
  getShowtimesByScreen: async (screenId) => {
    try {
      // Endpoint /api/showtimes/screen/{screenId} có trong bảng
      const response = await axios.get(`http://localhost:8080/api/showtimes/screen/${screenId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching showtimes for screen ${screenId}:`, error);
      throw error;
    }
  },

  createShowtime: async (showtimeData) => {
    console.log('Dữ liệu đầu vào:', showtimeData);
    
    // Tạo ShowtimeCreateRequest đúng theo DTO backend với OffsetDateTime
    const createRequest = {
      movieId: parseInt(showtimeData.movieId),
      screenId: parseInt(showtimeData.screenId),
      startTime: showtimeData.startTime,
      endTime: showtimeData.endTime
    };

    console.log('Đang tạo lịch chiếu mới với dữ liệu:', JSON.stringify(createRequest, null, 2));

    try {
      const url = "http://localhost:8080/api/showtimes/set";
      console.log(`Gọi API tạo lịch chiếu: ${url}`);
      console.log('Dữ liệu gửi đi (chi tiết):');
      console.log('movieId:', createRequest.movieId, 'kiểu:', typeof createRequest.movieId);
      console.log('screenId:', createRequest.screenId, 'kiểu:', typeof createRequest.screenId);
      console.log('startTime:', createRequest.startTime, 'kiểu:', typeof createRequest.startTime);
      console.log('endTime:', createRequest.endTime, 'kiểu:', typeof createRequest.endTime);
      
      // Thử gửi với một định dạng khác
      const backupRequest = {
        ...createRequest,
        startTime: createRequest.startTime.replace('+00:00', 'Z'),
        endTime: createRequest.endTime.replace('+00:00', 'Z')
      };
      
      console.log('Thử gửi với định dạng thay thế:', backupRequest);
      
      try {
        const response = await axios.post(url, createRequest);
        console.log('Kết quả tạo lịch chiếu:', response.data);
        
        // Sau khi tạo thành công, kiểm tra và xác nhận dữ liệu
        try {
          console.log(`Kiểm tra lịch chiếu sau khi tạo cho phim ID: ${showtimeData.movieId}`);
          const checkResponse = await axios.get(`http://localhost:8080/api/showtimes/movie/${showtimeData.movieId}`);
          console.log('Lịch chiếu hiện tại của phim:', checkResponse.data);
        } catch (checkError) {
          console.error('Không thể kiểm tra lịch chiếu sau khi tạo:', checkError);
        }
        
        return response.data;
      } catch (initialError) {
        console.error('Lỗi với định dạng ban đầu:', initialError.message);
        console.log('Thử lại với định dạng thay thế...');
        try {
          const backupResponse = await axios.post(url, backupRequest);
          console.log('Kết quả với định dạng thay thế:', backupResponse.data);
          return backupResponse.data;
        } catch (backupError) {
          console.error('Cả hai cách định dạng đều thất bại:', backupError.message);
          throw initialError; // Ném lỗi ban đầu
        }
      }
      
      // Dòng sau đây sẽ không bao giờ được thực thi vì mỗi nhánh try/catch đều có return riêng
    } catch (error) {
      console.error("Error creating showtime:", error);
      console.error('Chi tiết lỗi:', error.message);
      if (error.response) {
        console.error('Response status:', error.response.status);
        console.error('Response data:', error.response.data);
      }
      throw error;
    }
  },

  // Cập nhật lịch chiếu theo ShowtimeUpdateRequest DTO
  updateShowtime: async (id, showtimeData) => {
    try {
      // ShowtimeUpdateRequest chỉ cho phép cập nhật các trường thời gian
      const updateRequest = {
        startTime: showtimeData.startTime,
        endTime: showtimeData.endTime
      };

      const response = await axios.put(
        `http://localhost:8080/api/showtimes/update/${id}`,
        updateRequest
      );
      return response.data;
    } catch (error) {
      console.error(`Error updating showtime ${id}:`, error);
      throw error;
    }
  },
};

// Dashboard API - Sử dụng các endpoint hiện có để lấy dữ liệu cho dashboard
export const dashboardAPI = {
  getSummary: async () => {
    try {
      // Lấy dữ liệu từ các API endpoints 
      const [moviesResponse, theatresResponse, screensResponse] = await Promise.all([
        axios.get("http://localhost:8080/api/movie/get"),
        axios.get("http://localhost:8080/api/theatres/get"),
        axios.get("http://localhost:8080/api/screens/get")
      ]);

      const movies = moviesResponse.data;
      const theatres = theatresResponse.data;
      const screens = screensResponse.data;

      // Khởi tạo tổng số lịch chiếu
      let totalShowtimes = 0;
      
      // Lấy tổng số lịch chiếu từ tất cả các phim
      if (movies && movies.length > 0) {
        try {
          // Lấy lịch chiếu từ phim đầu tiên
          const firstMovieShowtimes = await axios.get(`http://localhost:8080/api/showtimes/movie/${movies[0].id}`);
          totalShowtimes = firstMovieShowtimes.data.length || 0;
          
          // Nếu có nhiều phim, lấy thêm lịch chiếu từ một số phim khác
          const movieLimit = Math.min(movies.length, 5);
          for (let i = 1; i < movieLimit; i++) {
            try {
              const response = await axios.get(`http://localhost:8080/api/showtimes/movie/${movies[i].id}`);
              totalShowtimes += response.data.length || 0;
            } catch (error) {
              console.error(`Error fetching showtimes for movie ${movies[i].id}:`, error);
            }
          }
        } catch (error) {
          console.error('Error fetching showtimes:', error);
        }
      }
      
      // Tính toán các thống kê bao gồm cả showtimes
      const summary = {
        totalMovies: movies.length,
        totalTheatres: theatres.length,
        totalScreens: screens.length,
        totalShowtimes: totalShowtimes
      };

      return summary;
    } catch (error) {
      console.error("Error fetching dashboard data:", error);
      throw error;
    }
  },
};

// Helper functions
export const formatShowtimeDate = (date) => {
  // Hỗ trợ định dạng ngày giờ cho showtime
  if (!date) return "";

  const d = new Date(date);
  const day = d.getDate().toString().padStart(2, "0");
  const month = (d.getMonth() + 1).toString().padStart(2, "0");
  const year = d.getFullYear();
  const hours = d.getHours().toString().padStart(2, "0");
  const minutes = d.getMinutes().toString().padStart(2, "0");

  return `${day}/${month}/${year} ${hours}:${minutes}`;
};