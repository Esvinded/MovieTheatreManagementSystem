import React, { useState, useEffect } from 'react';
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
  CircularProgress,
  FormControl,
  Tooltip
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import UploadIcon from '@mui/icons-material/Upload';
import ImageIcon from '@mui/icons-material/Image';
import Layout from '../components/Layout';
import { moviesAPI } from '../services/api';

const format = (date, formatStr) => {
  const d = new Date(date);

  return `${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
};

// Chuyển thành số phút để hiển thị trong frontend
const parseDuration = (isoDuration) => {
  if (!isoDuration || typeof isoDuration !== 'string') return 0;

  // Xử lý định dạng ISO 8601 duration từ Java như "PT2H30M"
  const hourMatch = isoDuration.match(/(\d+)H/);
  const minuteMatch = isoDuration.match(/(\d+)M/);

  const hours = hourMatch ? parseInt(hourMatch[1], 10) : 0;
  const minutes = minuteMatch ? parseInt(minuteMatch[1], 10) : 0;

  return hours * 60 + minutes;
};

const MoviesManagement = () => {
  // State
  const [loading, setLoading] = useState(true);
  const [movies, setMovies] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const [openPosterDialog, setOpenPosterDialog] = useState(false);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [posterFile, setPosterFile] = useState(null);
  const [posterPreview, setPosterPreview] = useState('');
  const [posterUploadLoading, setPosterUploadLoading] = useState(false);
  const [saving, setSaving] = useState(false);


  const defaultMovieState = {
    title: '',
    duration: '',
    status: 'ACTIVE', 
    PosterURL: '',
    description: ''
  };
  const [movieForm, setMovieForm] = useState(defaultMovieState);
  // Fetch movies from API
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        setLoading(true);

        const data = await moviesAPI.getAllMovies();

        const mappedData = Array.isArray(data) ? data.map(movie => ({
          id: movie.id,
          title: movie.title,
          duration: typeof movie.duration === 'string' 
            ? parseDuration(movie.duration) 
            : movie.duration || 0,
          status: movie.status,
          PosterURL: movie.PosterURL || '',
          description: movie.description || ''
        })) : [];

        setMovies(mappedData);
      } catch (error) {
        console.error('Error fetching movies:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, []);


  const filteredMovies = movies.filter(movie => 
    movie.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Open delete confirmation dialog
  const handleOpenDeleteDialog = (movie) => {
    setSelectedMovie(movie);
    setOpenDeleteDialog(true);
  };

  // Close delete confirmation dialog
  const handleCloseDeleteDialog = () => {
    setOpenDeleteDialog(false);
    setSelectedMovie(null);
  };

  // Delete movie via API
  const handleDeleteMovie = async () => {
    try {
      await moviesAPI.deleteMovie(selectedMovie.id);

      // Đảm bảo dữ liệu luôn nhất quán từ backend
      const updatedMovies = await moviesAPI.getAllMovies();
      setMovies(updatedMovies);

      handleCloseDeleteDialog();
    } catch (error) {
      console.error('Error deleting movie:', error);

    }
  };

  // Hiển thị màu sắc dựa trên trạng thái
  const getStatusColor = (status) => {
    return status === 'ACTIVE' ? 'success' : 'default';
  };

  // Hiển thị label trạng thái
  const getStatusLabel = (status) => {
    return status === 'ACTIVE' ? 'Hoạt động' : 'Không hoạt động';
  };

  // Handle poster dialog
  const handleOpenPosterDialog = (movie) => {
    setSelectedMovie(movie);
    setPosterPreview(movie.PosterURL || '');
    setOpenPosterDialog(true);
  };

  const handleClosePosterDialog = () => {
    setOpenPosterDialog(false);
    setSelectedMovie(null);
    setPosterFile(null);
    setPosterPreview('');
    setPosterUploadLoading(false);
  };

  // Xử lý thay đổi URL hình ảnh
  const handlePosterUrlChange = (event) => {
    const url = event.target.value;
    setPosterPreview(url);
    // Lưu URL trực tiếp vào state
    if (selectedMovie) {
      setSelectedMovie({
        ...selectedMovie,
        PosterURL: url
      });
    }
  };

  // Handle poster change từ file input
  const handlePosterChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setPosterFile(file);
      // Thay vì đọc file dưới dạng base64, tạo URL tạm thời cho file để xem trước
      const imageUrl = URL.createObjectURL(file);
      setPosterPreview(imageUrl);
    }
  };

  // Cập nhật poster từ URL trực tiếp
  const handleUploadPoster = async () => {
    if (!selectedMovie) return;

    setPosterUploadLoading(true);
    try {
      let posterUrl = selectedMovie.PosterURL;

      // Không cần chuyển đổi thành Base64 nữa, sử dụng URL trực tiếp
      // Nếu có file mới, người dùng nên cung cấp URL hình ảnh thay vì tải lên file
      
      const movieUpdate = {
        ...selectedMovie,
        title: selectedMovie.title,
        duration: selectedMovie.duration,
        status: selectedMovie.status || 'ACTIVE', // Đảm bảo có trường status theo MovieUpdateRequest
        PosterURL: posterUrl || '',
        description: selectedMovie.description || ''
        // Không cần posterFile nữa vì backend chỉ cần nhận URL
      };
      // Call API to update movie
      await moviesAPI.updateMovie(selectedMovie.id, movieUpdate);

      const updatedMovies = await moviesAPI.getAllMovies();
      setMovies(updatedMovies);

      // Success message và close dialog
      alert('Poster đã được cập nhật thành công!');
      handleClosePosterDialog();
    } catch (error) {
      console.error('Error uploading poster:', error);
      alert('Không thể cập nhật poster. Vui lòng thử lại sau.');
    } finally {
      setPosterUploadLoading(false);
    }
  };

  // Handle saving a new movie
  const handleSaveMovie = async () => {
    if (!movieForm.title || !movieForm.duration) {
      alert('Vui lòng nhập đầy đủ thông tin bắt buộc (Tên phim và Thời lượng)');
      return;
    }

    setSaving(true);
    try {

      // Đảm bảo duration là một số hợp lệ
      const durationMinutes = movieForm.duration ? parseInt(movieForm.duration, 10) : 0;
      
      // Xử lý trường hợp nếu parseInt trả về NaN
      const validDurationMinutes = isNaN(durationMinutes) ? 0 : durationMinutes;
      
      // Tính toán giờ và phút
      const hours = Math.floor(validDurationMinutes / 60);
      const minutes = validDurationMinutes % 60;
      
      // Tạo chuỗi ISO 8601 duration với định dạng chính xác
      // Chỉ thêm phần giờ nếu có giờ, chỉ thêm phần phút nếu có phút
      let isoDuration = 'PT';
      if (hours > 0) isoDuration += `${hours}H`;
      if (minutes > 0) isoDuration += `${minutes}M`;
      if (hours === 0 && minutes === 0) isoDuration += '0H'; // Đảm bảo ít nhất có PT0H nếu không có thời lượng
      
      const movieData = {
        title: movieForm.title,
        PosterURL: movieForm.PosterURL || '',
        duration: isoDuration,
        description: movieForm.description || ''
      };


      console.log('Sending to backend:', {
        title: movieData.title,
        duration: movieData.duration,
        posterUrlLength: movieData.PosterURL?.length || 0,
        description: movieData.description
      });

      // Gọi API tạo phim mới
      const newMovie = await moviesAPI.createMovie(movieData);


      // Đảm bảo dữ liệu luôn nhất quán từ backend
      const updatedMovies = await moviesAPI.getAllMovies();
      setMovies(updatedMovies);

      // Reset form 
      setMovieForm(defaultMovieState);
      setOpenAddDialog(false);
      alert('Thêm phim mới thành công!');
    } catch (error) {
      console.error('Error creating movie:', error);

      if (error.response) {
        // Server trả về response với status code ngoài phạm vi 2xx
        console.error('Error response:', error.response.data);
        console.error('Status code:', error.response.status);

        if (error.response.data && error.response.data.message) {
          alert(`Lỗi từ server: ${error.response.data.message}`);
        } else {
          alert(`Lỗi từ server: ${error.response.status} - ${error.response.statusText}`);
        }
      } else if (error.request) {
        // Request được gửi nhưng không nhận được response
        console.error('No response received:', error.request);
        alert('Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng và backend Spring Boot đã khởi động chưa.');
      } else {
        // Lỗi khi thiết lập request
        console.error('Request setup error:', error.message);
        alert(`Lỗi khi gửi yêu cầu: ${error.message}`);
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        {/* Tiêu đề */}
        <Grid container spacing={2} alignItems="center" sx={{ mb: 3 }}>
          <Grid item xs={12} md={8}>
            <Typography variant="h4" component="h1" gutterBottom>
              Quản lý phim
            </Typography>
          </Grid>
          <Grid item xs={12} md={4} sx={{ textAlign: { xs: 'left', md: 'right' } }}>
            <Button 
              variant="contained" 
              color="primary" 
              startIcon={<AddIcon />}
              onClick={() => setOpenAddDialog(true)}
            >
              Thêm phim mới
            </Button>
          </Grid>
        </Grid>

        {/* Ô tìm kiếm */}
        <Paper sx={{ p: 2, mb: 3 }}>
          <TextField
            fullWidth
            variant="outlined"
            placeholder="Tìm kiếm theo tên phim..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
            }}
          />
        </Paper>

        {/* Bảng danh sách phim */}
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Tên phim</TableCell>
                {/* Đã loại bỏ cột thể loại vì không có trong backend */}
                <TableCell>Thời lượng (phút)</TableCell>
                {/* Đã loại bỏ cột ngày chiếu vì không có trong backend */}
                <TableCell>Trạng thái</TableCell>
                <TableCell align="right">Hành động</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    <CircularProgress size={24} />
                  </TableCell>
                </TableRow>
              ) : filteredMovies.length > 0 ? (
                filteredMovies.map((movie) => (
                  <TableRow key={movie.id}>
                    <TableCell>{movie.id}</TableCell>
                    <TableCell>{movie.title}</TableCell>
                    {/* Đã loại bỏ cột genre vì không có trong backend */}
                    <TableCell>
                      {movie.duration ? 
                        // Nếu duration là chuỗi ISO-8601 từ Spring Boot (PT1H30M)
                        typeof movie.duration === 'string' && movie.duration.startsWith('PT') ? 
                          parseDuration(movie.duration) : 
                          movie.duration
                      : 'N/A'}
                    </TableCell>
                    {/* Đã loại bỏ cột releaseDate vì không có trong backend */}
                    <TableCell>
                      <Chip 
                        label={getStatusLabel(movie.status)}
                        color={getStatusColor(movie.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Tooltip title="Chức năng này đã được tích hợp vào quản lý phim">
                        <span>
                          <IconButton 
                            color="primary" 
                            disabled
                            title="Chỉnh sửa phim"
                          >
                            <EditIcon />
                          </IconButton>
                        </span>
                      </Tooltip>
                      <IconButton 
                        color="success" 
                        onClick={() => handleOpenPosterDialog(movie)}
                        title="Tải lên poster phim"
                      >
                        <ImageIcon />
                      </IconButton>
                      <IconButton 
                        color="error" 
                        onClick={() => handleOpenDeleteDialog(movie)}
                        title="Xóa phim"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={5} align="center">Không tìm thấy phim nào</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Dialog xác nhận xóa phim */}
        <Dialog
          open={openDeleteDialog}
          onClose={handleCloseDeleteDialog}
        >
          <DialogTitle>Xác nhận xóa phim</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Bạn có chắc chắn muốn xóa phim "{selectedMovie?.title}"? 
              Hành động này không thể hoàn tác.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDeleteDialog}>Hủy</Button>
            <Button onClick={handleDeleteMovie} color="error" autoFocus>
              Xóa
            </Button>
          </DialogActions>
        </Dialog>

        {/* Poster Upload Dialog */}
        <Dialog
          open={openPosterDialog}
          onClose={handleClosePosterDialog}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Tải lên poster phim</DialogTitle>
          <DialogContent>
            <DialogContentText sx={{ mb: 2 }}>
              Tải lên poster mới cho phim "{selectedMovie?.title}"
            </DialogContentText>

            {/* Current Poster Preview */}
            {selectedMovie?.PosterURL && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle2" gutterBottom>
                  Poster hiện tại:
                </Typography>
                <Box
                  component="img"
                  src={selectedMovie.PosterURL}
                  alt={selectedMovie.title}
                  sx={{
                    width: '100%',
                    maxHeight: 300,
                    objectFit: 'contain',
                    border: '1px solid #eee',
                  }}
                />
              </Box>
            )}

            {/* New Poster Upload */}
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle2" gutterBottom>
                Chọn hình ảnh mới:
              </Typography>
              <Button
                variant="outlined"
                component="label"
                startIcon={<UploadIcon />}
                sx={{ mb: 2 }}
              >
                Chọn file
                <input
                  type="file"
                  hidden
                  accept="image/*"
                  onChange={handlePosterChange}
                />
              </Button>

              {/* New Poster Preview */}
              {posterFile && posterPreview && (
                <Box sx={{ mt: 2 }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Preview:
                  </Typography>
                  <Box
                    component="img"
                    src={posterPreview}
                    alt="Preview"
                    sx={{
                      width: '100%',
                      maxHeight: 300,
                      objectFit: 'contain',
                      border: '1px solid #eee',
                    }}
                  />
                </Box>
              )}
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClosePosterDialog}>Hủy</Button>
            <Button 
              onClick={handleUploadPoster} 
              color="primary" 
              disabled={!posterFile || posterUploadLoading}
              variant="contained"
            >
              {posterUploadLoading ? <CircularProgress size={24} /> : 'Tải lên'}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Dialog thêm phim mới */}
        <Dialog
          open={openAddDialog}
          onClose={() => setOpenAddDialog(false)}
          maxWidth="md"
          fullWidth
        >
          <DialogTitle>Thêm phim mới</DialogTitle>
          <DialogContent>
            <Box component="form" sx={{ mt: 2 }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Tên phim"
                    name="title"
                    value={movieForm.title}
                    onChange={(e) => setMovieForm({...movieForm, title: e.target.value})}
                    required
                  />
                </Grid>
                {/* Đã loại bỏ trường thể loại vì không có trong backend */}
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Thời lượng (phút)"
                    name="duration"
                    type="number"
                    value={movieForm.duration}
                    onChange={(e) => setMovieForm({...movieForm, duration: e.target.value})}
                    required
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Mô tả phim"
                    name="description"
                    multiline
                    rows={3}
                    value={movieForm.description}
                    onChange={(e) => setMovieForm({...movieForm, description: e.target.value})}
                  />
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    Nhập URL hình ảnh phim:
                  </Typography>
                  
                  <TextField
                    fullWidth
                    label="URL hình ảnh poster"
                    name="posterUrl"
                    placeholder="https://image.tmdb.org/t/p/w500/..."
                    value={movieForm.PosterURL || ''}
                    onChange={(e) => setMovieForm({...movieForm, PosterURL: e.target.value})}
                    helperText="Nhập URL hình ảnh từ internet (ví dụ: https://image.tmdb.org/t/p/w500/...)"
                    sx={{ mb: 2 }}
                  />
                  {movieForm.PosterURL && (
                    <Box sx={{ mt: 1, textAlign: 'center' }}>
                      <Typography variant="caption" display="block" gutterBottom>
                        Xem trước:
                      </Typography>
                      <Box 
                        component="img"
                        src={movieForm.PosterURL}
                        alt="Movie poster preview"
                        sx={{
                          height: 150,
                          maxWidth: '100%',
                          objectFit: 'contain',
                          border: '1px solid #eee',
                        }}
                      />
                    </Box>
                  )}
                </Grid>
{/* Đã loại bỏ select box Status khỏi form thêm phim vì MovieCreateRequest không hỗ trợ */}
              </Grid>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenAddDialog(false)}>Huỷ</Button>
            <Button 
              variant="contained" 
              color="primary"
              disabled={saving}
              onClick={handleSaveMovie}
            >
              {saving ? <CircularProgress size={24} /> : 'Lưu'}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </Layout>
  );
};

export default MoviesManagement;