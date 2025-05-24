import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  TextField,
  InputAdornment,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Tooltip,
  Divider,
  FormHelperText,
  Snackbar,
  Alert,
  Tab,
  Tabs,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";
import EventIcon from "@mui/icons-material/Event";
import MovieIcon from "@mui/icons-material/Movie";
import TheatersIcon from "@mui/icons-material/Theaters";
import ScreenShareIcon from "@mui/icons-material/ScreenShare";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import SaveIcon from "@mui/icons-material/Save";
import CancelIcon from "@mui/icons-material/Cancel";
import Layout from "../components/Layout";
import {
  showtimesAPI,
  moviesAPI,
  theatresAPI,
  screensAPI,
} from "../services/api";
// Thay thế import từ date-fns
const format = (date, formatStr) => {
  const d = new Date(date);
  // Định dạng đơn giản ngày/tháng/năm giờ:phút
  return `${d.getDate().toString().padStart(2, "0")}/${(d.getMonth() + 1).toString().padStart(2, "0")}/${d.getFullYear()} ${d.getHours().toString().padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`;
};

// TabPanel component để hiển thị nội dung của tab
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`showtime-tabpanel-${index}`}
      aria-labelledby={`showtime-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const ShowtimesManagement = () => {
  // State cho tabs
  const [tabValue, setTabValue] = useState(0);

  // State cho danh sách lịch chiếu
  const [loading, setLoading] = useState(true);
  const [showtimes, setShowtimes] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterMovie, setFilterMovie] = useState("");
  const [filterTheatre, setFilterTheatre] = useState("");
  const [movies, setMovies] = useState([]);
  const [theatres, setTheatres] = useState([]);

  // State cho tạo lịch chiếu mới
  const [formData, setFormData] = useState({
    movieId: "",
    theatreId: "",
    screenId: "",
    showDate: null,
    startTime: null,
  });
  const [errors, setErrors] = useState({});
  const [screens, setScreens] = useState([]);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [isEditing, setIsEditing] = useState(false);

  // Xử lý thay đổi tab
  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  // Lấy dữ liệu từ API
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        console.log("Bắt đầu tải dữ liệu...");
        
        // Lấy danh sách phim
        try {
          const moviesData = await moviesAPI.getAllMovies();
          console.log("Đã tải phim từ backend:", moviesData);
          setMovies(moviesData || []);

          // Lấy danh sách rạp chiếu
          const theatresData = await theatresAPI.getAllTheatres();
          console.log("Đã tải rạp từ backend:", theatresData);
          setTheatres(theatresData || []);

          // Tải lịch chiếu cho tất cả các phim
          if (moviesData && moviesData.length > 0) {
            console.log("Bắt đầu tải lịch chiếu cho tất cả phim");
            try {
              let allShowtimes = [];
              
              // Lấy lịch chiếu từ tất cả các phim
              for (const movie of moviesData) {
                try {
                  console.log(`Đang tải lịch chiếu cho phim ID: ${movie.id}`);
                  const movieShowtimes = await showtimesAPI.getShowtimesByMovie(movie.id);
                  if (movieShowtimes && Array.isArray(movieShowtimes) && movieShowtimes.length > 0) {
                    console.log(`Tìm thấy ${movieShowtimes.length} lịch chiếu cho phim ${movie.id}`);
                    allShowtimes = [...allShowtimes, ...movieShowtimes];
                  }
                } catch (movieError) {
                  console.error(`Lỗi khi tải lịch chiếu cho phim ${movie.id}:`, movieError);
                }
              }
              
              console.log(`Tổng số lịch chiếu đã tải: ${allShowtimes.length}`);
              if (allShowtimes.length > 0) {
                await processShowtimes(allShowtimes, moviesData, theatresData);
              } else {
                console.log("Không tìm thấy lịch chiếu nào cho tất cả phim");
                setShowtimes([]);
              }
            } catch (showtimeError) {
              console.error("Lỗi khi tải lịch chiếu:", showtimeError);
              setShowtimes([]);
              setSnackbarMessage("Không thể tải dữ liệu lịch chiếu. Vui lòng thử lại sau.");
              setSnackbarSeverity("error");
              setOpenSnackbar(true);
            }
          } else {
            setShowtimes([]);
            console.log("Không tìm thấy phim nào để tải lịch chiếu");
            setSnackbarMessage("Không tìm thấy phim nào để tải lịch chiếu.");
            setSnackbarSeverity("info");
            setOpenSnackbar(true);
          }
        } catch (error) {
          console.error("Không thể kết nối đến backend:", error);
          setMovies([]);
          setTheatres([]);
          setShowtimes([]);
          setSnackbarMessage("Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng và thử lại sau.");
          setSnackbarSeverity("error");
          setOpenSnackbar(true);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        setShowtimes([]);
        setSnackbarMessage("Không thể tải dữ liệu. Vui lòng thử lại sau.");
        setSnackbarSeverity("error");
        setOpenSnackbar(true);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Cập nhật danh sách phòng chiếu khi chọn rạp trong form tạo mới
  useEffect(() => {
    if (formData.theatreId) {
      const fetchScreens = async () => {
        try {
          // Gọi API để lấy danh sách phòng chiếu theo rạp
          const screensData = await screensAPI.getScreensByTheatre(
            parseInt(formData.theatreId),
          );
          setScreens(screensData);
        } catch (error) {
          console.error("Error fetching screens:", error);
          setSnackbarSeverity("error");
          setSnackbarMessage(
            "Không thể tải danh sách phòng chiếu. Vui lòng thử lại sau.",
          );
          setOpenSnackbar(true);
          setScreens([]);
        }
      };

      fetchScreens();
    } else {
      setScreens([]);
    }
  }, [formData.theatreId]);

  // Tính toán thời gian kết thúc dựa trên phim và thời gian bắt đầu
  const getEndTime = () => {
    if (!formData.movieId || !formData.startTime) return null;

    const movie = movies.find((m) => m.id === parseInt(formData.movieId));
    if (!movie) return null;

    // Xử lý duration từ định dạng ISO-8601 Duration
    let durationMinutes = 0;

    if (typeof movie.duration === "string" && movie.duration.startsWith("PT")) {
      // Parse ISO-8601 Duration (PT1H30M)
      const hourMatch = movie.duration.match(/(\d+)H/);
      const minuteMatch = movie.duration.match(/(\d+)M/);

      const hours = hourMatch ? parseInt(hourMatch[1]) : 0;
      const minutes = minuteMatch ? parseInt(minuteMatch[1]) : 0;

      durationMinutes = hours * 60 + minutes;
    } else if (typeof movie.duration === "number") {
      // Nếu đã được chuyac�n đổi thành số, dùng trực tiếp
      durationMinutes = movie.duration;
    } else {
      // Mặc định 90 phút nếu không xác định được
      durationMinutes = 90;
    }

    // Tạo thời gian bắt đầu và thêm duration
    const fullDateTime = new Date(`${formData.showDate}T${formData.startTime}`);
    const endTime = new Date(fullDateTime.getTime() + durationMinutes * 60000);

    // Trả về định dạng giờ:phút
    return `${endTime.getHours().toString().padStart(2, "0")}:${endTime.getMinutes().toString().padStart(2, "0")}`;
  };

  // Xử lý tìm kiếm và lọc
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleFilterMovieChange = (e) => {
    setFilterMovie(e.target.value);
  };

  const handleFilterTheatreChange = (e) => {
    setFilterTheatre(e.target.value);
  };

  // Lọc danh sách lịch chiếu
  const filteredShowtimes = showtimes.filter((showtime) => {
    if (!showtime) return false;
    
    // Kiểm tra tính hợp lệ của các trường
    const movieTitleMatch = showtime.movieTitle ? 
      showtime.movieTitle.toLowerCase().includes(searchTerm.toLowerCase()) : false;
    const theatreNameMatch = showtime.theatreName ? 
      showtime.theatreName.toLowerCase().includes(searchTerm.toLowerCase()) : false;
    const screenNameMatch = showtime.screenName ? 
      showtime.screenName.toLowerCase().includes(searchTerm.toLowerCase()) : false;
    
    const matchesSearch = 
      movieTitleMatch || theatreNameMatch || screenNameMatch || !searchTerm;

    const matchesMovie =
      !filterMovie || (showtime.movieId && showtime.movieId === parseInt(filterMovie));
    const matchesTheatre =
      !filterTheatre || (showtime.theatreId && showtime.theatreId === parseInt(filterTheatre));

    return matchesSearch && matchesMovie && matchesTheatre;
  });

  // Xử lý thay đổi input trong form tạo lịch chiếu mới
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });

    // Xóa lỗi khi người dùng nhập lại
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: undefined,
      });
    }
  };

  // Xử lý thay đổi ngày
  const handleDateChange = (date) => {
    setFormData({
      ...formData,
      showDate: date,
    });

    if (errors.showDate) {
      setErrors({
        ...errors,
        showDate: undefined,
      });
    }
  };

  // Xử lý thay đổi giờ
  const handleTimeChange = (time) => {
    setFormData({
      ...formData,
      startTime: time,
    });

    if (errors.startTime) {
      setErrors({
        ...errors,
        startTime: undefined,
      });
    }
  };

  // Xác thực form
  const validateForm = () => {
    const newErrors = {};

    if (!formData.movieId) newErrors.movieId = "Vui lòng chọn phim";
    if (!formData.theatreId) newErrors.theatreId = "Vui lòng chọn rạp";
    if (!formData.screenId) newErrors.screenId = "Vui lòng chọn phòng chiếu";
    if (!formData.showDate) newErrors.showDate = "Vui lòng chọn ngày chiếu";
    if (!formData.startTime) newErrors.startTime = "Vui lòng chọn giờ bắt đầu";
    
    // Kiểm tra tính hợp lệ của ngày tháng
    if (formData.showDate) {
     
      const [year, month, day] = formData.showDate.split('-').map(num => parseInt(num, 10));
      const inputDate = new Date(year, month - 1, day);
      
      // Kiểm tra ngày có hợp lệ không
      if (
        inputDate.getFullYear() !== year || 
        inputDate.getMonth() + 1 !== month || 
        inputDate.getDate() !== day
      ) {
        newErrors.showDate = "Ngày không hợp lệ (ví dụ: 31/04 không tồn tại)";
      } else {
        // Kiểm tra ngày có phải là quá khứ không
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        inputDate.setHours(0, 0, 0, 0);
        
        if (inputDate < today) {
          newErrors.showDate = "Không thể tạo lịch chiếu cho ngày quá khứ";
        }
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Xử lý submit form tạo/cập nhật lịch chiếu
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    setSubmitLoading(true);

    try {
      // Xác định thời gian kết thúc từ thời gian bắt đầu và thời lượng phim
      const movie = movies.find((m) => m.id === parseInt(formData.movieId));
      const duration = movie ? movie.duration : 120; // Mặc định 2 giờ

      // Tạo đối tượng Date từ showDate và startTime
      const dateStr = formData.showDate;
      const timeStr = formData.startTime;
      
      // Tạo đối tượng Date đầy đủ với giờ và ngày
      console.log(`Tạo date từ: ${dateStr}T${timeStr}`);
      const startDateTime = new Date(`${dateStr}T${timeStr}`);
      console.log("startDateTime:", startDateTime);

      // Tính thời gian kết thúc (thêm duration phút)
      let durationMinutes = 0;
      if (typeof duration === "string" && duration.startsWith("PT")) {
        // Xử lý định dạng ISO Duration như "PT1H30M"
        const hourMatch = duration.match(/(\d+)H/);
        const minuteMatch = duration.match(/(\d+)M/);

        const hours = hourMatch ? parseInt(hourMatch[1]) : 0;
        const minutes = minuteMatch ? parseInt(minuteMatch[1]) : 0;

        durationMinutes = hours * 60 + minutes;
        console.log(`Phân tích thời lượng phim: ${hours} giờ ${minutes} phút = ${durationMinutes} phút`);
      } else {
        // Dùng duration trực tiếp nếu là số
        durationMinutes = parseInt(duration) || 90; // Mặc định 90 phút nếu không xác định được
        console.log(`Thời lượng phim dạng số: ${durationMinutes} phút`);
      }

      // Tính thời gian kết thúc
      const endDateTime = new Date(
        startDateTime.getTime() + durationMinutes * 60000,
      );
      console.log("endDateTime:", endDateTime);

      // Tạo chuỗi thời gian chính xác theo định dạng ISO-8601 standard
      // Sử dụng 3 định dạng khác nhau để thử nghiệm
      const formatOption1 = (dateObj) => {
        return dateObj.toISOString(); // Standard ISO format with Z: 2025-05-21T09:30:00.000Z
      };
      
      const formatOption2 = (dateObj) => {
        const year = dateObj.getFullYear();
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getDate()).padStart(2, '0');
        const hours = String(dateObj.getHours()).padStart(2, '0');
        const minutes = String(dateObj.getMinutes()).padStart(2, '0');
        const seconds = String(dateObj.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}+00:00`;
      };
      
      const formatOption3 = (dateObj) => {
        const year = dateObj.getFullYear();
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getDate()).padStart(2, '0');
        const hours = String(dateObj.getHours()).padStart(2, '0');
        const minutes = String(dateObj.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}:00Z`;
      };
      
      // Sử dụng định dạng chuẩn ISO
      const startTimeISO = formatOption1(startDateTime);
      const endTimeISO = formatOption1(endDateTime);
      
      // Lưu các định dạng khác để thử nghiệm
      const startTimeOption2 = formatOption2(startDateTime);
      const endTimeOption2 = formatOption2(endDateTime);
      const startTimeOption3 = formatOption3(startDateTime);
      const endTimeOption3 = formatOption3(endDateTime);
      
      console.log("Định dạng 1 (ISO standard):");
      console.log("startTimeISO:", startTimeISO);
      console.log("endTimeISO:", endTimeISO);
      console.log("Định dạng 2 (with +00:00):");
      console.log("startTimeOption2:", startTimeOption2);
      console.log("endTimeOption2:", endTimeOption2);
      console.log("Định dạng 3 (simplified with Z):");
      console.log("startTimeOption3:", startTimeOption3);
      console.log("endTimeOption3:", endTimeOption3);
      
      console.log("startTimeISO:", startTimeISO);
      console.log("endTimeISO:", endTimeISO);

      if (isEditing && formData.id) {
        // Trường hợp cập nhật lịch chiếu - chỉ gửi startTime và endTime theo ShowtimeUpdateRequest
        const updateData = {
          startTime: startTimeISO,
          endTime: endTimeISO
        };

        // Gọi API cập nhật lịch chiếu
        await showtimesAPI.updateShowtime(formData.id, updateData);
        
        // Tải lại danh sách lịch chiếu từ server sau khi cập nhật
        if (movies.length > 0) {
          const updatedShowtimes = await showtimesAPI.getShowtimesByMovie(movies[0].id);
          setShowtimes(updatedShowtimes || []);
        }
        
        setSnackbarMessage("Cập nhật lịch chiếu thành công!");
      } else {
        // Trường hợp tạo mới lịch chiếu
        const showtimeData = {
          movieId: parseInt(formData.movieId),
          screenId: parseInt(formData.screenId),
          startTime: startTimeISO,
          endTime: endTimeISO,
        };

        // Gọi API để tạo lịch chiếu mới
        await showtimesAPI.createShowtime(showtimeData);
        
        // Tải lại danh sách lịch chiếu từ server để đảm bảo dữ liệu được cập nhật đầy đủ
        try {
          console.log("Đang tải lại danh sách lịch chiếu sau khi thêm mới...");
          // Lấy lịch chiếu của phim vừa thêm
          const updatedShowtimes = await showtimesAPI.getShowtimesByMovie(parseInt(formData.movieId));
          console.log("Dữ liệu lịch chiếu mới nhận được:", updatedShowtimes);
          
          if (updatedShowtimes && Array.isArray(updatedShowtimes)) {
            // Thêm lịch chiếu mới vào đầu danh sách hiện tại
            await processShowtimes(updatedShowtimes, movies, theatres);
          } else {
            console.log("Không nhận được dữ liệu lịch chiếu sau khi thêm");
            
            // Thêm lịch chiếu mới vào danh sách hiện tại
            const newShowtime = {
              id: Math.floor(Math.random() * 10000), // ID tạm thời
              movieId: parseInt(formData.movieId),
              screenId: parseInt(formData.screenId),
              startTime: `${formData.showDate}T${formData.startTime}:00+07:00`,
              endTime: endTimeISO
            };
            
            // Xử lý lịch chiếu mới và thêm vào danh sách
            const processedShowtimes = await processShowtimes([newShowtime], movies, theatres);
            setShowtimes(prev => {
              // Nếu đã có lịch chiếu trong danh sách, thêm vào
              if (prev && Array.isArray(prev) && prev.length > 0) {
                return [...processedShowtimes, ...prev];
              }
              return processedShowtimes;
            });
          }
        } catch (error) {
          console.error("Lỗi khi tải lại danh sách lịch chiếu:", error);
          setSnackbarMessage("Thêm lịch chiếu thành công, nhưng không thể tải lại danh sách mới.");
          setSnackbarSeverity("warning");
        }
        
        setSnackbarMessage("Thêm lịch chiếu thành công!");
      }

      setSnackbarSeverity("success");
      setOpenSnackbar(true);

      // Reset form và trạng thái chỉnh sửa
      setFormData({
        id: null,
        movieId: "",
        theatreId: "",
        screenId: "",
        showDate: null,
        startTime: null,
      });
      setIsEditing(false);

      // Tải lại danh sách lịch chiếu và chuyển tab
      try {
        // Tải lịch chiếu mới từ server cho tất cả các phim để đảm bảo nhìn thấy tất cả lịch chiếu
        console.log("Đang tải lại dữ liệu lịch chiếu sau khi thêm mới...");
        let allShowtimes = [];
        
        // Lấy lịch chiếu từ tất cả các phim
        for (const movie of movies) {
          try {
            console.log(`Đang tải lịch chiếu cho phim ID: ${movie.id}`);
            const movieShowtimes = await showtimesAPI.getShowtimesByMovie(movie.id);
            if (movieShowtimes && Array.isArray(movieShowtimes) && movieShowtimes.length > 0) {
              console.log(`Tìm thấy ${movieShowtimes.length} lịch chiếu cho phim ${movie.id}`);
              allShowtimes = [...allShowtimes, ...movieShowtimes];
            }
          } catch (error) {
            console.error(`Lỗi khi tải lịch chiếu cho phim ${movie.id}:`, error);
          }
        }
        
        console.log(`Tổng số lịch chiếu đã tải: ${allShowtimes.length}`);
        if (allShowtimes.length > 0) {
          await processShowtimes(allShowtimes, movies, theatres);
        } else {
          console.log("Không tìm thấy lịch chiếu nào sau khi thêm mới!");
        }
      } catch (error) {
        console.error("Lỗi khi tải lại dữ liệu lịch chiếu:", error);
      }
      
      setTabValue(0); // Chuyển về tab danh sách
    } catch (error) {
      console.error("Error creating showtime:", error);
      setSnackbarMessage(
        "Có lỗi xảy ra: " +
          (error.response?.data?.message || "Không thể tạo lịch chiếu"),
      );
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setSubmitLoading(false);
    }
  };

  // Hàm xử lý dữ liệu lịch chiếu
  const processShowtimes = async (showtimesData, moviesData = movies, theatresData = theatres, providedScreens = null) => {
    try {
      console.log("Bắt đầu xử lý dữ liệu lịch chiếu", showtimesData);
      
      if (!showtimesData) {
        console.log("Không có dữ liệu lịch chiếu");
        setShowtimes([]);
        return [];
      }
      
      // Chuyển đổi thành mảng nếu không phải mảng
      const dataArray = Array.isArray(showtimesData) ? showtimesData : [showtimesData];
      
      if (dataArray.length === 0) {
        console.log("Danh sách lịch chiếu trống");
        setShowtimes([]);
        return [];
      }
      
      // Lấy tất cả screens để map với showtime
      const allScreens = providedScreens || await screensAPI.getAllScreens();
      console.log("Đã tải phòng chiếu:", allScreens);

      // Xử lý từng lịch chiếu
      const processedShowtimes = dataArray
        .map((showtime) => {
          try {
            console.log("Đang xử lý lịch chiếu:", showtime);
            
            // Kiểm tra tính hợp lệ của dữ liệu
            if (!showtime) {
              console.log("Lịch chiếu không hợp lệ");
              return null;
            }
            
            const id = showtime.id || Math.floor(Math.random() * 1000000);
            const movieId = showtime.movieId || (moviesData.length > 0 ? moviesData[0].id : 1);
            const screenId = showtime.screenId || (allScreens.length > 0 ? allScreens[0].id : 1);
            
            // Xử lý thời gian bắt đầu và kết thúc
            let startDateTime, endDateTime;
            try {
              startDateTime = new Date(showtime.startTime);
              endDateTime = showtime.endTime ? new Date(showtime.endTime) : new Date(startDateTime.getTime() + 120 * 60000);
            } catch (e) {
              console.log("Lỗi xử lý thời gian, sử dụng thời gian hiện tại:", e);
              startDateTime = new Date();
              endDateTime = new Date(startDateTime.getTime() + 120 * 60000);
            }

            // Tìm thông tin phim
            const movie = moviesData.find((m) => m.id === movieId) || {};
            const movieTitle = movie.title || `Phim ID: ${movieId}`;

            // Tạo đối tượng lịch chiếu cơ bản
            const processedShowtime = {
              id: id,
              movieId: movieId,
              movieTitle: movieTitle,
              screenId: screenId,
              startTime: format(startDateTime, "HH:mm"),
              endTime: format(endDateTime, "HH:mm"),
              showDate: format(startDateTime, "yyyy-MM-dd"),
              screenName: "",
              theatreId: null,
              theatreName: "",
            };

            // Tìm thông tin phòng chiếu
            const screen = allScreens.find(s => s.id === screenId);
            if (screen) {
              processedShowtime.screenName = screen.name || `Phòng ${screenId}`;
              processedShowtime.theatreId = screen.theatreId;

              // Tìm thông tin rạp từ danh sách theatres đã lấy
              if (screen.theatreId) {
                const theatre = theatresData.find(t => t.id === screen.theatreId);
                if (theatre) {
                  processedShowtime.theatreName = theatre.name;
                } else {
                  console.log("Không tìm thấy thông tin rạp cho ID:", screen.theatreId);
                  // Sử dụng rạp đầu tiên nếu không tìm thấy
                  if (theatresData.length > 0) {
                    processedShowtime.theatreName = theatresData[0].name;
                    processedShowtime.theatreId = theatresData[0].id;
                  }
                }
              }
            } else {
              console.log("Không tìm thấy thông tin phòng chiếu cho ID:", screenId);
              // Sử dụng phòng đầu tiên nếu không tìm thấy
              if (allScreens.length > 0) {
                processedShowtime.screenName = allScreens[0].name;
                processedShowtime.theatreId = allScreens[0].theatreId;
                
                // Cập nhật tên rạp
                if (allScreens[0].theatreId) {
                  const theatre = theatresData.find(t => t.id === allScreens[0].theatreId);
                  if (theatre) {
                    processedShowtime.theatreName = theatre.name;
                  }
                }
              }
            }

            console.log("Đã xử lý lịch chiếu:", processedShowtime);
            return processedShowtime;
          } catch (error) {
            console.error("Lỗi khi xử lý lịch chiếu:", error, showtime);
            return null;
          }
        })
        .filter(Boolean);

      console.log("Hoàn thành xử lý dữ liệu lịch chiếu:", processedShowtimes);
      setShowtimes(processedShowtimes);
      return processedShowtimes;
    } catch (error) {
      console.error("Lỗi khi xử lý dữ liệu lịch chiếu:", error);
      setLoading(false);
    }
  };

  // Đóng snackbar
  const handleCloseSnackbar = () => {
    setOpenSnackbar(false);
  };

  // Xử lý chỉnh sửa lịch chiếu
  const handleEditShowtime = (showtime) => {
    // Chuyển đến tab tạo mới (sử dụng cùng form)
    setTabValue(1);

    // Nạp dữ liệu lịch chiếu cần chỉnh sửa vào form
    setFormData({
      id: showtime.id,
      movieId: showtime.movieId.toString(),
      theatreId: showtime.theatreId ? showtime.theatreId.toString() : "",
      screenId: showtime.screenId.toString(),
      showDate: showtime.showDate,
      startTime: showtime.startTime,
    });

    // Cập nhật tiêu đề tab
    setIsEditing(true);
  };



  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        {/* Tabs */}
        <Box sx={{ borderBottom: 1, borderColor: "divider", mb: 3 }}>
          <Tabs
            value={tabValue}
            onChange={handleTabChange}
            aria-label="showtime management tabs"
          >
            <Tab label="Danh sách lịch chiếu" />
            <Tab label={isEditing ? "Chỉnh sửa lịch chiếu" : "Tạo lịch chiếu mới"} />
          </Tabs>
        </Box>

        {/* Tab Danh sách lịch chiếu */}
        <TabPanel value={tabValue} index={0}>
          <Typography variant="h4" component="h1" gutterBottom>
            Quản lý lịch chiếu
          </Typography>

          {/* Công cụ tìm kiếm và lọc */}
          <Paper sx={{ p: 2, mb: 2 }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Tìm kiếm"
                  variant="outlined"
                  value={searchTerm}
                  onChange={handleSearchChange}
                  placeholder="Tìm theo tên phim, rạp, phòng..."
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <SearchIcon />
                      </InputAdornment>
                    ),
                  }}
                />
              </Grid>

              <Grid item xs={12} md={3}>
                <FormControl fullWidth variant="outlined">
                  <InputLabel id="filter-movie-label">Lọc theo phim</InputLabel>
                  <Select
                    labelId="filter-movie-label"
                    value={filterMovie}
                    onChange={handleFilterMovieChange}
                    label="Lọc theo phim"
                  >
                    <MenuItem value="">Tất cả phim</MenuItem>
                    {movies.map((movie) => (
                      <MenuItem key={movie.id} value={movie.id}>
                        {movie.title}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} md={3}>
                <FormControl fullWidth variant="outlined">
                  <InputLabel id="filter-theatre-label">
                    Lọc theo rạp
                  </InputLabel>
                  <Select
                    labelId="filter-theatre-label"
                    value={filterTheatre}
                    onChange={handleFilterTheatreChange}
                    label="Lọc theo rạp"
                  >
                    <MenuItem value="">Tất cả rạp</MenuItem>
                    {theatres.map((theatre) => (
                      <MenuItem key={theatre.id} value={theatre.id}>
                        {theatre.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} md={2}>
                <Button
                  variant="contained"
                  color="primary"
                  fullWidth
                  startIcon={<AddIcon />}
                  onClick={() => setTabValue(1)} // Chuyển sang tab tạo mới
                >
                  Thêm mới
                </Button>
              </Grid>
            </Grid>
          </Paper>

          {/* Bảng danh sách lịch chiếu */}
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Phim</TableCell>
                  <TableCell>Ngày chiếu</TableCell>
                  <TableCell>Giờ bắt đầu</TableCell>
                  <TableCell>Giờ kết thúc</TableCell>
                  <TableCell>Rạp</TableCell>
                  <TableCell>Phòng</TableCell>
                  <TableCell align="right">Thao tác</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loading ? (
                  <TableRow>
                    <TableCell colSpan={8} align="center">
                      Đang tải dữ liệu...
                    </TableCell>
                  </TableRow>
                ) : filteredShowtimes.length > 0 ? (
                  filteredShowtimes.map((showtime) => (
                    <TableRow key={showtime.id}>
                      <TableCell>{showtime.id}</TableCell>
                      <TableCell>{showtime.movieTitle}</TableCell>
                      <TableCell>
                        <Chip
                          icon={<EventIcon />}
                          label={showtime.showDate}
                          size="small"
                          color="default"
                        />
                      </TableCell>
                      <TableCell>{showtime.startTime}</TableCell>
                      <TableCell>{showtime.endTime}</TableCell>
                      <TableCell>{showtime.theatreName}</TableCell>
                      <TableCell>{showtime.screenName}</TableCell>
                      <TableCell align="right">
                        <Tooltip title="Chỉnh sửa lịch chiếu">
                          <span>
                            <IconButton 
                              color="primary"
                              onClick={() => handleEditShowtime(showtime)}
                            >
                              <EditIcon />
                            </IconButton>
                          </span>
                        </Tooltip>

                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={8} align="center">
                      <Box sx={{ textAlign: 'center', p: 3 }}>
                        <Typography variant="body1" color="text.secondary" gutterBottom>
                          Không tìm thấy suất chiếu nào
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                          Bạn có thể tạo lịch chiếu mới bằng cách nhấn vào nút "Thêm mới" hoặc tab "Tạo lịch chiếu mới"
                        </Typography>
                        <Button
                          variant="contained"
                          color="primary"
                          startIcon={<AddIcon />}
                          onClick={() => setTabValue(1)}
                        >
                          Tạo lịch chiếu mới
                        </Button>
                      </Box>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        {/* Tab Tạo lịch chiếu mới */}
        <TabPanel value={tabValue} index={1}>
          <Typography variant="h4" component="h1" gutterBottom sx={{ mb: 3 }}>
            Tạo lịch chiếu mới
          </Typography>

          {/* Form */}
          <Paper sx={{ p: 3 }}>
            <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                {/* Phim */}
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth error={Boolean(errors.movieId)}>
                    <InputLabel id="movie-label">Phim</InputLabel>
                    <Select
                      labelId="movie-label"
                      name="movieId"
                      value={formData.movieId}
                      onChange={handleChange}
                      label="Phim"
                      startAdornment={
                        <InputAdornment position="start">
                          <MovieIcon />
                        </InputAdornment>
                      }
                      disabled={loading}
                    >
                      {movies.map((movie) => (
                        <MenuItem key={movie.id} value={movie.id}>
                          {movie.title}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.movieId && (
                      <FormHelperText>{errors.movieId}</FormHelperText>
                    )}
                  </FormControl>
                </Grid>

                {/* Rạp */}
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth error={Boolean(errors.theatreId)}>
                    <InputLabel id="theatre-label">Rạp chiếu</InputLabel>
                    <Select
                      labelId="theatre-label"
                      name="theatreId"
                      value={formData.theatreId}
                      onChange={handleChange}
                      label="Rạp chiếu"
                      startAdornment={
                        <InputAdornment position="start">
                          <TheatersIcon />
                        </InputAdornment>
                      }
                      disabled={loading}
                    >
                      {theatres.map((theatre) => (
                        <MenuItem key={theatre.id} value={theatre.id}>
                          {theatre.name}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.theatreId && (
                      <FormHelperText>{errors.theatreId}</FormHelperText>
                    )}
                  </FormControl>
                </Grid>

                {/* Phòng chiếu */}
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth error={Boolean(errors.screenId)}>
                    <InputLabel id="screen-label">Phòng chiếu</InputLabel>
                    <Select
                      labelId="screen-label"
                      name="screenId"
                      value={formData.screenId}
                      onChange={handleChange}
                      label="Phòng chiếu"
                      startAdornment={
                        <InputAdornment position="start">
                          <ScreenShareIcon />
                        </InputAdornment>
                      }
                      disabled={!formData.theatreId || loading}
                    >
                      {screens.map((screen) => (
                        <MenuItem key={screen.id} value={screen.id}>
                          {screen.name || `Phòng ${screen.screenNumber}`}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.screenId && (
                      <FormHelperText>{errors.screenId}</FormHelperText>
                    )}
                    {!formData.theatreId && (
                      <FormHelperText>Vui lòng chọn rạp trước</FormHelperText>
                    )}
                  </FormControl>
                </Grid>

                {/* Ngày chiếu */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Ngày chiếu"
                    type="date"
                    value={formData.showDate || ""}
                    onChange={(e) => handleDateChange(e.target.value)}
                    error={Boolean(errors.showDate)}
                    helperText={errors.showDate}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <AccessTimeIcon />
                        </InputAdornment>
                      ),
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                    disabled={loading}
                  />
                </Grid>

                {/* Giờ bắt đầu */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Giờ bắt đầu"
                    type="time"
                    value={formData.startTime || ""}
                    onChange={(e) => handleTimeChange(e.target.value)}
                    error={Boolean(errors.startTime)}
                    helperText={errors.startTime}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <AccessTimeIcon />
                        </InputAdornment>
                      ),
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                    inputProps={{
                      step: 300, // 5 phút
                    }}
                    disabled={loading}
                  />
                </Grid>

                {/* Độ dài phim và giờ kết thúc */}
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Thông tin lịch chiếu"
                    value={(() => {
                      // Nếu chưa chọn phim hoặc giờ bắt đầu
                      if (!formData.movieId || !formData.startTime) {
                        return "Chọn phim và giờ bắt đầu";
                      }
                      
                      // Tìm phim đã chọn
                      const movie = movies.find(m => m.id === parseInt(formData.movieId));
                      if (!movie) return "Chọn phim";
                      
                      // Giờ bắt đầu đã chọn
                      const startTime = formData.startTime;
                      
                      // Xử lý và hiển thị độ dài phim theo chuẩn
                      let durationDisplay = "";
                      let durationMinutes = 0;
                      
                      if (typeof movie.duration === "string" && movie.duration.startsWith("PT")) {
                        // Parse ISO-8601 Duration (PT1H30M)
                        const hourMatch = movie.duration.match(/(\d+)H/);
                        const minuteMatch = movie.duration.match(/(\d+)M/);
                        
                        const hours = hourMatch ? parseInt(hourMatch[1]) : 0;
                        const minutes = minuteMatch ? parseInt(minuteMatch[1]) : 0;
                        
                        durationMinutes = hours * 60 + minutes;
                        
                        if (hours > 0) {
                          durationDisplay = `${hours}h ${minutes}m`;
                        } else {
                          durationDisplay = `${minutes}m`;
                        }
                      } else if (typeof movie.duration === "number") {
                        // Nếu là số phút
                        durationMinutes = movie.duration;
                        const hours = Math.floor(durationMinutes / 60);
                        const minutes = durationMinutes % 60;
                        
                        if (hours > 0) {
                          durationDisplay = `${hours}h ${minutes}m`;
                        } else {
                          durationDisplay = `${minutes}m`;
                        }
                      } else {
                        // Mặc định
                        durationDisplay = "~90m";
                        durationMinutes = 90;
                      }
                      
                      // Tính giờ kết thúc
                      const endTime = getEndTime();
                      
                      // Chuyển đổi giờ bắt đầu sang định dạng AM/PM
                      const startHour = parseInt(startTime.split(':')[0]);
                      const startMinute = startTime.split(':')[1];
                      const startAmPm = startHour >= 12 ? 'PM' : 'AM';
                      const start12Hour = startHour % 12 || 12; // Chuyển 0 thành 12
                      const startTimeFormatted = `${start12Hour}:${startMinute} ${startAmPm}`;
                      
                      // Chuyển đổi giờ kết thúc sang định dạng AM/PM
                      const endHour = parseInt(endTime.split(':')[0]);
                      const endMinute = endTime.split(':')[1];
                      const endAmPm = endHour >= 12 ? 'PM' : 'AM';
                      const end12Hour = endHour % 12 || 12; // Chuyển 0 thành 12
                      const endTimeFormatted = `${end12Hour}:${endMinute} ${endAmPm}`;
                      
                      // Hiển thị giống như trong ảnh mẫu nhưng với AM/PM
                      return `Giờ bắt đầu: ${startTimeFormatted} • Độ dài: ${durationDisplay} • Kết thúc lúc: ${endTimeFormatted}`;
                    })()}
                    InputProps={{
                      readOnly: true,
                      startAdornment: (
                        <InputAdornment position="start">
                          <AccessTimeIcon />
                        </InputAdornment>
                      ),
                    }}
                    disabled
                  />
                </Grid>
              </Grid>

              <Divider sx={{ my: 3 }} />

              {/* Nút submit */}
              <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2 }}>
                <Button
                  variant="outlined"
                  color="error"
                  startIcon={<CancelIcon />}
                  onClick={() => setTabValue(0)} // Quay lại tab danh sách
                  disabled={submitLoading}
                >
                  Hủy
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  startIcon={<SaveIcon />}
                  disabled={submitLoading || loading}
                >
                  {submitLoading ? "Đang lưu..." : "Lưu lịch chiếu"}
                </Button>
              </Box>
            </Box>
          </Paper>
        </TabPanel>

        {/* Snackbar thông báo */}
        <Snackbar
          open={openSnackbar}
          autoHideDuration={6000}
          onClose={handleCloseSnackbar}
          anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
        >
          <Alert
            onClose={handleCloseSnackbar}
            severity={snackbarSeverity}
            sx={{ width: "100%" }}
          >
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Box>
    </Layout>
  );
};

export default ShowtimesManagement;
